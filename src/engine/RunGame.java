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
import view.GUI;

public class RunGame
{
    Config config;
    Engine engine;
    Game game;
    
    public RunGame(Config config) {
        this.config = config;        
    }
    
    public GameResult go()
    { 
        game = new Game(config.game);

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
        
        Bot[] bots = new Bot[2];
        bots[0] = setupBot(config.bot1Init, gui);
        bots[1] = setupBot(config.bot2Init, gui);
                
        for (int i = 0 ; i < 2 ; ++i) {
            bots[i].init(config.botCommandTimeoutMillis);
        }
        
        engine = new Engine(game, bots, gui, config.botCommandTimeoutMillis);
        
        engine.distributeStartingRegions();
        
        while(!game.isDone())
        {
            engine.playRound();
        }

        return finish();
    }

    static Bot constructBot(Class<?> botClass) {        
        Object botObj;
        try {
            botObj = botClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e); 
        }
        if (!(Bot.class.isAssignableFrom(botObj.getClass()))) {
            throw new RuntimeException(
                "Constructed bot does not implement " + Bot.class.getName() + " interface, " +
                "bot class instantiated: " + botClass.getName());
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

    private Bot setupBot(String botInit, GUI gui) {
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
}
