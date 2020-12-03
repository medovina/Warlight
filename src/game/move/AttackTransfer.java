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

package game.move;

import game.Region;

/**
 * This Move is used in the second part of each round. It represents the attack or transfer of armies from
 * fromRegion to toRegion. If toRegion is owned by the player himself, it's a transfer. If toRegion is
 * owned by the opponent, this Move is an attack. 
 */

public class AttackTransfer {
    public Region fromRegion;
    public Region toRegion;
    public int armies;
    
    public AttackTransfer(Region fromRegion, Region toRegion, int armies) {
        this.fromRegion = fromRegion; this.toRegion = toRegion; this.armies = armies;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof AttackTransfer))
            return false;

        AttackTransfer a = (AttackTransfer) o;
        return fromRegion == a.fromRegion && toRegion == a.toRegion &&
               armies == a.armies;
    }

    public void setArmies(int n) {
        armies = n;
    }
    
    public Region getFromRegion() {
        return fromRegion;
    }
    
    public Region getToRegion() {
        return toRegion;
    }
    
    public int getArmies() {
        return armies;
    }
    
    public String getString() {
        return String.format(
            "attack/transfer with %d armies from %s to %s",
            armies, fromRegion.getName(), toRegion.getName());
    }
}
