import { EdgeAttributes, GenerateMetricsFunction, MetricInformation, NodeAttributes } from "../types/graph.types";
import { GraphLayoutService } from "./graph-layout.service";
import { Injectable } from "@angular/core";
import {
  density,
  diameter,
  edgeExtent,
  modularity,
  nodeExtent,
  simpleSize,
  weightedSize,
} from "graphology-metrics/graph";
import { edgeUniformity, neighborhoodPreservation, stress } from "graphology-metrics/layout-quality";
import { eccentricity, EdgeWeightGetter, weightedDegree } from "graphology-metrics/node";
import { hits, pagerank } from "graphology-metrics/centrality";
import { disparity, simmelianStrength } from "graphology-metrics/edge";
import betweennessCentrality from "graphology-metrics/centrality/betweenness";
import edgeBetweennessCentrality from "graphology-metrics/centrality/edge-betweenness";
import closenessCentrality from "graphology-metrics/centrality/closeness";
import { degreeCentrality } from "graphology-metrics/centrality/degree";
import eigenvectorCentrality from "graphology-metrics/centrality/eigenvector";
import { SigmaGraphService } from "../data/sigma-graph.service";
import { Attributes } from "graphology-types";
import { GraphDataService } from "../data/graph-data.service";

/**
 * GraphMetricsService provides different functions for computing various metrics related to the graph.
 *
 * @template N - Type of the Node Attributes
 * @template E - Type of the Edge Attributes
 * @template G - Type of the Graph Attributes
 */
@Injectable()
export class GraphMetricsService<N extends NodeAttributes, E extends EdgeAttributes, G extends Attributes> {
  private customMetricsGenerator?: GenerateMetricsFunction;

  /** Mapping object to manage metric functions. Each function is mapped to a string key. */
  private metricsInformationMapping: {
    [key: string]: Function;
  } = {
    nodeCount: this.getNodesCount.bind(this),
    edgeCount: this.getEdgesCount.bind(this),
    iteration: this.getIteration.bind(this),
    density: this.getDensity.bind(this),
    nodeSizeExtent: this.getNodeSizeExtent.bind(this),
    edgeWeightExtent: this.getEdgeExtent.bind(this),
    modularity: this.getModularity.bind(this),
    simpleSize: this.getSimpleSize.bind(this),
    weightedSize: this.getWeightedSize.bind(this),
    weightedDegree: this.getWeightedDegree.bind(this),
    eccentricity: this.getEccentricity.bind(this),
    edgeUniformity: this.getEdgeUniformity.bind(this),
    neighborhoodPreservation: this.getNeighborhoodPreservation.bind(this),
    diameter: () => ((this.graphData.visibleNodeCount() ?? 0) > 1000 ? "Graph is too large" : this.getDiameter()),
    stress: () => ((this.graphData.visibleEdgeCount() ?? 0) > 1000 ? "Graph is too large" : this.getStress()),
  };

  /** Mapping object for display names of each metric. */
  private displayNamesMapping: { [key: string]: string } = {
    nodeCount: "Nodes Count",
    edgeCount: "Edges Count",
    iteration: "Iteration",
    density: "Density",
    diameter: "Diameter",
    nodeSizeExtent: "Node Size Extent",
    edgeWeightExtent: "Edge Weight Extent",
    modularity: "Modularity",
    simpleSize: "Simple Size",
    weightedSize: "Weighted Size",
    weightedDegree: "Weighted Degree",
    eccentricity: "Eccentricity",
    edgeUniformity: "Edge Uniformity",
    neighborhoodPreservation: "Neighborhood Preservation",
    stress: "Stress",
  };

  constructor(
    private graphData: GraphDataService<N, E, G>,
    private layoutService: GraphLayoutService<N, E, G>,
    private sigmaGraph: SigmaGraphService<N, E, G>
  ) {}

  /**
   * Sets a custom metric generator function.
   *
   * @param {GenerateMetricsFunction} generateMetricsFunction - Custom metrics generator
   */
  public setGenerateMetrics(generateMetricsFunction: GenerateMetricsFunction): void {
    this.customMetricsGenerator = generateMetricsFunction;
  }

