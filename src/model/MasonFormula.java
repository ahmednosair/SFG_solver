package model;


import java.util.*;

public class MasonFormula {
    DirectGraph graph;
    ArrayList<Path> fwdPaths;
    ArrayList<Path> loops;
    ArrayList<HashSet<HashSet<Integer>>> nonTouchingLoops;
    ArrayList<HashSet<Integer>> touchesPath;

    public MasonFormula() {
    }

    public void setGraph(DirectGraph graph) {
        this.graph = graph;
    }

    public double evaluateTransFn(int src, int sink) {
        fillFwdPathsAndLoops(src, sink);
        filterDup();
        fillNonTouchingLoops();
        fillTouchesPath();
        double delta = 1, deltaCarrier;
        for (int i = 0; i < nonTouchingLoops.size(); i++) {
            double sum = 0;
            for (HashSet<Integer> comb : nonTouchingLoops.get(i)) {
                double product = 1;
                for (Integer l : comb) {
                    product *= loops.get(l).evaluateGain();
                }
                sum += product;
            }
            delta = (i % 2 == 0 ? (delta - sum) : (delta + sum));
        }
        deltaCarrier = delta;
        ArrayList<Double> mK = new ArrayList<>();
        for (int m = 0; m < fwdPaths.size(); m++) {
            delta = 1;
            for (int i = 0; i < nonTouchingLoops.size(); i++) {
                double sum = 0;
                for (HashSet<Integer> comb : nonTouchingLoops.get(i)) {
                    double product = 1;
                    for (Integer l : comb) {
                        product *= (touchesPath.get(m).contains(l) ? 0 : loops.get(l).evaluateGain());
                    }
                    sum += product;
                }
                delta = i % 2 == 0 ? (delta - sum) : (delta + sum);
            }
            mK.add(delta);
        }
        double trsFn = 0;
        for (int m = 0; m < fwdPaths.size(); m++) {
            trsFn += ((mK.get(m) * fwdPaths.get(m).evaluateGain())) / deltaCarrier;
        }
        return trsFn;
    }

    private void fillTouchesPath() {
        touchesPath = new ArrayList<>();
        for (int i = 0; i < fwdPaths.size(); i++) {
            touchesPath.add(new HashSet<>());
            HashSet<Integer> p = new HashSet<>();
            Iterator<DirectEdge> it = fwdPaths.get(i).getEdges();
            while (it.hasNext()) {
                p.add(it.next().destination);
            }
            p.add(fwdPaths.get(i).src);
            for (int j = 0; j < loops.size(); j++) {
                HashSet<Integer> l = new HashSet<>();
                it = loops.get(j).getEdges();
                while (it.hasNext()) {
                    l.add(it.next().destination);
                }
                HashSet<Integer> m = new HashSet<>();
                m.addAll(p);
                m.addAll(l);
                if (m.size() != l.size() + p.size()) {
                    touchesPath.get(i).add(j);
                }
            }
        }
    }

    private void filterDup() {
        ArrayList<Path> dup = loops;
        loops = new ArrayList<>();
        for (Path p : dup) {
            if (!loops.contains(p)) {
                loops.add(p);
            }
        }
    }

    private void fillFwdPathsAndLoops(int src, int sink) {
        Set<Integer> visited = new HashSet<>();
        fwdPaths = new ArrayList<>();
        loops = new ArrayList<>();
        ArrayList<DirectEdge> currentPath = new ArrayList<>();
        visited.add(src);
        getFwdPathAndLoops(src, src, sink, currentPath, visited);
    }

    private void getFwdPathAndLoops(int currentVer, int src, int sink, ArrayList<DirectEdge> currentPath, Set<Integer> visited) {
        if (currentVer == sink) {
            fwdPaths.add(new Path(new ArrayList<>(currentPath), src));
            return;
        }
        ArrayList<DirectEdge> neighbours = graph.getOutEdges(currentVer);
        if (neighbours == null) {
            return;
        }
        for (DirectEdge e : neighbours) {
            if (visited.contains(e.destination)) {
                ArrayList<DirectEdge> tmp = new ArrayList<>();
                boolean flag = false;
                for (DirectEdge directEdge : currentPath) {
                    if (flag) {
                        tmp.add(directEdge);
                    }
                    if (directEdge.destination == e.destination) {
                        flag = true;
                    }
                }
                if (tmp.isEmpty()) {
                    tmp = new ArrayList<>(currentPath);
                    tmp.add(e);
                    loops.add(new Path(tmp, src));
                } else {
                    tmp.add(e);
                    loops.add(new Path(tmp, e.destination));
                }

            } else {
                visited.add(e.destination);
                currentPath.add(e);
                getFwdPathAndLoops(e.destination, src, sink, currentPath, visited);
                currentPath.remove(e);
                visited.remove(e.destination);
            }
        }
    }

    private void fillNonTouchingLoops() {
        nonTouchingLoops = new ArrayList<>();
        nonTouchingLoops.add(new HashSet<>());
        for (int i = 0; i < loops.size(); i++) {
            nonTouchingLoops.get(0).add(new HashSet<>(Collections.singletonList(i)));
        }

        for (int i = 1; i < loops.size(); i++) {
            nonTouchingLoops.add(new HashSet<>());
            HashSet<HashSet<Integer>> tmp = getComb(nonTouchingLoops.get(i - 1));
            for (HashSet<Integer> set : tmp) {
                if (nonTouchingSet(set)) {
                    nonTouchingLoops.get(i).add(set);
                }
            }
        }
    }

    private boolean nonTouchingSet(HashSet<Integer> set) {
        for (Integer i : set) {
            for (Integer j : set) {
                if (!i.equals(j) && loops.get(i).isTouching(loops.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    private HashSet<HashSet<Integer>> getComb(HashSet<HashSet<Integer>> toBeComb) {

        HashSet<HashSet<Integer>> set = new HashSet<>();
        for (HashSet<Integer> list : toBeComb) {
            for (int i = 0; i < loops.size(); i++) {
                HashSet<Integer> tmp = new HashSet<>(list);
                tmp.add(i);
                if (tmp.size() == toBeComb.iterator().next().size() + 1) {
                    set.add(tmp);
                }
            }
        }
        return set;
    }
}
