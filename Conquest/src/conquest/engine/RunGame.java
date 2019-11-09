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

package conquest.engine;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import conquest.engine.Robot.RobotConfig;
import conquest.engine.replay.FileGameLog;
import conquest.engine.replay.GameLog;
import conquest.engine.replay.ReplayHandler;
import conquest.engine.robot.HumanRobot;
import conquest.engine.robot.IORobot;
import conquest.engine.robot.InternalRobot;
import conquest.engine.robot.ProcessRobot;
import conquest.game.*;
import conquest.view.GUI;

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
            
            System.out.println("starting game " + config.gameId);
            
            String[] playerNames = new String[2];
            Robot[] robots = new Robot[2];
            
            robots[0] = setupRobot(1, config.bot1Init);
            robots[1] = setupRobot(2, config.bot2Init);
                    
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
            gui = new GUI(game);
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
        
        if (gui != null) {
            gui.setPlayerNames(robots[0].getRobotPlayerName(), robots[1].getRobotPlayerName());
        }        
                
        //send the bots the info they need to start
        for (int i = 0 ; i < 2 ; ++i) {
            robots[i].writeInfo("settings your_player_number " + (i + 1));
            sendSetupMapInfo(robots[i], game.getMap());
        }
        engine.distributeStartingRegions(); //decide the players' starting regions
        engine.sendAllInfo();
        engine.nextRound();   // advance to round 1
        
        //play the game
        while(!game.isDone())
        {
            System.out.println("Round " + game.getRoundNumber());
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

    private Robot setupRobot(int player, String botInit) throws IOException {
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
            return new InternalRobot(player, botFQCN);
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

    private void sendSetupMapInfo(Robot bot, GameMap initMap)
    {
        bot.writeInfo(getSuperRegionsString(initMap));
        bot.writeInfo(getRegionsString(initMap));
        bot.writeInfo(getNeighborsString(initMap));
    }
    
    private String getSuperRegionsString(GameMap map)
    {
        String superRegionsString = "setup_map continents";
        for(Continent superRegion : map.continents)
        {
            int id = superRegion.getId();
            int reward = superRegion.getArmiesReward();
            superRegionsString = superRegionsString.concat(" " + id + " " + reward);
        }
        return superRegionsString;
    }
    
    private String getRegionsString(GameMap map)
    {
        String regionsString = "setup_map regions";
        for(Region region : map.regions)
        {
            int id = region.getId();
            int superRegionId = region.getContinent().getId();
            regionsString = regionsString.concat(" " + id + " " + superRegionId);
        }
        return regionsString;
    }
    
    private String getNeighborsString(GameMap map)
    {
        String neighborsString = "setup_map neighbors";
        ArrayList<Point> doneList = new ArrayList<Point>();
        for(Region region : map.regions)
        {
            int id = region.getId();
            String neighbors = "";
            for(Region neighbor : region.getNeighbors())
            {
                if(checkDoneList(doneList, id, neighbor.getId()))
                {
                    neighbors = neighbors.concat("," + neighbor.getId());
                    doneList.add(new Point(id,neighbor.getId()));
                }
            }
            if(neighbors.length() != 0)
            {
                neighbors = neighbors.replaceFirst(","," ");
                neighborsString = neighborsString.concat(" " + id + neighbors);
            }
        }
        return neighborsString;
    }
    
    private Boolean checkDoneList(ArrayList<Point> doneList, int regionId, int neighborId)
    {
        for(Point p : doneList)
            if((p.x == regionId && p.y == neighborId) || (p.x == neighborId && p.y == regionId))
                return false;
        return true;
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
    
    public static void main(String args[])
    {    
        Config config = new Config();
        
        config.bot1Init = "internal:conquest.bot.BotStarter";
        //config.bot1Init = "human";
        config.bot2Init = "internal:conquest.bot.BotStarter";
        //config.bot2Init = "process:java -cp bin conquest.bot.BotStarter";
        //config.bot2Init = "dir;process:c:/my_bot/;java -cp bin conquest.bot.BotStarter";
        
        config.botCommandTimeoutMillis = 24*60*60*1000;
        
        config.game.maxGameRounds = 100;
        
        // visualize the map, if turned off, the simulation would run headless 
        config.visualize = true;
        
        config.replayLog = new File("./replay.log");
        
        RunGame run = new RunGame(config);
        run.go();
        
        System.exit(0);
    }
    

}
