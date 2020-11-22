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

import java.util.List;

import game.*;
import game.move.AttackTransferMove;
import game.move.PlaceArmiesMove;

public interface Agent {
    public void init(long timeoutMillis);

    /**
     * CHOOSE REGIONS - called only at the beginning.
     */
    public Region chooseRegion(Game game);
    
    /**
     * PLACE ARMIES - distribute armies between your regions.
     */
    public List<PlaceArmiesMove> placeArmies(Game game);
    
    /**
     * MOVE ARMIES - attack opponents' regions or neutral ones ... or transfer armies between your regions.
     */
    public List<AttackTransferMove> moveArmies(Game game);

}
