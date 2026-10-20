package kz.aitu.daa.assignment3;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmsTest {
    @Test
    void dsuJoinsSets() {
        DSU dsu = new DSU(4);
        assertTrue(dsu.union(0, 1));
        assertTrue(dsu.union(2, 3));
        assertTrue(dsu.union(1, 2));
        assertFalse(dsu.union(0, 3));
        assertEquals(dsu.find(0), dsu.find(3));
    }

    @Test
    void bothMstAlgorithmsFindTheSameWeight() {
        Graph graph = new Graph(4, false);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 2);
        graph.addEdge(0, 2, 4);
        graph.addEdge(2, 3, 3);
        graph.addEdge(1, 3, 5);
        assertEquals(6, MST.kruskal(graph).weight());
        assertEquals(6, MST.prim(graph).weight());
        assertTrue(MST.kruskal(graph).connected());
        assertEquals(3, MST.prim(graph).edgesUsed());
    }

    @Test
    void disconnectedAndSingleVertexGraphs() {
        Graph disconnected = new Graph(4, false);
        disconnected.addEdge(0, 1, 2);
        disconnected.addEdge(2, 3, 3);
        assertFalse(MST.kruskal(disconnected).connected());
        assertFalse(MST.prim(disconnected).connected());
        assertEquals(5, MST.kruskal(disconnected).weight());
        assertEquals(5, MST.prim(disconnected).weight());

        Graph single = new Graph(1, false);
        assertTrue(MST.kruskal(single).connected());
        assertTrue(MST.prim(single).connected());
        assertEquals(0, MST.prim(single).weight());
        assertEquals(List.of(0), ShortestPaths.dijkstra(single, 0).pathTo(0));
    }

    @Test
    void duplicateWeightsAllowMoreThanOneMst() {
        Graph graph = new Graph(4, false);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 0, 1);
        assertEquals(3, MST.kruskal(graph).weight());
        assertEquals(3, MST.prim(graph).weight());
    }

    @Test
    void randomConnectedGraphsGiveEqualMstWeights() {
        Random random = new Random(42);
        for (int trial = 0; trial < 30; trial++) {
            Graph graph = new Graph(8, false);
            for (int v = 1; v < 8; v++) {
                graph.addEdge(v - 1, v, 1 + random.nextInt(20));
            }
            for (int j = 0; j < 12; j++) {
                int a = random.nextInt(8);
                int b = random.nextInt(8);
                if (a != b) {
                    graph.addEdge(a, b, 1 + random.nextInt(20));
                }
            }
            assertEquals(MST.kruskal(graph).weight(), MST.prim(graph).weight());
        }
    }

    @Test
    void topologicalOrderWorksForDagAndRejectsCycle() {
        Graph dag = new Graph(4, true);
        dag.addEdge(0, 1, 1);
        dag.addEdge(0, 2, 1);
        dag.addEdge(1, 3, 1);
        dag.addEdge(2, 3, 1);
        Connectivity.TopoResult result = Connectivity.topologicalOrder(dag);
        assertTrue(result.dag());
        assertEquals(4, result.order().size());
        int[] position = new int[4];
        for (int i = 0; i < result.order().size(); i++) {
            position[result.order().get(i)] = i;
        }
        for (Graph.Edge edge : dag.edges()) {
            assertTrue(position[edge.from()] < position[edge.to()]);
        }

        dag.addEdge(3, 0, 1);
        assertFalse(Connectivity.topologicalOrder(dag).dag());
        assertTrue(Connectivity.topologicalOrder(dag).order().isEmpty());
    }

    @Test
    void kosarajuFindsEveryComponentOnce() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 2, 1);
        graph.addEdge(3, 4, 1);
        List<List<Integer>> groups =
                Connectivity.stronglyConnectedComponents(graph).components();
        assertEquals(3, groups.size());
        assertTrue(groups.stream().anyMatch(g -> g.size() == 2 && g.containsAll(List.of(0, 1))));
        assertTrue(groups.stream().anyMatch(g -> g.size() == 2 && g.containsAll(List.of(2, 3))));
        assertTrue(groups.stream().anyMatch(g -> g.equals(List.of(4))));
        int[] all = groups.stream().flatMap(List::stream).mapToInt(Integer::intValue).sorted().toArray();
        assertArrayEquals(new int[]{0, 1, 2, 3, 4}, all);
    }

    @Test
    void dijkstraReportsDistancePathAndUnreachableVertex() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 4);
        graph.addEdge(0, 2, 1);
        graph.addEdge(2, 1, 2);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 8);
        ShortestPaths.Result result = ShortestPaths.dijkstra(graph, 0);
        assertEquals(4, result.distanceTo(3));
        assertEquals(List.of(0, 2, 1, 3), result.pathTo(3));
        assertEquals(ShortestPaths.INF, result.distanceTo(4));
        assertTrue(result.pathTo(4).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> graph.addEdge(0, 4, -1));
        assertThrows(IllegalArgumentException.class, () -> ShortestPaths.dijkstra(graph, 5));
    }

    @Test
    void oneVertexDirectedGraphIsDagAndOneScc() {
        Graph graph = new Graph(1, true);
        assertEquals(List.of(0), Connectivity.topologicalOrder(graph).order());
        assertEquals(List.of(List.of(0)),
                Connectivity.stronglyConnectedComponents(graph).components());
    }
}
