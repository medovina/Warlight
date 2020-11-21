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

public class GameMap implements Cloneable {
    public ArrayList<Region> regions;  // maps (id - 1) -> Region
    public ArrayList<Continent> continents;  // maps (id - 1) -> Continent
    
    public GameMap()
    {
        regions = new ArrayList<Region>();
        continents = new ArrayList<Continent>();
    }

    public void add(Region region)
    {
        if (region.getId() != regions.size())
            throw new Error("regions out of order");
        regions.add(region);
    }
    
    public void add(Continent continent)
    {
        if (continent.getId() != continents.size())
            throw new Error("continents out of order");
        continents.add(continent);
    }
    
    @Override
    public GameMap clone() {
        GameMap newMap = new GameMap();

        for(Continent sr : continents) //copy continents
        {
            Continent newContinent = new Continent(sr.getMapContinent());
            newMap.add(newContinent);
        }
        for(Region r : regions) //copy regions
        {
            Region newRegion = new Region(r.getMapRegion(),
                    newMap.getContinent(r.getContinent().getId()), r.getOwner(), r.getArmies());
            newMap.add(newRegion);
        }
        for(Region r : regions) //add neighbors to copied regions
        {
            Region newRegion = newMap.getRegion(r.getId());
            for(Region neighbor : r.getNeighbors())
                newRegion.addNeighbor(newMap.getRegion(neighbor.getId()));
        }
        return newMap;
    }
    
    /**
     * @return : the list of all Regions in this map
     */
    public ArrayList<Region> getRegions() {
        return regions;
    }
    
    /**
     * @return : the list of all Continents in this map
     */
    public ArrayList<Continent> getContinents() {
        return continents;
    }
    
    public Region getRegion(int id)
    {
        if (0 <= id && id < regions.size())
            return regions.get(id);
        
        System.err.println("Could not find region with id " + id);
        return null;
    }
    
    public Region getRegion(MapRegion r) {
        return getRegion(r.id);
    }
    
    public Continent getContinent(int id)
    {
        if (0 <= id && id < continents.size())
            return continents.get(id);

        System.err.println("Could not find continent with id " + id);
        return null;
    }

    public Continent getContinent(MapContinent c) {
        return getContinent(c.id);
    }
    
    public int numberRegionsOwned(int player) {
        int n = 0;
        
        for (Region r: regions)
            if (r.getOwner() == player)
                n += 1;
        
        return n;
    }
    
    public int numberArmiesOwned(int player) {
        int n = 0;
        
        for (Region r: regions)
            if (r.getOwner() == player)
                n += r.getArmies();
        
        return n;
    }

    //return all regions owned by given player
    public ArrayList<Region> ownedRegionsByPlayer(int player)
    {
        ArrayList<Region> ownedRegions = new ArrayList<Region>();
        
        for(Region region : this.getRegions())
            if(region.getOwner() == player)
                ownedRegions.add(region);

        return ownedRegions;
    }
        
}
