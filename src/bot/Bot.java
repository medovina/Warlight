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

package bot;

import java.util.List;

import game.*;
import game.move.AttackTransferMove;
import game.move.PlaceArmiesMove;
import game.world.MapRegion;

public interface Bot {
    public void init(long timeoutMillis);

    /**
     * CHOOSE REGIONS - called only at the beginning.
     * @param state
     * @param timeoutMillis in milliseconds
     */
    public MapRegion chooseRegion(GameState state);
    
    /**
     * PLACE ARMIES - distribute armies between your regions.
     * @param state
     * @param timeoutMillis in milliseconds
     */
    public List<PlaceArmiesMove> placeArmies(GameState state);
    
    /**
     * MOVE ARMIES - attack opponents' regions or neutral ones ... or transfer armies between your regions.
     * @param state
     * @param timeoutMillis in milliseconds
     */
    public List<AttackTransferMove> moveArmies(GameState state);

}
