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

import engine.replay.FileGameLog;
import engine.replay.GameLog;
import engine.robot.HumanRobot;
import game.*;
import view.GUI;

public class RunGame
{
    Config config;
    Engine engine;
    GameState game;
    
    public RunGame(Config config) {
        this.config = config;        
    }
    
    public GameResult go()
    { 
        GameLog log = null;
        if (config.replayLog != null) {
            log = new FileGameLog(config.replayLog);
        }
        
        game = new GameState(config.game, null);

        GUI gui;
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
        
        Bot[] robots = new Bot[2];
        robots[0] = setupRobot(config.bot1Init, gui);
        robots[1] = setupRobot(config.bot2Init, gui);
                
        //start the engine
        this.engine = new Engine(game, robots, gui, config.botCommandTimeoutMillis);
        
        if (log != null) {
            log.start(config);
        }
        
        for (int i = 1 ; i <= 2 ; ++i) {
            robots[i - 1].init(config.botCommandTimeoutMillis);
        }
        
        //send the bots the info they need to start
        engine.distributeStartingRegions(); //decide the players' starting regions
        
        //play the game
        while(!game.isDone())
        {
            if (log != null) {
                log.logComment(0, "Round " + game.getRoundNumber());
            }
            engine.playRound();
        }

        GameResult result = finish(game.getMap(), robots);
        
        if (log != null) {
            log.finish(result);
        }
        
        return result;
    }

    static Bot constructBot(Class<?> botClass) {        
        Object botObj;
        try {
            botObj = botClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e); 
        }
        if (!(Bot.class.isAssignableFrom(botObj.getClass()))) {
            throw new RuntimeException("Constructed bot does not implement " + Bot.class.getName() + " interface, bot class instantiated: " + botClass.getName());
        }
        Bot bot = (Bot) botObj;
        return bot;
    }
    
    static Bot constructBot(String botFQCN) {
        Class<?> botClass;
        try {
            try {
                botClass = Class.forName(botFQCN);
            } catch (ClassNotFoundException e) {
                botClass = Class.forName("bots." + botFQCN);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to locate bot class: " + botFQCN, e);
        }
        return constructBot(botClass);
    }

    private Bot setupRobot(String botInit, GUI gui) {
        if (botInit.startsWith("internal:")) {
            String botFQCN = botInit.substring(9);
            return constructBot(botFQCN);
        }
        if (botInit.startsWith("human")) {
            config.visualize = true;
            return new HumanRobot(gui);
        }
        throw new RuntimeException("Invalid init string: " + botInit);
    }

    private GameResult finish(GameMap map, Bot[] bots)
    {
        return this.saveGame(map);        
    }
    
    public GameResult saveGame(GameMap map) {

        GameResult result = new GameResult();
        
        result.config = config;
        result.player1Regions = map.numberRegionsOwned(1);
        result.player1Armies = map.numberArmiesOwned(1);
        result.player2Regions = map.numberRegionsOwned(2);
        result.player2Armies = map.numberArmiesOwned(2);

        result.winner = game.winningPlayer();
        result.round = game.getRoundNumber();
        
        return result;
    }
}
