package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DirectGraph {
    Map<Integer, ArrayList<DirectEdge>> adjList;

    public DirectGraph() {
        adjList = new HashMap<>();
    }

    public ArrayList<DirectEdge> getOutEdges(int v) {
        return adjList.get(v);
    }

    public void addEdge(int v, DirectEdge e) {
        if (!adjList.containsKey(v)) {
            adjList.put(v, new ArrayList<>());

        }
        adjList.get(v).add(e);
    }

    public int numVertices() {
        return adjList.size();
    }
}
