package kz.aitu.daa.assignment3;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    public record Edge(int from, int to, int weight) {}

    private final List<List<Edge>> neighbors = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private final boolean directed;

    public Graph(int vertices, boolean directed) {
        if (vertices < 1) {
            throw new IllegalArgumentException("Graph needs at least one vertex");
        }
        this.directed = directed;
        for (int i = 0; i < vertices; i++) {
            neighbors.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, int weight) {
        if (from < 0 || from >= vertices() || to < 0 || to >= vertices() || weight < 0) {
            throw new IllegalArgumentException("Invalid edge");
        }
        Edge edge = new Edge(from, to, weight);
        edges.add(edge);
        neighbors.get(from).add(edge);
        if (!directed) {
            neighbors.get(to).add(new Edge(to, from, weight));
        }
    }

    public int vertices() {
        return neighbors.size();
    }

    public boolean directed() {
        return directed;
    }

    public List<Edge> edges() {
        return edges;
    }

    public List<Edge> neighbors(int vertex) {
        return neighbors.get(vertex);
    }
}
