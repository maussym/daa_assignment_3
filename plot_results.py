import csv
from pathlib import Path

import matplotlib.pyplot as plt


results = Path("results")
with (results / "results.csv").open(newline="", encoding="utf-8") as file:
    rows = list(csv.DictReader(file))

algorithms = ["Kruskal", "Prim", "Topological DAG", "SCC", "Dijkstra"]


def draw(title, x_name, x_values, selected, output, log_x=False):
    fig, ax = plt.subplots(figsize=(8, 5))
    for name in algorithms:
        times = [
            float(next(row["avg_ms"] for row in selected
                       if row["algorithm"] == name and int(row[x_name]) == x))
            for x in x_values
        ]
        ax.plot(x_values, times, marker="o", label=name)
    if log_x:
        ax.set_xscale("log")
    ax.set_xticks(x_values, [str(x) for x in x_values])
    ax.set_title(title)
    ax.set_xlabel(x_name.capitalize())
    ax.set_ylabel("Average time (ms)")
    ax.grid(alpha=0.3)
    ax.legend()
    fig.tight_layout()
    fig.savefig(results / "plots" / output, dpi=160)
    plt.close(fig)


(results / "plots").mkdir(exist_ok=True)
main_sizes = [(10, 30), (100, 500), (1000, 5000)]
by_vertices = [
    row for row in rows
    if (int(row["vertices"]), int(row["edges"])) in main_sizes
]
by_edges = [row for row in rows if int(row["vertices"]) == 1000]
draw("Execution time vs vertices", "vertices", [10, 100, 1000],
     by_vertices, "time_vs_vertices.png", log_x=True)
draw("Execution time vs edges at 1000 vertices", "edges", [1000, 3000, 5000],
     by_edges, "time_vs_edges.png")
print("Saved two plots in results/plots")
