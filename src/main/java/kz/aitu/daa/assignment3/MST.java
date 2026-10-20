package kz.aitu.daa.assignment3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class MST {
    public record Result(long weight, boolean connected, int edgesUsed,
                         long edgeChecks, long dsuFindCalls) {}

    public static Result kruskal(Graph graph) {
        checkUndirected(graph);
        List<Graph.Edge> sorted = new ArrayList<>(graph.edges());
        sorted.sort(Comparator.comparingInt(Graph.Edge::weight));

        DSU dsu = new DSU(graph.vertices());
        long weight = 0;
        long checks = 0;
        int used = 0;
        for (Graph.Edge edge : sorted) {
            checks++;
            if (dsu.union(edge.from(), edge.to())) {
                weight += edge.weight();
                used++;
                if (used == graph.vertices() - 1) {
                    break;
                }
            }
        }
        return new Result(weight, used == graph.vertices() - 1,
                used, checks, dsu.findCalls());
    }

    public static Result prim(Graph graph) {
        checkUndirected(graph);
        boolean[] visited = new boolean[graph.vertices()];
        PriorityQueue<Graph.Edge> queue =
                new PriorityQueue<>(Comparator.comparingInt(Graph.Edge::weight));
        long weight = 0;
        long checks = 0;
        int used = 0;
        int parts = 0;

        for (int start = 0; start < graph.vertices(); start++) {
            if (visited[start]) {
                continue;
            }
            parts++;
            queue.add(new Graph.Edge(-1, start, 0));
            while (!queue.isEmpty()) {
                Graph.Edge edge = queue.poll();
                int vertex = edge.to();
                if (visited[vertex]) {
                    continue;
                }
                visited[vertex] = true;
                if (edge.from() != -1) {
                    weight += edge.weight();
                    used++;
                }
                for (Graph.Edge next : graph.neighbors(vertex)) {
                    checks++;
                    if (!visited[next.to()]) {
                        queue.add(next);
                    }
                }
            }
        }
        return new Result(weight, parts == 1, used, checks, 0);
    }

    private static void checkUndirected(Graph graph) {
        if (graph.directed()) {
            throw new IllegalArgumentException("MST requires an undirected graph");
        }
    }
}
