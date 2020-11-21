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
import game.move.AttackTransferMove;
import game.move.PlaceArmiesMove;
import view.GUI;

public class Engine {
    Game game;
    
    private Bot[] bots;
    private long timeoutMillis;
    private GUI gui;
    
    public Engine(Game game, Bot[] robots, GUI gui, long timeoutMillis)
    {
        this.game = game;
        
        this.gui = gui;
        
        this.bots = robots;
        this.timeoutMillis = timeoutMillis;        
    }
    
    Bot bot(int i) {
        return bots[i - 1];
    }

    boolean timeout(Bot bot, long start) {
        long elapsed = System.currentTimeMillis() - start;
        if (!(bot instanceof HumanRobot) &&
                timeoutMillis > 0 && elapsed > timeoutMillis + 150 /* grace period */) {
            System.err.format("bot failed to respond in time!  timeout = %d, elapsed = %d\n",
                timeoutMillis, elapsed);
            return true; 
        }
        return false;
    }

    public void playRound()
    {
        if (gui != null) {
            gui.newRound(game.getRoundNumber());
            gui.updateRegions(game.getRegions());
        }
        
        for (int i = 1 ; i <= 2 ; ++i) {
            long start = System.currentTimeMillis();
            List<PlaceArmiesMove> placeMoves = bot(i).placeArmies(game);
            if (timeout(bot(i), start)) {
                System.err.println("bot failed to return place armies moves in time!");
                placeMoves = new ArrayList<PlaceArmiesMove>();
            }
            
            game.placeArmies(placeMoves);
    
            if (gui != null && !(bot(i) instanceof HumanRobot)) {
                List<PlaceArmiesMove> legalMoves = new ArrayList<PlaceArmiesMove>();
    
                for (PlaceArmiesMove move : placeMoves)
                    if (move.getIllegalMove().equals(""))
                        legalMoves.add(move);
                
                gui.placeArmies(i, game.getRegions(), legalMoves);
            }
            
            start = System.currentTimeMillis();
            List<AttackTransferMove> moves = bot(i).moveArmies(game);
            if (timeout(bot(i), start)) {
                System.err.println("bot failed to return attack transfer moves in time!");
                moves = new ArrayList<AttackTransferMove>();
            }
            
            game.attackTransfer(moves);
            
            if (game.isDone())
                break;
        }
        
        if (gui != null) {
            gui.updateMap();
        }
    }
    
    public void distributeStartingRegions()
    {
        if (game.getPhase() == Phase.STARTING_REGIONS) {
            if (gui != null) {
                gui.showPickableRegions();
            }
        
            for (int i = 1 ; i <= game.numStartingRegions() ; ++i)
                for (int p = 1 ; p <= 2 ; ++p) {
                    long start = System.currentTimeMillis();
                    Region region = bot(p).chooseRegion(game);
                    if (timeout(bot(p), start)) {
                        System.err.println("bot failed to return starting region in time!");
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
}
