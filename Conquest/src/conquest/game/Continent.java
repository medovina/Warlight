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

package conquest.game;
import java.util.ArrayList;

import conquest.game.world.WorldContinent;

public class Continent {
    
    private WorldContinent worldContinent;
    private int id;
    private int armiesReward;
    private ArrayList<Region> regions;
    
    public Continent(WorldContinent continent, int id, int armiesReward)
    {
        this.worldContinent = continent;
        this.id = id;
        this.armiesReward = armiesReward;
        regions = new ArrayList<Region>();
    }
    
    public void addRegion(Region region)
    {
        if(!regions.contains(region))
            regions.add(region);
    }
    
    /**
     * @return The player that fully owns this continent, or 0 if none
     */
    public int owner()
    {
        int player = regions.get(0).getOwner();
        for(Region region : regions)
        {
            if (player != region.getOwner())
                return 0;
        }
        return player;
    }
    
    /**
     * @return The id of this continent
     */
    public int getId() {
        return id;
    }
    
    /**
     * @return The number of armies a Player is rewarded when he fully owns this continent
     */
    public int getArmiesReward() {
        return armiesReward;
    }
    
    /**
     * @return A list with the Regions that are part of this continent
     */
    public ArrayList<Region> getRegions() {
        return regions;
    }

    public WorldContinent getWorldContinent() {
        return worldContinent;
    }
    
}
