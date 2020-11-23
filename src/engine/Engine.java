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

import java.util.ArrayList;
import java.util.List;

import game.*;
import game.move.AttackTransfer;
import game.move.PlaceArmies;
import view.GUI;

public class Engine {
    Config config;
    Game game;
    Agent[] agents;
    GUI gui;

    public Engine(Config config) {
        this.config = config;        
    }
    
    Agent agent(int i) {
        return agents[i - 1];
    }

    boolean timeout(Agent agent, long start) {
        long elapsed = System.currentTimeMillis() - start;
        if (!(agent instanceof HumanAgent) &&
                config.timeoutMillis > 0 && elapsed > config.timeoutMillis + 150 /* grace period */) {
            System.err.format("agent failed to respond in time!  timeout = %d, elapsed = %d\n",
                config.timeoutMillis, elapsed);
            return true; 
        }
        return false;
    }

    void playRound()
    {
        if (gui != null) {
            gui.newRound(game.getRoundNumber());
            gui.updateRegions(game.getRegions());
        }
        
        for (int i = 1 ; i <= 2 ; ++i) {
            long start = System.currentTimeMillis();
            List<PlaceArmies> placeMoves = agent(i).placeArmies(game);
            if (timeout(agent(i), start)) {
                System.err.println("agent failed to return place armies moves in time!");
                placeMoves = new ArrayList<PlaceArmies>();
            }
            
            List<PlaceArmies> legalMoves = game.placeArmies(placeMoves);
    
            if (gui != null && !(agent(i) instanceof HumanAgent))
                gui.placeArmies(i, game.getRegions(), legalMoves);
            
            start = System.currentTimeMillis();
            List<AttackTransfer> moves = agent(i).moveArmies(game);
            if (timeout(agent(i), start)) {
                System.err.println("agent failed to return attack transfer moves in time!");
                moves = new ArrayList<AttackTransfer>();
            }
            
            game.attackTransfer(moves);
            
            if (game.isDone())
                break;
        }
        
        if (gui != null) {
            gui.updateMap();
        }
    }
    
    void distributeStartingRegions()
    {
        if (game.getPhase() == Phase.STARTING_REGIONS) {
            if (gui != null) {
                gui.showPickableRegions();
            }
        
            for (int i = 1 ; i <= game.numStartingRegions() ; ++i)
                for (int p = 1 ; p <= 2 ; ++p) {
                    long start = System.currentTimeMillis();
                    Region region = agent(p).chooseRegion(game);
                    if (timeout(agent(p), start)) {
                        System.err.println("agent failed to return starting region in time!");
                        region = null;
                    }
                    
                    if (region == null || !game.pickableRegions.contains(region)) {
                        System.err.println("invalid starting region; choosing one at random");
                        region = game.getRandomStartingRegion(p);
                    }
            
                    game.chooseRegion(region);
                }
            }
        
        if (gui != null) {
            gui.regionsChosen(game.getRegions());
        }
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

    private GameResult finish()
    {
        GameResult result = new GameResult();
        
        result.config = config;
        result.player1Regions = game.numberRegionsOwned(1);
        result.player1Armies = game.numberArmiesOwned(1);
        result.player2Regions = game.numberRegionsOwned(2);
        result.player2Armies = game.numberArmiesOwned(2);

        result.winner = game.winningPlayer();
        result.round = game.getRoundNumber();
        
        return result;
    }

    public GameResult go()
    { 
        game = new Game(config.gameConfig);

        if (config.visualize) {
            gui = new GUI(game, config);
            if (config.visualizeContinual != null) {
                gui.setContinual(config.visualizeContinual);
            }
            if (config.visualizeContinualFrameTimeMillis != null) {
                gui.setContinualFrameTime(config.visualizeContinualFrameTimeMillis);
            }
            game.setGUI(gui);
        } else gui = null;
        
        agents = new Agent[2];
        agents[0] = setupAgent(config.agent1Init, gui);
        agents[1] = setupAgent(config.agent2Init, gui);
                
        for (int i = 0 ; i < 2 ; ++i) {
            agents[i].init(config.timeoutMillis);
        }
        
        distributeStartingRegions();
        
        while(!game.isDone())
        {
            playRound();
        }

        return finish();
    }
}
