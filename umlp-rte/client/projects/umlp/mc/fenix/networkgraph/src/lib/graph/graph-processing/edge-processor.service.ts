import { EdgeAttributes, GraphEdge, NodeAttributes } from "../types/graph.types";
import { Attributes, SerializedEdge } from "graphology-types";
import { Injectable } from "@angular/core";
import { ProcessorServiceTemplate } from "./processor-service-template.service";
import { GroupMappingService } from "./group-mapping.service";
import { Color, ColorFormat, ColorUtil } from "../../shared/utils/color.util";

/**
 * The `EdgeProcessorService` extends `ProcessorServiceTemplate` and is designed to convert raw edge data into a format
 * understandable by `SigmaJS`. It inherits core functionalities from the template while introducing mechanisms to
 * handle edge-specific operations. By preprocessing the data, we allow for more flexibility with respect to naming and
 * structure of the input data. For example, the source of an edge can be specified using "source" or "from" attributes
 * (and more could easily be added). The service also handles the generation of unique identifiers for edges (i.e., if
 * no key has been specified). Additionally, it checks for validity (i.e., only allows edges that are connected to
 * existing nodes as well as edges that are not duplicated).
 *
 * @template N - Type of the Node Attributes.
 * @template E - Type of the Edge Attributes.
 * @template G - Type of the Graph Attributes.
 */
@Injectable()
export class EdgeProcessorService<
  N extends NodeAttributes,
  E extends EdgeAttributes,
  G extends Attributes,
