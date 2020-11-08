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

import java.io.File;
import java.io.IOException;

import bot.BotLoader;
import engine.Robot.RobotConfig;
import engine.replay.FileGameLog;
import engine.replay.GameLog;
import engine.replay.ReplayHandler;
import engine.robot.HumanRobot;
import engine.robot.IORobot;
import engine.robot.InternalRobot;
import engine.robot.ProcessRobot;
import game.*;
import view.GUI;

public class RunGame
{
    Config config;
    
    Engine engine;
    GameState game;
    
    public RunGame(Config config)
    {
        this.config = config;        
    }
    
    public GameResult goReplay(File replayFile) {
        try {
            System.out.println("starting replay " + replayFile.getAbsolutePath());
            
            ReplayHandler replay = new ReplayHandler(replayFile);
            Config replayConfig = replay.getConfig();
            
            config.player1Name = replayConfig.player1Name;
            config.player2Name = replayConfig.player2Name;
            config.botCommandTimeoutMillis = replayConfig.botCommandTimeoutMillis;
            config.game = replayConfig.game;
            
            String[] playerNames = new String[2];
            Robot[] robots = new Robot[2];
            
            robots[0] = new IORobot(replay);
            robots[1] = new IORobot(replay);
                    
            playerNames[0] = config.player1Name;
            playerNames[1] = config.player2Name;
            
            return go(null, playerNames, robots);
        } catch (Exception e) {
            throw new RuntimeException("Failed to replay the game.", e);
        }
    }

    public GameResult go()
    { 
        try {
            GameLog log = null;
            if (config.replayLog != null) {
                log = new FileGameLog(config.replayLog);
            }
            
            String[] playerNames = new String[2];
            Robot[] robots = new Robot[2];
            
            robots[0] = setupRobot(1, config.botLoader, config.bot1Init);
            robots[1] = setupRobot(2, config.botLoader, config.bot2Init);
                    
            playerNames[0] = config.player1Name;
            playerNames[1] = config.player2Name;
                        
            return go(log, playerNames, robots);
        } catch (Exception e) {
            throw new RuntimeException("Failed to run/finish the game.", e);
        }
    }

    private GameResult go(GameLog log, String[] playerNames, Robot[] robots) throws InterruptedException {
        game = new GameState(config.game, null, playerNames, null);

        GUI gui;
        if (config.visualize) {
            gui = new GUI(game, robots);
            if (config.visualizeContinual != null) {
                gui.setContinual(config.visualizeContinual);
            }
            if (config.visualizeContinualFrameTimeMillis != null) {
                gui.setContinualFrameTime(config.visualizeContinualFrameTimeMillis);
            }
            game.setGUI(gui);
        } else gui = null;
        
        //start the engine
        this.engine = new Engine(game, robots, gui, config.botCommandTimeoutMillis);
        
        if (log != null) {
            log.start(config);
        }
        
        for (int i = 1 ; i <= 2 ; ++i) {
            RobotConfig robotCfg =
                    new RobotConfig(i, playerNames[i - 1], i == 1 ? Team.PLAYER_1 : Team.PLAYER_2,
                            config.botCommandTimeoutMillis, log, config.logToConsole, gui);
            robots[i - 1].setup(robotCfg);
        }
        
        //send the bots the info they need to start
        for (int i = 0 ; i < 2 ; ++i)
            robots[i].writeInfo("settings your_player_number " + (i + 1));
        engine.distributeStartingRegions(); //decide the players' starting regions
        engine.sendAllInfo();
        engine.nextRound();   // advance to round 1
        
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

    private Robot setupRobot(int player, BotLoader botLoader, String botInit) throws IOException {
        if (botInit.startsWith("dir;process:")) {
            String cmd = botInit.substring(12);
            int semicolon = cmd.indexOf(";");
            if (semicolon < 0) throw new RuntimeException(
                "Invalid bot torrent (does not contain ';' separating directory and command): " + botInit);
            String dir = cmd.substring(0, semicolon);
            String process = cmd.substring(semicolon+1);            
            return new ProcessRobot(player, dir, process);
        }
        if (botInit.startsWith("process:")) {
            String cmd = botInit.substring(8);
            return new ProcessRobot(player, cmd);
        }
        if (botInit.startsWith("internal:")) {
            String botFQCN = botInit.substring(9);
            return new InternalRobot(player, botLoader, botFQCN);
        }
        if (botInit.startsWith("human")) {
            config.visualize = true;
            return new HumanRobot();
        }
        throw new RuntimeException("Invalid init string for player '" + player +
                "', must start either with 'process:' or 'internal:' or 'human', passed value was: " + botInit);
    }

    private GameResult finish(GameMap map, Robot[] bots) throws InterruptedException
    {
        System.out.println("GAME FINISHED: stopping bots...");
        for (Robot r : bots)
            try {
                r.finish();
            } catch (Exception e) { }
        
        return this.saveGame(map);        
    }
    
    public GameResult saveGame(GameMap map) {

        GameResult result = new GameResult();
        
        result.config = config;
        result.player1Regions = map.numberRegionsOwned(1);
        result.player1Armies = map.numberArmiesOwned(1);
        result.player2Regions = map.numberRegionsOwned(2);
        result.player2Armies = map.numberArmiesOwned(2);

        switch (game.winningPlayer()) {
        case 1: 
            result.winner = Team.PLAYER_1;
            break;
        case 2:
            result.winner = Team.PLAYER_2;
            break;
        default:
            result.winner = null;
        }
        
        result.round = game.getRoundNumber()-1;
        
        System.out.println(result.getHumanString());
        
        return result;
    }
}
