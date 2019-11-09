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

package conquest.bot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

import conquest.engine.io.BotStreamReader;
import conquest.game.*;
import conquest.game.move.AttackTransferMove;
import conquest.game.move.PlaceArmiesMove;
import conquest.game.world.*;

public class BotParser extends Thread {
    
    final BotStreamReader input;    

    final PrintStream output;
    
    final Bot bot;    
    
    GameState currentState;

    FileBotLog log;
    
    public BotParser(Bot bot) {
        this(bot, System.in, System.out);
    }
    
    public FileBotLog setLogFile(File file) {
        log = new FileBotLog(file);
        log.start();
        log("Logging started...");
        return log;
    }
    
    public BotParser(Bot bot, InputStream input, PrintStream output)
    {
        super("BotParser[" + bot.getClass().getName() + "]");
        this.input = new BotStreamReader(input);
        this.output = output;
        
        this.bot = bot;
        this.currentState = new GameState(null, new GameMap(), null, new ArrayList<Region>());
    }
    
    public static Bot constructBot(String botFQCN) {
        Class<?> botClass;
        try {
            botClass = Class.forName(botFQCN);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to locate bot class: " + botFQCN, e);
        }
        return constructBot(botClass);
    }
    
    public static Bot constructBot(Class<?> botClass) {        
        Object botObj;
        try {
            botObj = botClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to construct Bot instance, tried to invoke parameterless constructor from class: " + botClass.getName()); 
        }
        if (!(Bot.class.isAssignableFrom(botObj.getClass()))) {
            throw new RuntimeException("Constructed bot does not implement " + Bot.class.getName() + " interface, bot class instantiated: " + botClass.getName());
        }
        Bot bot = (Bot) botObj;
        return bot;
    }
    
    public static BotParser runInternal(String botFQCN, InputStream input, PrintStream output) {
        Bot bot = constructBot(botFQCN);
        return runInternal(bot, input, output);
    }
    
    public static BotParser runInternal(Bot bot, InputStream input, PrintStream output) {
        BotParser parser = new BotParser(bot, input, output);
        parser.start();
        return parser;
    }
    
    private void log(String msg) {
        if (log != null) {
            log.log(msg);
        }
    }
    
    public Bot getBot() {
        return bot;
    }

    //regions from which a player is able to pick his preferred starting regions
    void setPickableStartingRegions(GameState state, String[] mapInput)
    {
        ArrayList<Region> regions = new ArrayList<Region>();
        
        for(int i=2; i<mapInput.length; i++)
        {
            int regionId;
            try {
                regionId = Integer.parseInt(mapInput[i]);
                Region pickableRegion = Region.forId(regionId);
                regions.add(pickableRegion);
            }
            catch(Exception e) {
                System.err.println("Unable to parse pickable regions " + e.getMessage());
            }
        }

        state.setPickableRegions(regions);
    }

    //initial map is given to the bot with all the information except for player and armies info
    void setupMap(GameState state, String[] mapInput)
    {
        GameMap map = state.getMap();

        if(mapInput[1].equals("continents"))
        {
            for(int i=2; i<mapInput.length; i++)
            {
                try {
                    int continentId = Integer.parseInt(mapInput[i]);
                    i++;
                    int reward = Integer.parseInt(mapInput[i]);
                    map.add(new ContinentData(Continent.forId(continentId), continentId, reward));
                }
                catch(Exception e) {
                    System.err.println("Unable to parse Continents");
                }
            }
        }
        else if(mapInput[1].equals("regions"))
        {
            for(int i=2; i<mapInput.length; i++)
            {
                try {
                    int regionId = Integer.parseInt(mapInput[i]);
                    i++;
                    int continentId = Integer.parseInt(mapInput[i]);
                    ContinentData continent = map.getContinent(continentId);
                    map.add(new RegionData(Region.forId(regionId), regionId, continent));
                }
                catch(Exception e) {
                    System.err.println("Unable to parse Regions " + e.getMessage());
                }
            }
        }
        else if(mapInput[1].equals("neighbors"))
        {
            for(int i=2; i<mapInput.length; i++)
            {
                try {
                    RegionData region = map.getRegion(Integer.parseInt(mapInput[i]));
                    i++;
                    String[] neighborIds = mapInput[i].split(",");
                    for(int j=0; j<neighborIds.length; j++)
                    {
                        RegionData neighbor = map.getRegion(Integer.parseInt(neighborIds[j]));
                        region.addNeighbor(neighbor);
                    }
                }
                catch(Exception e) {
                    System.err.println("Unable to parse Neighbors " + e.getMessage());
                }
            }
        }
    }

