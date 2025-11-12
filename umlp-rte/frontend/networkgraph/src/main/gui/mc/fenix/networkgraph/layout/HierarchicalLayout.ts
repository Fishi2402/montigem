import { AbstractGraph }  from "graphology-types";
import {node} from "graphology-metrics";

export type HierarchicalLayoutOptionsInterface = {
  edgeLabels : string[],
  edgeGroups : string[]
  direction?: "vertical" | "horizontal"
}

export class HierarchicalLayout  {

  protected levelSeparation : number = 150;
  protected nodeSeparation : number = 100;
  protected graph : AbstractGraph;

  // Determine the levels of each node
  public determineLevels(relevantEdgeLabels? : string[], relevantEdgeGroups?: string[]) {
    const levels = {};
    this.graph.forEachNode(nodeKey => {
      levels[nodeKey] = 0;
    });

    let maxLevel = 0;
    this.graph.forEachEdge(edgeKey => {
      if((!relevantEdgeLabels || relevantEdgeLabels.includes(this.graph.getEdgeAttribute(edgeKey, "label")))
        && (!relevantEdgeGroups || relevantEdgeGroups.includes(this.graph.getEdgeAttribute(edgeKey, "group")))
      ) {
        const sourceLevel = levels[this.graph.source(edgeKey)];
        if (levels[this.graph.target(edgeKey)] <= sourceLevel) {
          levels[this.graph.target(edgeKey)] = sourceLevel + 1;
          if (levels[this.graph.target(edgeKey)] > maxLevel) {
            maxLevel = levels[this.graph.target(edgeKey)];
          }
        }
      }
    });

    return levels ;
  }

  public tightTree(graph: AbstractGraph, relevantEdgeLabels? : string[], relevantEdgeGroups?: string[]) {
    // Choose arbitrary node from which to start our tree
    var start = graph.nodes()[0];
    var size = graph.nodes().length;

    const tree : string[] = []
    tree.push(start)

    var edge, delta;
    while (this.tightTreeHelper(tree, graph) < size) {
      edge = this.findMinSlackEdge(tree, graph);
      delta = tree.includes(graph.source(edge)) ? HierarchicalLayout.slack(graph, edge) : -HierarchicalLayout.slack(graph, edge);
      this.shiftRanks(tree, graph, delta);
    }
    return graph;
  }

   /*
   * Finds a maximal tree of tight edges and returns the number of nodes in the
   * tree.
   */
  public tightTreeHelper(t : string[], g : AbstractGraph) {

    function dfs(nodeKey : string) {
      g.edges(nodeKey).forEach(edgeKey => {
        var edgeSource = g.source(edgeKey),
          edgeTarget = (nodeKey === edgeSource) ? g.target(edgeKey) : edgeSource;
        if (!t.includes(edgeTarget) && !HierarchicalLayout.slack(g, edgeKey)) {
          t.push(edgeTarget);
          dfs(edgeTarget);
        }
      });
    }

    t.forEach(dfs, this);
    return t.length;
  }

  public findMinSlackEdge(treeNodes : string[], g : AbstractGraph) {
    console.warn("Treenodes: ",treeNodes)

    let minSlack = Number.POSITIVE_INFINITY;
    let minSlackEdge : string = null;
    g.forEachEdge(edgeKey => {
      if(treeNodes.includes(g.source(edgeKey)) !== treeNodes.includes(g.target(edgeKey))) {
        const currentSlack = HierarchicalLayout.slack(g, edgeKey);
        if(currentSlack < minSlack) {
          minSlack = currentSlack;
          minSlackEdge = edgeKey;
        }
      }
    })
    console.warn(minSlack)
    console.warn("MinSlackEdge:", minSlackEdge)
    return minSlackEdge;
  }

  /*
   * Returns the amount of slack for the given edge. The slack is defined as the
   * difference between the length of the edge and its minimum length.
   */
  public static slack(g : AbstractGraph, edgeKey : string) {
    const slack = g.getNodeAttribute(g.target(edgeKey), "rank") - g.getNodeAttribute(g.source(edgeKey), "rank") - g.getEdgeAttribute(edgeKey, "minlen");
    console.warn("current slack: ",g.target(edgeKey), g.source(edgeKey), slack )
    return slack;
  }

  public longestPath(g : AbstractGraph) {
    var visited = {};

    function dfs(nodeKey : string) {
      if (Object.hasOwn(visited, nodeKey)) {
        console.warn("Already visited", g.getNodeAttribute(nodeKey, "rank"))
        return g.getNodeAttribute(nodeKey, "rank");
      }
      visited[nodeKey] = true;

      let outEdgesMinLens = g.outEdges(nodeKey).length === 0? [Number.POSITIVE_INFINITY] : g.outEdges(nodeKey).map(e => {
        console.warn("Edge is ", e)

        if (e == null) {
          return Number.POSITIVE_INFINITY;
        }
        return dfs(g.target(e)) - g.getEdgeAttribute(e, "minlen");
      });
      let rank = HierarchicalLayout.applyWithChunking(Math.min, outEdgesMinLens);

      if (rank === Number.POSITIVE_INFINITY) {
        rank = 0;
      }

      g.setNodeAttribute(nodeKey, "rank", rank)
      return rank;
    }

    g.nodes().forEach((nodeKey: string) => dfs(nodeKey));
  }