  /**
   * Retrieves the display name for a given metric key.
   *
   * @param {string} key - Metric key
   * @returns {string} - Display name of the metric
   */
  public getDisplayName(key: string): string {
    return this.displayNamesMapping[key] || key;
  }

  /**
   * Retrieves all available metric keys.
   *
   * @returns {string[]} - Array of all metric keys
   */
  public getAllInformationMetricKeys(): string[] {
    return Object.keys(this.metricsInformationMapping);
  }

  /**
   * Compiles and returns metric information based on specified or all keys.
   *
   * @param {string[]} [includeKeys] - Optional array of keys to include in the result
   * @returns {MetricInformation} - Object containing metric information
   */
  public getMetricInformation(includeKeys: string[] = this.getAllInformationMetricKeys()): MetricInformation {
    if (!this.sigmaGraph.renderer) return {};
    if (this.customMetricsGenerator) return this.customMetricsGenerator();

    const result: MetricInformation = {};
    for (const key in this.metricsInformationMapping) {
      if (includeKeys.includes(key)) {
        result[this.getDisplayName(key)] = this.metricsInformationMapping[key]();
      }
    }
    return result;
  }

  // Individual metric functions
  /**
   * Retrieves the count of visible nodes.
   *
   * @returns {number | null} - The count of visible nodes or null if unavailable
   */
  private getNodesCount(): number | null {
    return this.graphData.visibleNodeCount();
  }

  /**
   * Retrieves the count of visible edges.
   *
   * @returns {number | null} - The count of visible edges or null if unavailable
   */
  private getEdgesCount(): number | null {
    return this.graphData.visibleEdgeCount();
  }

  /**
   * Retrieves the count of layout iterations.
   *
   * @returns {number | null} - The count of layout iterations or null if unavailable
   */
  private getIteration(): number | null {
    return this.layoutService.getIterationCount();
  }

  /**
   * Retrieves the density of the graph.
   *
   * @returns {number | null} - Density of the graph or null if unavailable.
   */
  private getDensity(): number | null {
    return this.sigmaGraph.graph ? density(this.sigmaGraph.graph) : null;
  }

  /**
   * Retrieves the diameter of the graph.
   *
   * @returns {number | null} - Diameter of the graph or null if unavailable.
   */
  private getDiameter(): number | null {
    return this.sigmaGraph.graph ? diameter(this.sigmaGraph.graph) : null;
  }

  /**
   * Retrieves the extent of node sizes in the graph.
   *
   * @param {keyof NodeAttributes} [attribute] - Node attribute to use for size. Default is `"size"`
   * @returns {[number, number] | null} - Tuple [min, max] sizes of nodes or null if unavailable.
   */
  private getNodeSizeExtent(attribute: keyof NodeAttributes = "size"): [number, number] | null {
    return this.sigmaGraph.graph ? nodeExtent(this.sigmaGraph.graph, attribute) : null;
  }

  /**
   * Retrieves the extent of edge weights in the graph.
   *
   * @param {keyof EdgeAttributes} [attribute] - Edge attribute to use for weight. Default is "size". Default is
   *   `"size"`
   * @returns {[number, number] | null} - Tuple [min, max] weights of edges or null if unavailable.
   */
  private getEdgeExtent(attribute: keyof EdgeAttributes = "size"): [number, number] | null {
    return this.sigmaGraph.graph ? edgeExtent(this.sigmaGraph.graph, attribute) : null;
  }

  /**
   * Retrieves the modularity of the graph based on community structures.
   *
   * @param {any} [options] - Options for the modularity function. Default is `{ getNodeCommunity: "group" }`
   * @returns {number | null} - Modularity score of the graph or null if unavailable.
   */
  private getModularity(options: any = { getNodeCommunity: "group" }): number | null {
    // Modularity can only be computed on non-mixed graphs
    if (this.sigmaGraph.graph?.type === "mixed") return null;
    return this.sigmaGraph.graph ? modularity(this.sigmaGraph.graph, options) : null;
  }

