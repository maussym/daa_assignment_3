package kz.aitu.daa.assignment3;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

public class Benchmark {
    private static final int REPEATS = 5;
    private record Count(long edgeChecks, long dsuFindCalls) {}

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("results"));
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(
                Path.of("results/results.csv")))) {
            out.println("vertices,edges,algorithm,avg_ms,edge_checks,dsu_find_calls");
            int[][] sizes = {
                    {10, 30}, {100, 500}, {1000, 1000},
                    {1000, 3000}, {1000, 5000}
            };
            for (int[] size : sizes) {
                int vertices = size[0];
                int edges = size[1];
                Graph roads = makeRoads(vertices, edges);
                Graph dag = makeDirected(vertices, edges, false);
                Graph cyclic = makeDirected(vertices, edges, true);
                MST.Result kruskalCheck = MST.kruskal(roads);
                if (!kruskalCheck.connected()
                        || kruskalCheck.weight() != MST.prim(roads).weight()
                        || !Connectivity.topologicalOrder(dag).dag()
                        || Connectivity.topologicalOrder(cyclic).dag()) {
                    throw new IllegalStateException("Generated graph check failed");
                }

                measure(out, vertices, edges, "Kruskal", () -> {
                    MST.Result result = MST.kruskal(roads);
                    return new Count(result.edgeChecks(), result.dsuFindCalls());
                });
                measure(out, vertices, edges, "Prim", () -> {
                    MST.Result result = MST.prim(roads);
                    return new Count(result.edgeChecks(), 0);
                });
                measure(out, vertices, edges, "Topological DAG", () -> {
                    Connectivity.TopoResult result = Connectivity.topologicalOrder(dag);
                    return new Count(result.edgeChecks(), 0);
                });
                measure(out, vertices, edges, "Topological cycle", () -> {
                    Connectivity.TopoResult result = Connectivity.topologicalOrder(cyclic);
                    return new Count(result.edgeChecks(), 0);
                });
                measure(out, vertices, edges, "SCC", () -> {
                    Connectivity.SccResult result =
                            Connectivity.stronglyConnectedComponents(cyclic);
                    return new Count(result.edgeChecks(), 0);
                });
                measure(out, vertices, edges, "Dijkstra", () -> {
                    ShortestPaths.Result result = ShortestPaths.dijkstra(roads, 0);
                    return new Count(result.edgeChecks(), 0);
                });
            }
        }
        System.out.println("Saved results/results.csv");
    }

    private static void measure(PrintWriter out, int vertices, int edges,
                                String name, Supplier<Count> algorithm) {
        for (int warmup = 0; warmup < 3; warmup++) {
            algorithm.get();
        }
        long totalNanos = 0;
        Count count = null;
        for (int run = 0; run < REPEATS; run++) {
            long start = System.nanoTime();
            count = algorithm.get();
            totalNanos += System.nanoTime() - start;
        }
        double averageMs = totalNanos / (REPEATS * 1_000_000.0);
        out.printf(Locale.US, "%d,%d,%s,%.6f,%d,%d%n",
                vertices, edges, name, averageMs,
                count.edgeChecks(), count.dsuFindCalls());
    }

    private static Graph makeRoads(int vertices, int edges) {
        Graph graph = new Graph(vertices, false);
        Random random = new Random(42L + vertices * 31L + edges);
        Set<Long> used = new HashSet<>();
        for (int v = 1; v < vertices; v++) {
            addUnique(graph, used, v - 1, v, random, false);
        }
        while (graph.edges().size() < edges) {
            int a = random.nextInt(vertices);
            int b = random.nextInt(vertices);
            if (a != b) {
                addUnique(graph, used, a, b, random, false);
            }
        }
        return graph;
    }

    private static Graph makeDirected(int vertices, int edges, boolean cyclic) {
        Graph graph = new Graph(vertices, true);
        Random random = new Random(142L + vertices * 31L + edges);
        Set<Long> used = new HashSet<>();
        for (int v = 1; v < vertices; v++) {
            addUnique(graph, used, v - 1, v, random, true);
        }
        if (cyclic) {
            addUnique(graph, used, vertices - 1, 0, random, true);
        }
        while (graph.edges().size() < edges) {
            int a = random.nextInt(vertices);
            int b = random.nextInt(vertices);
            if (!cyclic && a > b) {
                int temp = a;
                a = b;
                b = temp;
            }
            if (a != b) {
                addUnique(graph, used, a, b, random, true);
            }
        }
        return graph;
    }

    private static void addUnique(Graph graph, Set<Long> used, int a, int b,
                                  Random random, boolean directed) {
        if (!directed && a > b) {
            int temp = a;
            a = b;
            b = temp;
        }
        long key = ((long) a << 32) | (b & 0xffffffffL);
        if (used.add(key)) {
            graph.addEdge(a, b, random.nextInt(100) + 1);
        }
    }
}
