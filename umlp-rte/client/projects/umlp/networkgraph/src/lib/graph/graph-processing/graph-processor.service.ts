import { EdgeAttributes, GraphEdge, GraphNode, NodeAttributes } from "../types/graph.types";
import { Attributes } from "graphology-types";
import { Injectable, NgZone } from "@angular/core";
import { EdgeProcessorService } from "./edge-processor.service";
import { NodeProcessorService } from "./node-processor.service";
import { GroupMappingService } from "./group-mapping.service";

/**
 * `GraphProcessorService` is a service responsible for managing and processing raw graph data into processed graph data
 * understandable by `SigmaJS`. to facilitate rendering and interaction within an application. It extends
 * `ProcessorServiceTemplate`, inheriting its processing capabilities, and interacts with `EdgeProcessorService`,
 * `NodeProcessorService`, and `GroupMappingService` to handle the processing of edges, nodes, and group mapping
 * respectively. It provides methods to set raw node and edge data, execute the graph data processing pipeline, and
 * reset the graph data structures to their initial states.
 *
 * @template N - Type of the Node Attributes
 * @template E - Type of the Edge Attributes
 * @template G - Type of the Graph Attributes
 */
@Injectable()
export class GraphProcessorService<N extends NodeAttributes, E extends EdgeAttributes, G extends Attributes> {
  constructor(
    private ngZone: NgZone,
    private edgeProcessor: EdgeProcessorService<N, E, G>,
    private nodeProcessor: NodeProcessorService<N, E, G>,
    private groupMapping: GroupMappingService<N, E, G>
  ) {}

  /**
   * Method to set the raw node data for the graph.
   *
   * @param {GraphNode[]} nodes - Array of raw node data.
   */
  public setNodeData(nodes: GraphNode[]): void {
    this.nodeProcessor.rawNodeData = nodes;
  }

  /**
   * Method to set the raw edge data for the graph.
   *
   * @param {GraphEdge[]} edges - Array of raw edge data.
   */
  public setEdgeData(edges: GraphEdge[]): void {
    this.edgeProcessor.rawEdgeData = edges;
  }

  /** Executes the entire graph data processing pipeline. */
  public processGraphData(): void {
    this.ngZone.runOutsideAngular(() => {
      this.reset();

      if (this.nodeProcessor.rawNodeData) {
        this.groupMapping.assignGroupColors(this.nodeProcessor.rawNodeData);
        this.nodeProcessor.processNodeData();
      }

      if (this.edgeProcessor.rawEdgeData) {
        this.edgeProcessor.processEdgeData();
      }
    });
  }

  /** Resets the graph data structures. */
  public reset(): void {
    this.nodeProcessor.reset();
    this.edgeProcessor.reset();
    this.groupMapping.reset();
  }
}