  /**
   * Retrieves the unweighted size of the graph, which is equal to the number of edges.
   *
   * @returns {number | null} - Unweighted size of the graph or null if unavailable.
   */
  private getSimpleSize(): number | null {
    return this.sigmaGraph.graph ? simpleSize(this.sigmaGraph.graph) : null;
  }

  /**
   * Retrieves the weighted size of the graph, which is the sum of the weights of all edges.
   *
   * @returns {number | null} - Weighted size of the graph or null if unavailable.
   */
  private getWeightedSize(): number | null {
    return this.sigmaGraph.graph ? weightedSize(this.sigmaGraph.graph) : null;
  }

  /**
   * Retrieves the weighted degree of a hovered node in the graph.
   *
   * @param {EdgeWeightGetter} [getEdgeWeight] - Function or attribute to get the weight of an edge. Default is "size".
   *   Default is `"size"`
   * @returns {number | null} - Weighted degree of the hovered node or null if unavailable.
   */
  private getWeightedDegree(getEdgeWeight: EdgeWeightGetter = "size"): number | null {
    return this.sigmaGraph.graph && this.graphData.hoveredNode
      ? weightedDegree(this.sigmaGraph.graph, this.graphData.hoveredNode, getEdgeWeight)
      : null;
  }

  /**
   * Retrieves the eccentricity of a hovered node in the graph.
   *
   * @returns {number | null} - Eccentricity of the hovered node or null if unavailable.
   */
  private getEccentricity(): number | null {
    return this.sigmaGraph.graph && this.graphData.hoveredNode
      ? eccentricity(this.sigmaGraph.graph, this.graphData.hoveredNode)
      : null;
  }

  /**
   * Retrieves the edge uniformity of the graph.
   *
   * @returns {number | null} - Edge uniformity of the graph or null if unavailable.
   */
  private getEdgeUniformity(): number | null {
    return this.sigmaGraph.graph ? edgeUniformity(this.sigmaGraph.graph) : null;
  }

  /**
   * Retrieves the neighborhood preservation of the graph.
   *
   * @returns {number | null} - Neighborhood preservation of the graph or null if unavailable.
   */
  private getNeighborhoodPreservation(): number | null {
    return this.sigmaGraph.graph ? neighborhoodPreservation(this.sigmaGraph.graph) : null;
  }

  /**
   * Retrieves the stress of the graph layout.
   *
   * @returns {number | null} - Stress of the graph or null if unavailable.
   */
  private getStress(): number | null {
    return this.sigmaGraph.graph ? stress(this.sigmaGraph.graph) : null;
  }

  /**
   * Retrieves the disparity measure for each edge in the graph.
   *
   * @param {any} [options] - Options for the disparity function. Default is `{ getEdgeWeight: "size" }`
   * @returns {{ [edge: string]: number } | null} - Object mapping edges to their disparity measures or null if
   *   unavailable.
   */
  private getDisparity(options: any = { getEdgeWeight: "size" }): { [edge: string]: number } | null {
    return this.sigmaGraph.graph ? disparity(this.sigmaGraph.graph, options) : null;
  }

  /**
   * Retrieves the Simmelian strength for each edge in the graph.
   *
   * @returns {{ [edge: string]: number } | null} - Object mapping edges to their Simmelian strengths or null if
   *   unavailable.
   */
  private getSimmelianStrength(): { [edge: string]: number } | null {
    return this.sigmaGraph.graph ? simmelianStrength(this.sigmaGraph.graph) : null;
  }

  /**
   * Retrieves the betweenness centrality for each node in the graph.
   *
   * @param {any} [options] - Options for the betweenness centrality function. Default is `{ getEdgeWeight: "size",
   *   nodeCentralityAttribute: "size" }`
   * @returns {{ [node: string]: number } | null} - Object mapping nodes to their betweenness centrality scores or null
   *   if unavailable.
   */
  private getBetweennessCentrality(
    options: any = { getEdgeWeight: "size", nodeCentralityAttribute: "size" }
  ): { [node: string]: number } | null {
    return this.sigmaGraph.graph ? betweennessCentrality(this.sigmaGraph.graph, options) : null;
  }