> extends ProcessorServiceTemplate<N, E, G> {
  /**
   * Holds the raw edge data received as input. The data format is adapted to allow for flexibility in the naming
   * conventions and structure of graphs.
   */
  public rawEdgeData: GraphEdge[] | null = null;

  /** Counter utilized for generating unique identifiers for edges. */
  private edgeIdCounter: number = 0;

  constructor(private groupMapping: GroupMappingService<N, E, G>) {
    super();
  }

  /**
   * Retrieves the identifier of the source node for a given edge.
   *
   * @param {GraphEdge} edge - The edge whose source node identifier is to be retrieved.
   * @returns {string} - The identifier of the source node of the given edge.
   */
  public static getSourceId(edge: GraphEdge): string {
    return String(ProcessorServiceTemplate.getAttribute(edge, "source", "from"));
  }

  /**
   * Retrieves the identifier of the target node for a given edge.
   *
   * @param {GraphEdge} edge - The edge whose target node identifier is to be retrieved.
   * @returns {string} - The identifier of the target node of the given edge.
   */
  public static getTargetId(edge: GraphEdge): string {
    return String(ProcessorServiceTemplate.getAttribute(edge, "target", "to"));
  }

  /** Resets the edge data structures and the edge identifier counter. */
  public reset(): void {
    this.data.resetEdgeData();
    this.edgeIdCounter = 0;
  }

  /** Processes the raw edge data to conform to the graph model. */
  public processEdgeData(): void {
    this.reset();

    // Initialize all data structures required to process the edges
    const duplicatedEdgeKeys: string[] = [];
    const duplicatedEdges: string[] = [];
    const invalidPairs: string[] = [];
    const selfLoops: string[] = [];
    const sourceTargetMap: Set<string> = new Set();
    const isDirected: boolean = this.graphSettings.getSetting("graphType") === "directed";
    const shouldIgnoreDuplicatedEdges: boolean = this.graphSettings.getSetting("ignoreDuplicatedEdges");

    this.data.processedEdges = [];

    for (const edge of this.rawEdgeData ?? []) {
      // Process each edge individually
      this.processIndividualEdge(
        edge,
        sourceTargetMap,
        duplicatedEdgeKeys,
        duplicatedEdges,
        invalidPairs,
        selfLoops,
        isDirected,
        shouldIgnoreDuplicatedEdges
      );
    }

    this.logEdgeWarnings(duplicatedEdges, duplicatedEdgeKeys, invalidPairs, selfLoops);
  }

  /**
   * Generates a unique identifier for a given edge.
   *
   * @param {GraphEdge} edge - The edge for which a unique identifier is to be generated.
   * @returns {string} - A unique identifier for the given edge.
   */
  private generateEdgeIdentifier(edge: GraphEdge): string {
    const [identifier, newCounter] = ProcessorServiceTemplate.generateIdentifier(
      this.edgeIdCounter,
      this.data.uniqueEdgeKeySet,
      edge,
      "edge"
    );
    this.edgeIdCounter = newCounter;
    return identifier;
  }

  /**
   * Converts a given edge of type {@link GraphEdge} into a {@link SerializedEdge} suitable for use with `SigmaJS`.
   *
   * @param {string} key - The key of the edge
   * @param {GraphEdge} edge - The edge to construct a graph edge from.
   * @returns {SerializedEdge} - The constructed edge.
   */
  private createEdge(key: string, edge: GraphEdge): SerializedEdge<E> {
    const isUndirected: boolean = this.isEdgeUndirected(edge);
    const group: string | undefined = this.groupMapping.getEdgeGroup(edge);
    let color: Color = ColorUtil.lightenColor(
      this.groupMapping.getColorForGroup(group, this.graphSettings.getSetting("defaultEdgeColor")),
      5
    );
    color = ColorUtil.toString(ColorUtil.parseColorIntoFormat(color, ColorFormat.RGB).newColor);

    return {
      key,
      source: EdgeProcessorService.getSourceId(edge),
      target: EdgeProcessorService.getTargetId(edge),
      undirected: isUndirected,
      attributes: {
        ...this.getCommonProperties(edge, key),
        group: group,
        size:
          ProcessorServiceTemplate.getAttribute(edge, "weight", "size") ??
          this.graphSettings.getSetting("defaultEdgeWeight"),
        type:
          ProcessorServiceTemplate.getAttribute(edge, "edgeProgram") ??
          (isUndirected
            ? this.graphSettings.getSetting("defaultUndirectedEdgeType")
            : this.graphSettings.getSetting("defaultDirectedEdgeType")),
        color: color,
      } as E,
    };
  }

  /**
   * Processes a single edge and updates tracking variables.
   *
   * @param {GraphEdge} edge - The raw edge data to be processed.
   * @param {Set<string>} sourceTargetSet - A set to track source and target pairs.
   * @param {string[]} duplicatedEdgesKeys - A list to track duplicated edge keys.
   * @param {string[]} duplicatedEdges - A list to track duplicated edges.
   * @param {string[]} invalidPairs - A list to track invalid source-target pairs.
   * @param {string[]} selfLoops - A list to track self-looped edges with same source and target (if self loops are
   *   disabled in the settings).
   * @param {boolean} isDirected - A flag indicating if the graph is directed.
   * @param {boolean} shouldIgnoreDuplicatedEdges - A flag to determine whether duplicated edges should be ignored.
   */
  private processIndividualEdge(
    edge: GraphEdge,
    sourceTargetSet: Set<string>,
    duplicatedEdgesKeys: string[],
    duplicatedEdges: string[],
    invalidPairs: string[],
    selfLoops: string[],
    isDirected: boolean,
    shouldIgnoreDuplicatedEdges: boolean
  ): void {
    const key: string = this.generateEdgeIdentifier(edge);

    // Check for duplicate edge keys
    if (this.data.uniqueEdgeKeySet.has(key)) {
      duplicatedEdgesKeys.push(key);
      return;
    }

    // Extract source & target IDs and generate keys
    const source: string = EdgeProcessorService.getSourceId(edge);
    const target: string = EdgeProcessorService.getTargetId(edge);
    const sourceTargetKey: string = `${source}-${target}`;
    const targetSourceKey: string = `${target}-${source}`;

    // Check for duplicate edges based on source and target nodes
    const isDuplicatedEdge: boolean = isDirected
      ? sourceTargetSet.has(sourceTargetKey)
      : sourceTargetSet.has(sourceTargetKey) || sourceTargetSet.has(targetSourceKey);

    if (isDuplicatedEdge) {
      if (shouldIgnoreDuplicatedEdges) {
        // If we have a duplicate edge and want to ignore it, we simply return
        duplicatedEdges.push(`(${source}, ${target})`);
        return;
      } else {
        // Otherwise, we must set `isMultiGraph` to true for SigmaJS to properly process it.
        this.data.isMultiGraph = true;
      }
    }

    // Check for invalid source-target pairs, i.e., edges connecting nonexistent nodes
    if (!this.data.uniqueNodeKeySet.has(source) || !this.data.uniqueNodeKeySet.has(target)) {
      invalidPairs.push(`(${source}, ${target})`);
      return;
    }

    // Check for invalid self-loops
    if (source === target && !this.graphSettings.getSetting("allowSelfLoops")) {
      selfLoops.push(`(${source}, ${target})`);
      return;
    }

    // Update the source-target map and unique edge key set to reflect the processed edge
    sourceTargetSet.add(sourceTargetKey);
    if (!isDirected) sourceTargetSet.add(targetSourceKey);
    this.data.uniqueEdgeKeySet.add(key);

    // Update the processed edges list with the newly created edge
    this.data.processedEdges?.push(this.createEdge(key, edge));
  }

  /**
   * Logs warnings related to edges, such as duplicated edges, duplicated edge keys, and invalid edge pairs.
   *
   * @param {string[]} duplicatedEdges - A list of duplicated edges.
   * @param {string[]} duplicatedEdgesKeys - A list of duplicated edge keys.
   * @param {string[]} invalidPairs - A list of invalid source-target pairs.
   * @param {string[]} selfLoops - A list of self-loops (edges with same source and target).
   */
  private logEdgeWarnings(
    duplicatedEdges: string[],
    duplicatedEdgesKeys: string[],
    invalidPairs: string[],
    selfLoops: string[]
  ): void {
    const warnings: string[] = [];

    if (duplicatedEdgesKeys.length > 0) {
      warnings.push(
        `Duplicate edge key(s) detected in a simple graph: ${duplicatedEdgesKeys.join(", ")}. Skipping these edges.`
      );
    }

    if (duplicatedEdges.length > 0) {
      warnings.push(
        `Duplicate edges with same source and target detected in a simple graph: ${duplicatedEdges.join(
          ", "
        )}. Skipping these edges for performance.`
      );
    }

    if (invalidPairs.length > 0) {
      warnings.push(`Invalid source or target pairs detected. Skipping these edges: ${invalidPairs.join(", ")}`);
    }

    if (selfLoops.length > 0) {
      warnings.push(
        `Self-Loops with identical source and target detected while they are forbidden in the settings. Skipping these edges: ${selfLoops.join(
          ", "
        )}`
      );
    }

    if (warnings.length > 0) console.warn(warnings.join(" "));
  }

  /**
   * Determines whether a given edge is undirected.
   *
   * @param {GraphEdge} edge - The edge to be checked.
   * @returns {boolean} - Returns true if the edge is undirected; otherwise, returns false.
   */
  private isEdgeUndirected(edge: GraphEdge): boolean {
    const graphType: string = this.graphSettings.getSetting("graphType");
    if (graphType !== "mixed") return graphType === "undirected";
    const directedAttribute = ProcessorServiceTemplate.getAttribute(edge, "directed");
    const undirectedAttribute = ProcessorServiceTemplate.getAttribute(edge, "undirected");

    if (directedAttribute != null) {
      return !directedAttribute;
    } else if (undirectedAttribute != null) {
      return undirectedAttribute;
    }

    // Undirected by default
    return true;
  }
}
