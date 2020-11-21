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
package game;

import java.util.ArrayList;

public class Region {
    private MapRegion mapRegion;
    private ArrayList<Region> neighbors;
    private Continent continent;
    
    public Region(MapRegion region, Continent continent)
    {
        this.mapRegion = region;
        this.continent = continent;
        this.neighbors = new ArrayList<Region>();
        
        continent.addRegion(this);
    }
    
    public void addNeighbor(Region neighbor)
    {
        if(!neighbors.contains(neighbor))
            neighbors.add(neighbor);
    }
    
    /**
     * @param region a Region object
     * @return True if this Region is a neighbor of given Region, false otherwise
     */
    public boolean isNeighbor(Region region)
    {
        return neighbors.contains(region);
    }

    /**
     * @return The id of this Region
     */
    public int getId() {
        return mapRegion.id;
    }

    public String mapName() {
        return mapRegion.getName();
    }
    
    /**
     * @return A list of this Region's neighboring Regions
     */
    public ArrayList<Region> getNeighbors() {
        return neighbors;
    }

    /**
     * @return The continent this Region is part of
     */
    public Continent getContinent() {
        return continent;
    }
    
    public MapRegion getMapRegion() {
        return mapRegion;
    }
}