  /**
   * Retrieves the edge betweenness centrality for each edge in the graph.
   *
   * @param {any} [options] - Options for the edge betweenness centrality function. Default is `{ getEdgeWeight: "size",
   *   edgeCentralityAttribute: "size" }`
   * @returns {{ [edge: string]: number } | null} - Object mapping edges to their edge betweenness centrality scores or
   *   null if unavailable.
   */
  private getEdgeBetweennessCentrality(
    options: any = { getEdgeWeight: "size", edgeCentralityAttribute: "size" }
  ): { [edge: string]: number } | null {
    return this.sigmaGraph.graph ? edgeBetweennessCentrality(this.sigmaGraph.graph, options) : null;
  }

  /**
   * Retrieves the closeness centrality for each node in the graph.
   *
   * @param {any} [options] - Options for the closeness centrality function. Default is `{ nodeCentralityAttribute:
   *   "size" }`
   * @returns {{ [node: string]: number } | null} - Object mapping nodes to their closeness centrality scores or null if
   *   unavailable.
   */
  private getClosenessCentrality(
    options: any = { nodeCentralityAttribute: "size" }
  ): { [node: string]: number } | null {
    return this.sigmaGraph.graph ? closenessCentrality(this.sigmaGraph.graph, options) : null;
  }

  /**
   * Retrieves the degree centrality for each node in the graph.
   *
   * @param {any} [options] - Options for the degree centrality function. Default is `{ nodeCentralityAttribute: "size"
   *   }`
   * @returns {{ [node: string]: number } | null} - Object mapping nodes to their degree centrality scores or null if
   *   unavailable.
   */
  private getDegreeCentrality(options: any = { nodeCentralityAttribute: "size" }): { [node: string]: number } | null {
    return this.sigmaGraph.graph ? degreeCentrality(this.sigmaGraph.graph, options) : null;
  }

  /**
   * - Retrieves the eigenvector centrality for each node in the graph.
   *
   * @param {any} [options] - Options for the eigenvector centrality function. Default is `{ getEdgeWeight: "size",
   *   nodeCentralityAttribute: "size" }`
   * @returns {{ [node: string]: number } | null} - Object mapping nodes to their eigenvector centrality scores or null
   *   if unavailable.
   */
  private getEigenvectorCentrality(
    options: any = { getEdgeWeight: "size", nodeCentralityAttribute: "size" }
  ): { [node: string]: number } | null {
    return this.sigmaGraph.graph ? eigenvectorCentrality(this.sigmaGraph.graph, options) : null;
  }

  /**
   * Retrieves the HITS (Hubs and Authorities) scores for each node in the graph.
   *
   * @param {any} [options] - Options for the HITS function. Default is `{ getEdgeWeight: "size" }`
   * @returns {{ hubs: { [node: string]: number }; authorities: { [node: string]: number } } | null} - Object mapping
   *   nodes to their HITS scores or null if unavailable.
   */
  private getHits(
    options: any = {
      getEdgeWeight: "size",
    }
  ): {
    hubs: {
      [node: string]: number;
    };
    authorities: {
      [node: string]: number;
    };
  } | null {
    return this.sigmaGraph.graph ? hits(this.sigmaGraph.graph, options) : null;
  }

  /**
   * Retrieves the PageRank scores for each node in the graph.
   *
   * @param {any} [options] - Options for the PageRank function. Default is `{ nodePagerankAttribute: "size",
   *   getEdgeWeight: "size" }`
   * @returns {{ [node: string]: number } | null} - Object mapping nodes to their PageRank scores or null if
   *   unavailable.
   */
  private getPagerank(options: any = { nodePagerankAttribute: "size", getEdgeWeight: "size" }): {
    [node: string]: number;
  } | null {
    return this.sigmaGraph.graph ? pagerank(this.sigmaGraph.graph, options) : null;
  }
}
