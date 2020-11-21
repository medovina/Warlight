package game;

import java.util.*;

public class World {
    ArrayList<MapContinent> continents = new ArrayList<MapContinent>();
    ArrayList<MapRegion> regions = new ArrayList<MapRegion>();

    public World() {
        for (ContinentData d : ContinentData.values()) {
            continents.add(new MapContinent(d.mapName, d.id, d.reward));
            if (continents.size() != d.id)
                throw new RuntimeException("id mismatch");
        }

        for (RegionData d : RegionData.values()) {
            MapContinent c = getContinentById(d.continentData.id);
            MapRegion r = new MapRegion(d.name, regions.size() + 1, c);
            regions.add(r);
            c.addRegion(r);
        }

        int id = 1;
        for (RegionData d : RegionData.values()) {
            MapRegion r = getRegionById(id);
            for (int n : d.forwardNeighbourIds) {
                MapRegion s = getRegionById(n);
                r.addNeighbor(s);
                s.addNeighbor(r);
            }
            id += 1;
        }
    }

    public int numContinents() {
        return continents.size();
    }

    public List<MapContinent> getContinents() {
        return continents;
    }

    public MapContinent getContinentById(int i) {
        return continents.get(i - 1);
    }

    public int numRegions() {
        return regions.size();
    }

    public List<MapRegion> getRegions() {
        return regions;
    }

    public MapRegion getRegionById(int i) {
        return regions.get(i - 1);
    }
}
