package model;

import java.util.*;


public class Path {
    final private ArrayList<DirectEdge> path;
    final int src;

    public Path(ArrayList<DirectEdge> list, int src) {
        path = list;
        this.src = src;
    }

    public Iterator<DirectEdge> getEdges() {
        return path.iterator();
    }

    public double evaluateGain() {
        double gain = 1;
        for (DirectEdge e : path) {
            gain *= e.weight;
        }
        return gain;
    }


    public boolean isTouching(Path p) {
        Set<Integer> s = new HashSet<>();
        s.add(src);
        for (DirectEdge e : path) {
            s.add(e.destination);
        }
        Iterator<DirectEdge> it = p.getEdges();
        if (s.contains(p.src)) {
            return true;
        }
        while (it.hasNext()) {
            if (s.contains(it.next().destination)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path1 = (Path) o;
        String thisP = toString();
        String thatP = path1.toString();
        if (thisP.length() != thatP.length()) {
            return false;
        }
        thisP = thisP.concat(thisP);
        return thisP.contains(thatP);
    }


    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (path.size()==1) {
            b.append(src);
            b.append('|');
        }
        for (int i=0;i<path.size()-1;i++) {
            b.append(path.get(i).destination);
            b.append('|');
        }
        return b.toString();
    }

}