  public static splitToChunks(array, chunkSize = this.CHUNKING_THRESHOLD) {
    const chunks = [];
    for (let i = 0; i < array.length; i += chunkSize) {
      const chunk = array.slice(i, i + chunkSize);
      chunks.push(chunk);
    }
    return chunks;
  }

  private static CHUNKING_THRESHOLD = 65535;

  public static applyWithChunking(fn, argsArray) {
    if(argsArray.length > this.CHUNKING_THRESHOLD) {
      const chunks = this.splitToChunks(argsArray);
      return fn.apply(null, chunks.map(chunk => fn.apply(null, chunk)));
    } else {
      return fn.apply(null, argsArray);
    }
  }

  public shiftRanks(t, g, delta) {
    t.forEach(v => g.setNodeAttribute(v, "rank", g.getNodeAttribute(v, "rank") + delta));
  }


  // Assign coordinates to nodes based on their levels
  public static assign(g: AbstractGraph, options? : HierarchicalLayoutOptionsInterface) {

    console.warn("Doing something", g, options)

    const hierarchicalLayout = new HierarchicalLayout()
    g.forEachEdge(edgeKey =>
      g.setEdgeAttribute(edgeKey, "minlen", 1)
    )

    hierarchicalLayout.longestPath(g)
    hierarchicalLayout.graph = g;
    //const levels = hierarchicalLayout.determineLevels(options?.edgeLabels);

    hierarchicalLayout.tightTree(g)
    const levelNodes : string[][] = []
    let rankMinimum = Number.POSITIVE_INFINITY;
    g.forEachNode(nodeKey => {
      const rank = g.getNodeAttribute(nodeKey, "rank")
      if (rank < rankMinimum) {
        rankMinimum = rank;
      }
    });

    if(rankMinimum === Number.POSITIVE_INFINITY) {
      console.warn("Rank minimum is ", Number.POSITIVE_INFINITY)
      return;
    }
    g.forEachNode(nodeKey => {
      const r :number = g.getNodeAttribute(nodeKey, "rank");
      let index = r;
      if(rankMinimum < 0) {
        index =  r - rankMinimum
      }
      console.warn("Rank", r, "Rank Minimum",rankMinimum)
      while (levelNodes.length <= index) {
        levelNodes.push([]);
      }
      (levelNodes[index]).push(nodeKey)
    })
/*    const levelNodes = Array.from({ length: maxLevel + 1 }, () => []);

    console.warn(levels)

    // Group nodes by their levels
    hierarchicalLayout.graph.forEachNode(nodeKey => {
      const level = levels[nodeKey];
      levelNodes[level].push(nodeKey);
    });*/


    console.warn(levelNodes)
    // Assign coordinates
    levelNodes.forEach((level : string[], levelIndex) => {

      if(options?.direction === "horizontal") {
        const levelHeight = level.reduce((previousValue, currentValue) => previousValue +
            g.getNodeAttribute(currentValue, "size"), 0)
          + (level.length - 1) * hierarchicalLayout.nodeSeparation;
        const startY = (0 - levelHeight) / 2;

        level.forEach((nodeKey, nodeIndex) => {
          if (!g.getNodeAttribute(nodeKey, "fixed")) {
            g.setNodeAttribute(nodeKey, "x", levelIndex * hierarchicalLayout.levelSeparation);
            g.setNodeAttribute(nodeKey, "y", startY + nodeIndex * (g.getNodeAttribute(nodeKey, "size") + hierarchicalLayout.nodeSeparation));
          }
        });
      } else {
        const levelWidth = level.reduce((previousValue, currentValue) => previousValue +
           g.getNodeAttribute(currentValue, "size"),0)
         + (level.length - 1) * hierarchicalLayout.nodeSeparation;
       const startX = (0 - levelWidth) / 2;

        level.forEach((nodeKey, nodeIndex) => {
          if(!g.getNodeAttribute(nodeKey, "fixed")) {
            g.setNodeAttribute(nodeKey, "x", startX + nodeIndex * (g.getNodeAttribute(nodeKey, "size") + hierarchicalLayout.nodeSeparation));
            g.setNodeAttribute(nodeKey, "y", levelIndex * hierarchicalLayout.levelSeparation);
          }
        });
      }
    });
  }

}
