package data_struct;

public class Edge {
    private Station neighbor;
    private double distance;
    private String line;

    public Edge(Station neighbor, double distance, String line) {
        this.neighbor = neighbor;
        this.distance = distance;
        this.line = line;
    }

    public Station getNeighbor() {
        return neighbor;
    }

    public double getDistance() {
        return distance;
    }

    public String getLine() {
        return line;
    }
}
