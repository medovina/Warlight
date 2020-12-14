package game;

import java.util.*;

import com.kitfox.svg.Group;

public class Continent {
    public Group rewardElement;

    public final int id;
    public int reward;
    public final String name;
    
    private List<Region> regions = new ArrayList<Region>();
    
    public Continent(String mapName, int id) {
        this.name = mapName;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public void addRegion(Region r) {
        regions.add(r);
    }
    
    public List<Region> getRegions() {
        return regions;
    }
}
