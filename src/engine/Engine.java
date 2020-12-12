// Copyright 2014 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//    
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package engine;

import game.*;
import game.move.*;
import view.GUI;

public class Engine {
    Config config;
    Game game;
    Agent[] agents;
    GUI gui;

    public Engine(Config config) {
        this.config = config;        
    }
    
    boolean timeout(Agent agent, long elapsed) {
        if (!(agent instanceof HumanAgent) &&
                config.timeoutMillis > 0 && elapsed > config.timeoutMillis + 150 /* grace period */) {
            System.err.format("agent failed to respond in time!  timeout = %d, elapsed = %d\n",
                config.timeoutMillis, elapsed);
            return true; 
        }
        return false;
    }

    static Agent constructAgent(Class<?> agentClass) {        
        Object agentObj;
        try {
            agentObj = agentClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e); 
        }
        if (!(Agent.class.isAssignableFrom(agentObj.getClass()))) {
            throw new RuntimeException(
                "Constructed agent does not implement " + Agent.class.getName() + " interface, " +
                "agent class instantiated: " + agentClass.getName());
        }
        Agent agent = (Agent) agentObj;
        return agent;
    }
    
    static Agent constructAgent(String agentFQCN) {
        Class<?> agentClass;
        try {
            try {
                agentClass = Class.forName(agentFQCN);
            } catch (ClassNotFoundException e) {
                agentClass = Class.forName("agents." + agentFQCN);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to locate agent class: " + agentFQCN, e);
        }
        return constructAgent(agentClass);
    }

    private Agent setupAgent(String agentInit, GUI gui) {
        if (agentInit.startsWith("internal:")) {
            String agentFQCN = agentInit.substring(9);
            return constructAgent(agentFQCN);
        }
        if (agentInit.startsWith("human")) {
            config.visualize = true;
            return new HumanAgent(gui);
        }
        throw new RuntimeException("Invalid init string: " + agentInit);
    }

    public GameResult run()
    { 
        game = new Game(config.gameConfig);

        if (config.visualize) {
            gui = new GUI(game, config);
            game.setGUI(gui);
        } else gui = null;

        int players = config.numPlayers();
        
        agents = new Agent[players + 1];
        for (int p = 1 ; p <= players ; ++p) {
            agents[p] = setupAgent(config.agentInit(p), gui);
            agents[p].init(config.timeoutMillis);
        }

        int[] totalMoves = new int[players + 1];
        long[] totalTime = new long[players + 1];

        while(!game.isDone()) {
            int player = game.currentPlayer();
            Agent agent = agents[player];

            long start = System.currentTimeMillis();
            Move move = agent.getMove(game.clone());
            long elapsed = System.currentTimeMillis() - start;
            totalMoves[player] += 1;
            totalTime[player] += elapsed;

            if (timeout(agent, elapsed))
                game.pass();
            else game.move(move);
        }

        for (int p = 1 ; p <= config.numPlayers() ; ++p)
            agents[p].terminate();

        return new GameResult(config, game, totalMoves, totalTime);
    }
}
