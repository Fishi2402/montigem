import { AbstractGraph }  from "graphology-types";

export type HierarchicalLayoutOptionsInterface = {
  edgeLabels : string[],
  edgeGroups : string[]
}

export class HierarchicalLayout  {

  protected levelSeparation : number = 150;
  protected nodeSeparation : number = 300;
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

    return { levels, maxLevel };
  }

  // Assign coordinates to nodes based on their levels
  public static assign(g: AbstractGraph, options? : HierarchicalLayoutOptionsInterface) {
    console.warn("Doing something", g, options)
    const hierarchicalLayout = new HierarchicalLayout()
    hierarchicalLayout.graph = g;
    const { levels, maxLevel } = hierarchicalLayout.determineLevels(options?.edgeLabels);
    const levelNodes = Array.from({ length: maxLevel + 1 }, () => []);

    console.warn(levels, maxLevel)

    // Group nodes by their levels
    hierarchicalLayout.graph.forEachNode(nodeKey => {
      const level = levels[nodeKey];
      levelNodes[level].push(nodeKey);
    });

    // Assign coordinates
    levelNodes.forEach((level : string[], levelIndex) => {

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
    });
  }

}
