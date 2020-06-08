package control;

public class Carrier {
    final int srcNode;
    final int destNode;
    final double weight;

    public int getSrcNode() {
        return srcNode;
    }

    public int getDestNode() {
        return destNode;
    }

    public double getWeight() {
        return weight;
    }

    public Carrier(int srcNode, int destNode, double weight) {
        this.srcNode = srcNode;
        this.destNode = destNode;
        this.weight = weight;
    }
}
