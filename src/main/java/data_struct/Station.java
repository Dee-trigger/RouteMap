package data_struct;

import java.util.ArrayList;
import java.util.List;

public class Station {
    private String name;
    private List<Edge> edges;

    public Station(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addNeighbor(Station neighbor, double distance, String line) {
        edges.add(new Edge(neighbor, distance, line));
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
