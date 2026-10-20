package kz.aitu.daa.assignment3;

public class DSU {
    private final int[] parent;
    private final int[] size;
    private long findCalls;

    public DSU(int n) {
        parent = new int[n];
        size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public int find(int x) {
        findCalls++;
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path compression.
        }
        return parent[x];
    }

    public boolean union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);
        if (rootA == rootB) {
            return false;
        }
        if (size[rootA] < size[rootB]) { // Attach the smaller set.
            int temp = rootA;
            rootA = rootB;
            rootB = temp;
        }
        parent[rootB] = rootA;
        size[rootA] += size[rootB];
        return true;
    }

    public long findCalls() {
        return findCalls;
    }
}
