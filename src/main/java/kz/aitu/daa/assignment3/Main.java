package kz.aitu.daa.assignment3;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equals("bench")) {
            Benchmark.main(new String[0]);
            return;
        }
        Graph roads = new Graph(5, false);
        roads.addEdge(0, 1, 4);
        roads.addEdge(0, 2, 2);
        roads.addEdge(1, 2, 1);
        roads.addEdge(1, 3, 5);
        roads.addEdge(2, 3, 8);
        roads.addEdge(3, 4, 3);

        MST.Result kruskal = MST.kruskal(roads);
        MST.Result prim = MST.prim(roads);
        System.out.println("Kruskal MST: " + kruskal.weight()
                + ", connected: " + kruskal.connected());
        System.out.println("Prim MST: " + prim.weight()
                + ", connected: " + prim.connected());

        ShortestPaths.Result paths = ShortestPaths.dijkstra(roads, 0);
        for (int v = 0; v < roads.vertices(); v++) {
            System.out.println("Route 0 to " + v + ": " + paths.pathTo(v)
                    + ", distance: " + paths.distanceTo(v));
        }

        Graph oneWay = new Graph(4, true);
        oneWay.addEdge(0, 1, 1);
        oneWay.addEdge(0, 2, 1);
        oneWay.addEdge(1, 3, 1);
        oneWay.addEdge(2, 3, 1);
        System.out.println("Topological order: "
                + Connectivity.topologicalOrder(oneWay).order());
        oneWay.addEdge(3, 1, 1);
        System.out.println("Topological order with cycle possible: "
                + Connectivity.topologicalOrder(oneWay).dag());
        System.out.println("Strongly connected components with cycle: "
                + Connectivity.stronglyConnectedComponents(oneWay).components());
    }
}
