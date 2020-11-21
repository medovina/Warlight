package bots;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bots.RegionBFS.BFSNode;
import game.GameMap;
import game.MapRegion;

/**
 * BFS over {@link MapRegion}s using visitor pattern via {@link BFSVisitor}.
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
        public MapRegion region;
        
        /**
         * Will be auto-filled...
         */
        public int level;
        
        /**
         * Will be auto-filled...
         */
        public List<MapRegion> parents = new ArrayList<MapRegion>();
        
        public void addParent(MapRegion region) {
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
        public BFSVisitResult<NODE> visit(MapRegion region, int level, NODE parent, NODE thisNode);
        
    }
    
    public RegionBFS() {        
    }
    
    private Map<MapRegion, NODE> nodes = new HashMap<MapRegion, NODE>();
    
    public void run(MapRegion start, BFSVisitor<NODE> visitor) {
        
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
            
            for (MapRegion region : parent.region.getNeighbours()) {
                
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
    
    public List<List<MapRegion>> getAllPaths(MapRegion to) {
        List<List<MapRegion>> result = new ArrayList<List<MapRegion>>();
        
        NODE node = getNode(to);
        
        if (node == null) return result;
        
        List<MapRegion> firstPath = new ArrayList<MapRegion>();
        firstPath.add(to);
        result.add(firstPath);
        
        generatePath(result, firstPath, to);
        
        for (List<MapRegion> path : result) {
            Collections.reverse(path);
        }
        
        return result;
    }
    
    private void generatePath(List<List<MapRegion>> result, List<MapRegion> currentPath, MapRegion nodeRegion) {
        NODE node = getNode(nodeRegion);
        
        if (node.parents.size() == 0) {
            return;
        }
        
        boolean first = true;
        
        int pathLen = currentPath.size();
        
        for (MapRegion parent : node.parents) {
            List<MapRegion> myPath = null;
            if (first) {
                myPath = currentPath;
            } else {
                myPath = new ArrayList<MapRegion>(currentPath.subList(0, pathLen));
                result.add(myPath);
            }
            myPath.add(parent);
            generatePath(result, myPath, parent);            
            first = false;
        }        
    }

    public Map<MapRegion, NODE> getNodes() {
        return nodes;
    }
    
    public NODE getNode(MapRegion region) {
        return nodes.get(region);
    }

    private void reset() {
        nodes.clear();
    }
}