    //visible regions are given to the bot with player and armies info
    void updateMap(GameState state, String[] mapInput)
    {
        for(int i=1; i<mapInput.length; i++)
        {
            try {
                RegionData region = state.getMap().getRegion(Integer.parseInt(mapInput[i]));
                int owner = Integer.parseInt(mapInput[i+1]);
                int armies = Integer.parseInt(mapInput[i+2]);
                
                region.setOwner(owner);
                region.setArmies(armies);
                i += 2;
            }
            catch(Exception e) {
                System.err.println("Unable to parse Map Update " + e.getMessage());
            }
        }
    }

    public void nextRound(GameState state) {
        state.setRoundNumber(state.getRoundNumber() + 1);
    }
    
    @Override
    public void run()
    {
        log("Bot thread started.");
        while (true) {
            String line;
            log("Reading input...");
            try {
                line = input.readLine();
            } catch (IOException e) {
                log("FAILED TO READ NEXT LINE: " + e.getMessage());
                if (log != null) {
                    log.finish();
                }
                throw new RuntimeException("Failed to read next line.", e);
            }
            if (line == null) {
                log("End of INPUT stream reached...");
                log("Terminating the thread.");
                if (log != null) {
                    log.finish();
                }
                return;
            }
            line = line.trim();
            if(line.length() == 0) { continue; }
            log("IN : " + line);
            String[] parts = line.split(" ");
            if(parts[0].equals("pick_starting_region")) {
                //pick a region you want to start with
                currentState.setPhase(Phase.STARTING_REGIONS);
                setPickableStartingRegions(currentState, parts);
                Region startingRegion = bot.chooseRegion(currentState, Long.valueOf(parts[1]));
                String output = startingRegion.id + "";
                
                log("OUT: " + output);
                this.output.println(output);
            } else if(parts.length == 3 && parts[0].equals("go")) {
                //we need to do a move
                String output = "";
                if(parts[1].equals("place_armies")) 
                {
                    currentState.setPhase(Phase.PLACE_ARMIES);
                    List<PlaceArmiesMove> placeArmiesMoves = bot.placeArmies(currentState, Long.valueOf(parts[2]));
                    for(PlaceArmiesMove move : placeArmiesMoves)
                        output = output.concat(move.getString() + ",");
                } 
                else if(parts[1].equals("attack/transfer")) 
                {
                    currentState.setPhase(Phase.ATTACK_TRANSFER);
                    List<AttackTransferMove> attackTransferMoves = bot.moveArmies(currentState, Long.valueOf(parts[2]));
                    for(AttackTransferMove move : attackTransferMoves)
                        output = output.concat(move.getString() + ",");
                }
                if(output.length() == 0) output = "No moves";
                log("OUT: " + output);
                this.output.println(output);
            } else if(parts.length == 3 && parts[0].equals("settings")) {
                if (parts[1].equals("your_player_number"))
                    currentState.setTurn(Integer.parseInt(parts[2]));
            } else if(parts[0].equals("setup_map")) {
                //initial full map is given
                setupMap(currentState, parts);
            } else if(parts[0].equals("update_map")) {
                //all visible regions are given
                updateMap(currentState, parts);
            } else if (parts[0].equals("next_round"))
                nextRound(currentState);
            else {
                log("Unable to parse line: " + line);
            }
        }
        // COULD NOT REACH HERE...
    }

}
