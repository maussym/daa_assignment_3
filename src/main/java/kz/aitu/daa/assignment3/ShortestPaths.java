package kz.aitu.daa.assignment3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class ShortestPaths {
    public static final long INF = Long.MAX_VALUE / 4;

    private record State(int vertex, long distance) {}

    public static class Result {
        private final long[] distance;
        private final int[] previous;
        private final long edgeChecks;

        Result(long[] distance, int[] previous, long edgeChecks) {
            this.distance = distance;
            this.previous = previous;
            this.edgeChecks = edgeChecks;
        }

        public long distanceTo(int vertex) {
            return distance[vertex];
        }

        public List<Integer> pathTo(int vertex) {
            if (distance[vertex] == INF) {
                return List.of();
            }
            List<Integer> path = new ArrayList<>();
            for (int v = vertex; v != -1; v = previous[v]) {
                path.add(v);
            }
            Collections.reverse(path);
            return path;
        }

        public long edgeChecks() {
            return edgeChecks;
        }
    }

    public static Result dijkstra(Graph graph, int source) {
        if (source < 0 || source >= graph.vertices()) {
            throw new IllegalArgumentException("Invalid source");
        }
        long[] distance = new long[graph.vertices()];
        int[] previous = new int[graph.vertices()];
        Arrays.fill(distance, INF);
        Arrays.fill(previous, -1);
        distance[source] = 0;

        PriorityQueue<State> queue =
                new PriorityQueue<>(Comparator.comparingLong(State::distance));
        queue.add(new State(source, 0));
        long checks = 0;
        while (!queue.isEmpty()) {
            State state = queue.poll();
            if (state.distance() != distance[state.vertex()]) {
                continue;
            }
            for (Graph.Edge edge : graph.neighbors(state.vertex())) {
                checks++;
                long candidate = state.distance() + edge.weight();
                if (candidate < distance[edge.to()]) {
                    distance[edge.to()] = candidate;
                    previous[edge.to()] = state.vertex();
                    queue.add(new State(edge.to(), candidate));
                }
            }
        }
        return new Result(distance, previous, checks);
    }
}
