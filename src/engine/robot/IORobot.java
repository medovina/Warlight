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

package engine.robot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import engine.Robot;
import engine.io.handler.Handler;
import engine.io.handler.IHandler;
import engine.replay.GameLog;
import game.*;
import game.move.*;
import game.world.MapRegion;

public class IORobot implements Robot
{
    IHandler handler;

    int player;
    
    int errorCounter;
    
    final int maxErrors = 2;

    private GameLog log;

    private RobotConfig config;

    RobotParser parser = new RobotParser();
    
    public IORobot(IHandler handler) throws IOException
    {
        this.handler = handler;
        errorCounter = 0;
    }
    
    public IORobot(int player, OutputStream input, boolean inputAutoFlush,
                   InputStream output, InputStream error) throws IOException
    {
        this.player = player;
        handler = new Handler("PLR" + player + "-Robot", input, inputAutoFlush, output, error);
        errorCounter = 0;
    }
    
    @Override
    public void setup(RobotConfig config) {
        this.config = config;
        handler.setGameLog(config.gameLog, config.player, config.logToConsole);
        handler.writeLine("init " + config.timeoutMillis);
    }
        
    @Override
    public MapRegion getStartingRegion(GameState state)
    {
        String output = "pick_starting_region";
        for(Region region : state.getPickableRegions())
            output = output.concat(" " + region.getId());
        
        handler.writeLine(output);
        String line = handler.readLine(config.timeoutMillis);
        return parser.parseStartingRegion(line);
    }

    private List<PlaceArmiesMove> placeArmiesMoves(String input) {
        ArrayList<PlaceArmiesMove> moves = new ArrayList<PlaceArmiesMove>();
        
        for (Move move : parser.parseMoves(input, player))
                if (move instanceof PlaceArmiesMove)
                        moves.add((PlaceArmiesMove) move);
                else
                        System.err.println("INVALID MOVE: " + move);
        
        return moves;
  }
    
    @Override
    public List<PlaceArmiesMove> getPlaceArmiesMoves(GameState state)
    {
        return placeArmiesMoves(getMoves("place_armies"));
    }

    private List<AttackTransferMove> attackTransferMoves(String input) {
        ArrayList<AttackTransferMove> moves = new ArrayList<AttackTransferMove>();
        
        for (Move move : parser.parseMoves(input, player))
                if (move instanceof AttackTransferMove)
                        moves.add((AttackTransferMove) move);
                else
                        System.err.println("INVALID MOVE: " + move);
        
        return moves;
  }
    
    @Override
    public List<AttackTransferMove> getAttackTransferMoves(GameState state)
    {
        return attackTransferMoves(getMoves("attack/transfer"));
    }
    
    private String getMoves(String moveType)
    {
        String line = "";
        if(errorCounter < maxErrors)
        {
            handler.writeLine("go " + moveType);
            
            long timeStart = System.currentTimeMillis();
            while(line != null && line.length() < 1)
            {
                long timeNow = System.currentTimeMillis();
                long timeElapsed = timeNow - timeStart;
                line = handler.readLine(config.timeoutMillis);
                if(timeElapsed >= config.timeoutMillis)
                    break;
            }
            if(line == null) {
                errorCounter++;
                return "";
            }
            if(line.equals("No moves"))
                return "";
        }
        else
        {
            if (log != null) {
                log.logComment(0, "go " + moveType + "\n");
                log.logComment(0, "Maximum number of idle moves returned: skipping move (let bot return 'No moves' instead of nothing)");
            }
        }
        return line;
    }
    
    @Override
    public void writeInfo(String info){
        handler.writeLine(info);
    }

    public boolean isRunning() {
        return handler.isRunning();
    }
    
    public void finish() {
        handler.stop();
    }

    @Override
    public int getRobotPlayer() {
        if (config == null) return 0;
        return config.player;
    }

    @Override
    public String getRobotPlayerName() {
        if (config == null) return "N/A";
        return config.playerName;
    }

}
