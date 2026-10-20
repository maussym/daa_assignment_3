package kz.aitu.daa.assignment3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class Connectivity {
    public record TopoResult(List<Integer> order, boolean dag, long edgeChecks) {}
    public record SccResult(List<List<Integer>> components, long edgeChecks) {}

    public static TopoResult topologicalOrder(Graph graph) {
        checkDirected(graph);
        int[] indegree = new int[graph.vertices()];
        long checks = 0;
        for (Graph.Edge edge : graph.edges()) {
            indegree[edge.to()]++;
            checks++;
        }
        Queue<Integer> queue = new ArrayDeque<>();
        for (int v = 0; v < graph.vertices(); v++) {
            if (indegree[v] == 0) {
                queue.add(v);
            }
        }

        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int v = queue.remove();
            order.add(v);
            for (Graph.Edge edge : graph.neighbors(v)) {
                checks++;
                if (--indegree[edge.to()] == 0) {
                    queue.add(edge.to());
                }
            }
        }
        boolean dag = order.size() == graph.vertices();
        return new TopoResult(dag ? order : List.of(), dag, checks);
    }

    public static SccResult stronglyConnectedComponents(Graph graph) {
        checkDirected(graph);
        boolean[] visited = new boolean[graph.vertices()];
        List<Integer> finished = new ArrayList<>();
        long[] checks = {0};
        for (int v = 0; v < graph.vertices(); v++) {
            if (!visited[v]) {
                finishDfs(graph, v, visited, finished, checks);
            }
        }

        List<List<Integer>> reversed = new ArrayList<>();
        for (int v = 0; v < graph.vertices(); v++) {
            reversed.add(new ArrayList<>());
        }
        for (Graph.Edge edge : graph.edges()) {
            reversed.get(edge.to()).add(edge.from());
            checks[0]++;
        }

        Arrays.fill(visited, false);
        List<List<Integer>> components = new ArrayList<>();
        for (int i = finished.size() - 1; i >= 0; i--) {
            int v = finished.get(i);
            if (!visited[v]) {
                List<Integer> component = new ArrayList<>();
                collectDfs(reversed, v, visited, component, checks);
                components.add(component);
            }
        }
        return new SccResult(components, checks[0]);
    }

    private static void finishDfs(Graph graph, int v, boolean[] visited,
                                  List<Integer> finished, long[] checks) {
        visited[v] = true;
        for (Graph.Edge edge : graph.neighbors(v)) {
            checks[0]++;
            if (!visited[edge.to()]) {
                finishDfs(graph, edge.to(), visited, finished, checks);
            }
        }
        finished.add(v);
    }

    private static void collectDfs(List<List<Integer>> reversed, int v,
                                   boolean[] visited, List<Integer> component,
                                   long[] checks) {
        visited[v] = true;
        component.add(v);
        for (int next : reversed.get(v)) {
            checks[0]++;
            if (!visited[next]) {
                collectDfs(reversed, next, visited, component, checks);
            }
        }
    }

    private static void checkDirected(Graph graph) {
        if (!graph.directed()) {
            throw new IllegalArgumentException("Directed graph required");
        }
    }
}
