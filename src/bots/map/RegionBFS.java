package bots.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import bots.map.RegionBFS.BFSNode;
import game.GameMap;
import game.world.WorldRegion;

/**
 * BFS over {@link WorldRegion}s using visitor pattern via {@link BFSVisitor}.
 * 
 * Your visitor needs to have access to {@link GameMap} in order to be useful.
 * 
 * @author Jimmy
 *
 * @param <NODE>
 */
public class RegionBFS<NODE extends BFSNode> {
    
    public static class BFSNode {
        
        /**
         * Will be auto-filled...
         */
        public WorldRegion region;
        
        /**
         * Will be auto-filled...
         */
        public int level;
        
        /**
         * Will be auto-filled...
         */
        public List<WorldRegion> parents = new ArrayList<WorldRegion>();
        
        public void addParent(WorldRegion region) {
            this.parents.add(region);
        }
        
    }
    
    public static enum BFSVisitResultType {
        
        ADD,
        IGNORE,
        TERMINATE
        
    }
    
    public static class BFSVisitResult<NODE extends BFSNode> {
        
        /**
         * Return if you want this region to be ignored (not added into map of nodes + not added to BFS queue for further expansion). 
         * BFS will continue with a next node from the queue.
         */
        public static <N extends BFSNode> BFSVisitResult<N> ignore() {
            return new BFSVisitResult<N>(BFSVisitResultType.IGNORE, null);
        }
        
        /**
         * Return if you want to terminate BFS right away.
         */
        public static <N extends BFSNode> BFSVisitResult<N> terminate() {
            return new BFSVisitResult<N>(BFSVisitResultType.TERMINATE, null);
        }
        
        public BFSVisitResultType type;
        
        public NODE node;
        
        public BFSVisitResult(NODE node) {
            this.type = BFSVisitResultType.ADD;
            this.node = node;
        }
        
        public BFSVisitResult(BFSVisitResultType type, NODE node) {
            this.type = type;
            this.node = node;
        }
        
    }
    
    public static interface BFSVisitor<NODE extends BFSNode> {
        
        /**
         * 
         * @param region region we have touched
         * @param level how deep (far) this region is from the start
         * @param parent from where we have reached the region (guaranteed to be "level-1" parent)
         * @param thisNode this is non-null if we already visited the region in the past
         * @return
         */
        public BFSVisitResult<NODE> visit(WorldRegion region, int level, NODE parent, NODE thisNode);
        
    }
    
    public RegionBFS() {        
    }
    
    private Map<WorldRegion, NODE> nodes = new HashMap<WorldRegion, NODE>();
    
    public void run(WorldRegion start, BFSVisitor<NODE> visitor) {
        
        reset();
        
        BFSVisitResult<NODE> firstVisit = visitor.visit(start, 0, null, null);
        
        if (firstVisit.type == BFSVisitResultType.IGNORE || firstVisit.type == BFSVisitResultType.TERMINATE) return;
        
        firstVisit.node.region = start;
        firstVisit.node.level = 0;
        
        nodes.put(start, firstVisit.node);
        
        LinkedList<NODE> queue = new LinkedList<NODE>();
        
        queue.add(firstVisit.node);
        
        while (queue.size() > 0) {
            NODE parent = queue.removeFirst();
            
            for (WorldRegion region : parent.region.getNeighbours()) {
                
                NODE node = nodes.get(region);
                
                boolean newNode = node == null;
                
                if (!newNode && node.level <= parent.level) continue;
                
                BFSVisitResult<NODE> result = visitor.visit(region, parent.level+1, parent, node);
                
                if (result.type == BFSVisitResultType.IGNORE) {
                    continue;                        
                }
                
                if (node == null) {
                    node = result.node;
                    if (node != null) {
                        node.region = region;
                        node.level = parent.level+1;                            
                        nodes.put(region, result.node);
                    }
                }
                if (node != null) {
                    node.addParent(parent.region);
                }
                
                if (newNode && node != null) queue.addLast(node);
                
                if (result.type == BFSVisitResultType.TERMINATE) {
                    return;
                }
            }
        }        
    }
    
    public List<List<WorldRegion>> getAllPaths(WorldRegion to) {
        List<List<WorldRegion>> result = new ArrayList<List<WorldRegion>>();
        
        NODE node = getNode(to);
        
        if (node == null) return result;
        
        List<WorldRegion> firstPath = new ArrayList<WorldRegion>();
        firstPath.add(to);
        result.add(firstPath);
        
        generatePath(result, firstPath, to);
        
        for (List<WorldRegion> path : result) {
            Collections.reverse(path);
        }
        
        return result;
    }
    
    private void generatePath(List<List<WorldRegion>> result, List<WorldRegion> currentPath, WorldRegion nodeRegion) {
        NODE node = getNode(nodeRegion);
        
        if (node.parents.size() == 0) {
            return;
        }
        
        boolean first = true;
        
        int pathLen = currentPath.size();
        
        for (WorldRegion parent : node.parents) {
            List<WorldRegion> myPath = null;
            if (first) {
                myPath = currentPath;
            } else {
                myPath = new ArrayList<WorldRegion>(currentPath.subList(0, pathLen));
                result.add(myPath);
            }
            myPath.add(parent);
            generatePath(result, myPath, parent);            
            first = false;
        }        
    }

    public Map<WorldRegion, NODE> getNodes() {
        return nodes;
    }
    
    public NODE getNode(WorldRegion region) {
        return nodes.get(region);
    }

    private void reset() {
        nodes.clear();
    }
    
    public static void main(String[] args) {
        // SIMPLE TEST FOR FINDING "all shortest path" BETWEEN SOME REGIONS
        
        BFSVisitor<BFSNode> visitor = new BFSVisitor<BFSNode>() {

            @Override
            public BFSVisitResult<BFSNode> visit(WorldRegion region, int level, BFSNode parent, BFSNode thisNode) {
                System.out.println((parent == null ? "START" : parent.level + ":" + parent.region) + " --> " + level + ":" + region);
                // WE CAN IGNORE THE REGION
                //return BFSVisitResult.IGNORE;
                
                // WE CAN TERMINATE PREMATURALY
                //return BFSVisitResult.TERMINATE;
                
                return new BFSVisitResult<BFSNode>(thisNode == null ? new BFSNode() : thisNode);
            }

        };
        
        RegionBFS<BFSNode> bfs = new RegionBFS<BFSNode>();
        
        WorldRegion from = WorldRegion.Eastern_Australia;
        
        System.out.println("BFS from " + from);
        
        bfs.run(from, visitor);
        
        System.out.println();
        
        // GET ALL SHORTEST PATH FROM REGION 'from' TO A FEW RANDOM OTHER REGIONS
        for (int i = 0; i < 5; ++i) {
            WorldRegion to = WorldRegion.values()[new Random().nextInt(WorldRegion.values().length)];
            while (from == to) {
                to = WorldRegion.values()[new Random().nextInt(WorldRegion.values().length)];
            }
            
            List<List<WorldRegion>> allPaths = bfs.getAllPaths(to);
            
            System.out.println("PATHS " + from + " --> " + to + ":");
            
            int j = 0;
            for (List<WorldRegion> path : allPaths) {
                ++j;
                System.out.print("  [" + path.size() + "] " + j + ". ");
                boolean first = true;
                for (WorldRegion region : path) {
                    if (first) first = false;
                    else System.out.print(" --> ");
                    System.out.print(region);
                }
                System.out.println();
            }
            
        }
        
    }

}
