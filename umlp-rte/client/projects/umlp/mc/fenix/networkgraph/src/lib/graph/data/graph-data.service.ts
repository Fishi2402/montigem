import { Injectable, signal, WritableSignal } from "@angular/core";
import { EdgeAttributes, NodeAttributes } from "../types/graph.types";
import { Attributes, SerializedEdge, SerializedGraph, SerializedNode } from "graphology-types";
import { CameraState } from "sigma/types";
import {
  ComplexGraphLayoutConfiguration,
  InitialGraphLayoutConfiguration,
  InitialGraphLayouts,
} from "../types/graph-layout.types";
import { SigmaGraphService } from "./sigma-graph.service";
import { GroupInformationMap, SearchInformationArray } from "../types/filter-group.type";
import { Observable, Subject } from "rxjs";
import { GraphSettingsService } from "../services/graph-settings.service";
import Graph from "graphology";

/**
 * The `GraphDataService` is a service responsible for managing and manipulating graph data. It provides a centralized
 * point of access to the graph data, ensuring data integrity and consistency. The service interacts with other services
 * such as GraphProcessorService and SigmaGraphService to process and visualize the graph data.
 *
 * @template N - Type of the Node Attributes
 * @template E - Type of the Edge Attributes
 * @template G - Type of the Graph Attributes
 */
@Injectable()
export class GraphDataService<N extends NodeAttributes, E extends EdgeAttributes, G extends Attributes> {
  /** Holds the initial graph layout configuration. */
  public initialLayoutConfiguration: InitialGraphLayoutConfiguration | null = { mode: InitialGraphLayouts.CIRCULAR };

  /** Holds the complex graph layout configuration. */
  public complexLayoutConfiguration: ComplexGraphLayoutConfiguration | null = null;

  /**
   * Processed and serialized nodes for rendering in SigmaJS. Holds an array of serialized nodes or null if no nodes
   * have been processed.
   */
  public processedNodes: SerializedNode<N>[] | null = null;

  /**
   * A map containing the relationship between group names and their respective member count. The keys represent group
   * names and values represent the member count.
   */
  public groupMemberCountMap: GroupInformationMap = new Map();

  /**
   * An array holding search-related information for each node group. Each entry contains a label, the group name and
   * associated search information.
   */
  public nodeSearchInformation: SearchInformationArray = [];

  /**
   * Processed and serialized edges for rendering in SigmaJS. Holds an array of serialized edges or null if no edges
   * have been processed.
   */
  public processedEdges: SerializedEdge<E>[] | null = null;

  /**
   * Indicates whether the graph is a multi-graph or not. This property is computed automatically based on the graph
   * data. To modify this behavior, set the {@link ignoreDuplicatedEdges} property to false using the
   * {@link GraphSettingsService}.
   */
  public isMultiGraph: boolean = false;

  /**
   * A set containing unique keys for each processed node, used for deduplication. Each key is a string that uniquely
   * identifies a node.
   */
  public uniqueNodeKeySet: Set<string> = new Set();

  /**
   * A set containing unique keys for each processed edge, used for deduplication. Each key is a string that uniquely
   * identifies an edge.
   */
  public uniqueEdgeKeySet: Set<string> = new Set();

  /** Subject to notify other parts of the app about data changes. */
  private dataChangeSubject: Subject<void> = new Subject();

  /** Observable to subscribe to for data change notifications. */
  public onDataChanged$: Observable<void> = this.dataChangeSubject.asObservable();

  constructor(
    private graphSettings: GraphSettingsService,
    private sigmaGraph: SigmaGraphService<N, E, G>
  ) {}

  /**
   * Updates the "forceLabel" attribute for each node and edge in the graph based on the
   * {@link graphSettings.forceNodeLabels} (respective {@link graphSettings.forceEdgeLabels}) setting.
   */
  public updateForceLabels(): void {
    // Update forcing of node labels
    const shouldForceNodeLabels: boolean = this.graphSettings.getSetting("forceNodeLabels");
    this.graph?.forEachNode((nodeKey: string): void => {
      this.graph?.setNodeAttribute(nodeKey, "forceLabel", shouldForceNodeLabels);
    });

    // Update forcing of edge labels
    const shouldForceEdgeLabels: boolean = this.graphSettings.getSetting("forceEdgeLabels");
    this.graph?.forEachEdge((edgeKey: string): void => {
      this.graph?.setEdgeAttribute(edgeKey, "forceLabel", shouldForceEdgeLabels);
    });
  }

  /** Holds The serialized graph data. */
  private _serializedGraph: WritableSignal<Partial<SerializedGraph<N, E, G>> | null> = signal(null);

  /**
   * Gets the serialized graph.
   *
   * @returns {Partial<SerializedGraph<N, E, G>> | null} - Current serialized graph data.
   */
  public get serializedGraph(): Partial<SerializedGraph<N, E, G>> | null {
    return this._serializedGraph();
  }

  /**
   * Sets the serialized graph.
   *
   * @param {SerializedGraph<N, E, G> | null} graph - New serialized graph data.
   */
  public set serializedGraph(graph: Partial<SerializedGraph<N, E, G>> | null) {
    this._serializedGraph.set(graph);
  }

  /** Holds The camera state for graph visualization. */
  private _cameraState: WritableSignal<Partial<CameraState> | null> = signal(null);

  /**
   * Gets the current camera state.
   *
   * @returns {Partial<CameraState> | null} - Current camera state.
   */
  public get cameraState(): Partial<CameraState> | null {
    return this._cameraState();
  }

  /**
   * Sets the camera state.
   *
   * @param {Partial<CameraState> | null} state - New camera state.
   */
  public set cameraState(state: Partial<CameraState> | null) {
    this._cameraState.set(state);
  }

  /**
   * A writable signal to store the key of the currently highlighted node.
   *
   * @type {WritableSignal<string | null>}
   */
  private _highlightNode: WritableSignal<string | null> = signal(null);

  /**
   * Gets the key of the currently highlighted node.
   *
   * @returns {string | null} - The key of the highlighted node, or null if no node is highlighted.
   */
  public get highlightNode(): string | null {
    return this._highlightNode();
  }

  /**
   * Removes the highlight attribute from the specified node and its neighbors.
   *
   * @param {string} nodeKey - The key of the node to un-highlight.
   */
  private unHighlightNodeAndNeighbors(nodeKey: string): void {
    // Remove highlight attribute from the node itself
    this.graph?.removeNodeAttribute(nodeKey, "highlighted");

    // Don't hide labels if forceLabels is enabled
    if (this.graphSettings.getSetting("forceNodeLabels")) return;

    // Get neighbors of the node
    const neighbors: string[] | undefined = this.graph?.neighbors(nodeKey);

    // Remove the forceLabel attribute from the neighbors and the edges between the node and its neighbors
    neighbors?.forEach((neighbor: string): void => {
      this.graph?.removeNodeAttribute(neighbor, "forceLabel");
      const edgeId: string | undefined = this.graph?.edge(nodeKey, neighbor);
      if (edgeId) this.graph?.removeEdgeAttribute(edgeId, "forceLabel");
    });
  }

  /**
   * Adds the highlight attribute to the specified node and its neighbors.
   *
   * @param {string} nodeKey - The key of the node to highlight.
   */
  private highlightNodeAndNeighbors(nodeKey: string): void {
    // Set highlight attribute for the node itself
    this.graph?.setNodeAttribute(nodeKey, "highlighted", true);

    // Don't manually update "forceLabel" when the setting is globally enabled
    if (this.graphSettings.getSetting("forceNodeLabels")) return;

    // Get neighbors of the node
    const neighbors: string[] | undefined = this.graph?.neighbors(nodeKey);

    // Set the forceLabel attribute for the neighbors and the edges between the node and its neighbors
    neighbors?.forEach((neighbor: string): void => {
      this.graph?.setNodeAttribute(neighbor, "forceLabel", true);
      const edgeId: string | undefined = this.graph?.edge(nodeKey, neighbor);
      if (edgeId) this.graph?.setEdgeAttribute(edgeId, "forceLabel", true);
    });
  }

  /**
   * Sets the key of the currently highlighted node and optionally highlights its neighbors. This function un-highlights
   * the current node and its neighbors, if any, before highlighting the new node.
   *
   * @param {string | null} value - The key of the node to highlight, or null to un-highlight the current node.
   */
  public set highlightNode(value: string | null) {
    // Retrieve the setting for highlighting neighboring elements
    const shouldHighlightNeighboringElements: boolean = this.graphSettings.getSetting(
      "highlightNeighboringElementsOnNodeSelect"
    );

    // Check if the node to highlight is different from the currently highlighted node
    if (value !== this.highlightNode) {
      // Un-highlight the currently highlighted node and its neighbors, if any
      if (shouldHighlightNeighboringElements && this.highlightNode) {
        this.unHighlightNodeAndNeighbors(this.highlightNode);
      }

      // Highlight the new node and its neighbors, if any
      if (shouldHighlightNeighboringElements && value) {
        this.highlightNodeAndNeighbors(value);
      }

      // Update the internal signal for tracking the highlighted node
      this._highlightNode.set(value);

      // Notify subscribers of the data change
      this.notifyDataChange();
    }
  }

  /**
   * A writable signal to store the key of the currently selected node.
   *
   * @type {WritableSignal<string | null>}
   */
  private _selectedNode: WritableSignal<string | null> = signal(null);

  /**
   * Gets the key of the currently selected node.
   *
   * @returns {string | null} - The key of the selected node, or null if no node is selected.
   */
  public get selectedNode(): string | null {
    return this._selectedNode();
  }

  /**
   * Sets the key of the currently selected node.
   *
   * @param {string | null} value - The key of the node to select, or null to un-select the current node.
   */
  public set selectedNode(value: string | null) {
    if (!this.graphSettings.getSetting("enableNodeClickEvents")) {
      this._selectedNode.set(null);
      this.highlightNode = null;
    } else if (value !== this.selectedNode && this.nodeIsVisible(value)) {
      this._selectedNode.set(value);
      this.highlightNode = value;
      this.notifyDataChange();
    }
  }

  /**
   * A writable signal to store the key of the currently selected edge.
   *
   * @type {WritableSignal<string | null>}
   */
  private _selectedEdge: WritableSignal<string | null> = signal(null);

  /**
   * Gets the key of the currently selected edge.
   *
   * @returns {string | null} - The key of the selected edge, or null if no edge is selected.
   */
  public get selectedEdge(): string | null {
    return this._selectedEdge();
  }

  /**
   * Sets the key of the currently selected edge.
   *
   * @param {string | null} value - The key of the edge to select, or null to un-select the current edge.
   */
  public set selectedEdge(value: string | null) {
    const shouldForceEdgeLabels: boolean = this.graphSettings.getSetting("forceEdgeLabels");
    if (!this.graphSettings.getSetting("enableEdgeEvents")) {
      // Hide label of previously selected edge (only if forceLabels is disabled)
      if (!shouldForceEdgeLabels && this.graph?.hasEdge(this._selectedEdge())) {
        this.graph?.setEdgeAttribute(this._selectedEdge(), "forceLabel", false);
      }
      this._selectedEdge.set(null);
    } else if (value !== this.selectedEdge && this.edgeIsVisible(value)) {
      // Hide label of previously selected edge (only if forceLabels is disabled)
      if (!shouldForceEdgeLabels && this.graph?.hasEdge(this._selectedEdge())) {
        this.graph?.setEdgeAttribute(this._selectedEdge(), "forceLabel", false);
      }

      // Force label of currently selected edge (only if forceLabels is disabled)
      if (shouldForceEdgeLabels || this.graph?.hasEdge(value)) {
        this.graph?.setEdgeAttribute(value, "forceLabel", true);
      }

      // Actually select the edge
      this._selectedEdge.set(value);

      // Notify data change
      this.notifyDataChange();
    }
  }

  /**
   * A writable signal to store the key of the currently dragged node.
   *
   * @type {WritableSignal<string | null>}
   */
  private _draggedNode: WritableSignal<string | null> = signal(null);

  /**
   * Gets the key of the currently dragged node.
   *
   * @returns {string | null} - The key of the dragged node, or null if no node is being dragged.
   */
  public get draggedNode(): string | null {
    return this._draggedNode();
  }

  /**
   * Sets the key of the currently dragged node.
   *
   * @param {string | null} value - The key of the node to drag, or null to stop dragging the current node.
   */
  public set draggedNode(value: string | null) {
    if (!this.graphSettings.getSetting("draggableNodes")) {
      this._draggedNode.set(null);
      this.isDragging = false;
    } else if (value !== this.draggedNode && this.nodeIsVisible(value)) {
      this._draggedNode.set(value);
      if (value) this.isDragging = true;
      this.notifyDataChange();
    }
  }

  /**
   * A writable signal to indicate whether a node is being dragged.
   *
   * @type {WritableSignal<boolean>}
   */
  private _isDragging: WritableSignal<boolean> = signal(false);

  /**
   * Gets whether a node is being dragged.
   *
   * @returns {boolean} - True if a node is being dragged, false otherwise.
   */
  public get isDragging(): boolean {
    return this._isDragging();
  }

  /**
   * Sets whether a node is being dragged.
   *
   * @param {boolean} value - True to indicate a node is being dragged, false otherwise.
   */
  public set isDragging(value: boolean) {
    if (!this.graphSettings.getSetting("draggableNodes")) {
      this._isDragging.set(false);
    } else if (value !== this.isDragging) {
      this._isDragging.set(value);
      this.notifyDataChange();
    }
  }

  /**
   * A writable signal to store the key of the currently hovered node.
   *
   * @type {WritableSignal<string | null>}
   */
  private _hoveredNode: WritableSignal<string | null> = signal(null);

  /**
   * Gets the key of the currently hovered node.
   *
   * @returns {string | null} - The key of the hovered node, or null if no node is hovered.
   */
  public get hoveredNode(): string | null {
    return this._hoveredNode();
  }

  /**
   * Sets the key of the currently hovered node.
   *
   * @param {string | null} value - The key of the node to hover, or null to un-hover the current node.
   */
  public set hoveredNode(value: string | null) {
    if (!this.graphSettings.getSetting("enableNodeHoverEvents")) {
      this._hoveredNode.set(null);
    } else if (value !== this.hoveredNode && this.nodeIsVisible(value)) {
      this._hoveredNode.set(value);
      this.notifyDataChange();
    }
  }

  /**
   * A writable signal to store the key of the currently hovered edge.
   *
   * @type {WritableSignal<string | null>}
   */
  private _hoveredEdge: WritableSignal<string | null> = signal(null);

  /**
   * Gets the key of the currently hovered edge.
   *
   * @returns {string | null} - The key of the hovered edge, or null if no edge is hovered.
   */
  public get hoveredEdge(): string | null {
    return this._hoveredEdge();
  }

  /**
   * Sets the key of the currently hovered edge.
   *
   * @param {string | null} value - The key of the edge to hover, or null to un-hover the current edge.
   */
  public set hoveredEdge(value: string | null) {
    if (!this.graphSettings.getSetting("enableEdgeEvents")) {
      this._hoveredEdge.set(null);
    } else if (value !== this.hoveredEdge && this.edgeIsVisible(value)) {
      this._hoveredEdge.set(value);
      this.notifyDataChange();
    }
  }

  /**
   * Indicates whether the graph has nodes.
   *
   * @returns {boolean} - True if the graph has nodes, false otherwise.
   */
  public get hasNodes(): boolean {
    return !!this.processedNodes?.length ?? false;
  }

  /**
   * Indicates whether the graph has visible nodes.
   *
   * @returns {boolean} - True if the graph has visible nodes, false otherwise.
   */
  public get hasVisibleNodes(): boolean {
    return this.visibleNodeCount() !== null;
  }

  /**
   * Provides access to the graph data.
   *
   * @returns {Graph<N, E, G> | null} - The graph data, or null if no graph data is available.
   */
  private get graph(): Graph<N, E, G> | null {
    return this.sigmaGraph.graph;
  }

  /** Resets the stored node data to its initial state. */
  public resetNodeData(): void {
    this.uniqueNodeKeySet.clear();
    this.processedNodes = null;
    this.groupMemberCountMap = new Map();
    this.nodeSearchInformation = [];
  }

  /** Resets the stored edge data to its initial state. */
  public resetEdgeData(): void {
    this.isMultiGraph = false;
    this.processedEdges = null;
    this.uniqueEdgeKeySet.clear();
  }

  /**
   * Shows the specified group keys in the graph.
   *
   * @param {Set<string> | string[] | null} groupKeys - Group keys to be shown.
   */
  public showGroupKeys(groupKeys: Set<string> | string[] | null): void {
    if (!groupKeys) return;

    const groupKeysSet: Set<string> = new Set(groupKeys);

    this.graph?.updateEachNodeAttributes((nodeKey: string, attributes: N) => {
      attributes.hidden = !groupKeysSet.has(attributes.group ?? this.getUnknownGroupName());
      if (this.selectedNode === nodeKey && attributes.hidden) this.selectedNode = null;
      return attributes;
    });

    this.notifyDataChange();
  }

  /**
   * Retrieves the name of the unknown group.
   *
   * @returns {string} - The name of the unknown group
   */
  public getUnknownGroupName(): string {
    return this.graphSettings.getSetting("unknownGroupName");
  }

  /**
   * Calculates and returns the total number of visible nodes in the graph.
   *
   * @returns {number | null} - The total number of nodes in the graph or null if not available.
   */
  public visibleNodeCount(): number | null {
    if (!this.processedNodes?.length ?? null) return null;
    return (
      this.sigmaGraph.graph?.filterNodes((_: string, attributes: Attributes) => !attributes["hidden"]).length ?? null
    );
  }

  /**
   * Fetches and returns the group information including name and number of items for each group.
   *
   * @returns {GroupInformationMap} - The information about all groups.
   */
  public getNodeFilterGroups(): GroupInformationMap {
    return this.groupMemberCountMap;
  }

  /**
   * Fetches and returns the information about the nodes that can be used for searching.
   *
   * @returns {SearchInformationArray} - The information about all nodes used for searching.
   */
  public getNodeSearchInformation(): SearchInformationArray {
    return this.nodeSearchInformation;
  }

  /**
   * Calculates and returns the total number of visible edges in the graph.
   *
   * @returns {number | null} - The total number of edges in the graph or null if not available.
   */
  public visibleEdgeCount(): number | null {
    if (!this.processedEdges?.length ?? null) return null;
    return (
      this.sigmaGraph.graph?.filterEdges((_: string, attributes: Attributes) => !attributes["hidden"]).length ?? null
    );
  }

  /**
   * Fetches and returns the serialized graph data.
   *
   * @returns {Partial<SerializedGraph<N, E, G>>} - Serialized graph data.
   */
  public getGraphData(): Partial<SerializedGraph<N, E, G>> {
    return {
      edges: this.processedEdges ?? [],
      nodes: this.processedNodes ?? [],
    };
  }

  /** Notifies subscribers about data changes. */
  public notifyDataChange(): void {
    this.dataChangeSubject.next();
  }

  /**
   * Checks if a node is visible based on its key.
   *
   * @param {string | null} nodeKey - Key of the node to be checked.
   * @returns {boolean} - Whether the node is visible.
   */
  private nodeIsVisible(nodeKey: string | null): boolean {
    return !nodeKey || (this.graph ? !this.graph.getNodeAttribute(nodeKey, "hidden") : false);
  }

  /**
   * Checks if an edge is visible based on its key.
   *
   * @param {string | null} edgeKey - Key of the edge to be checked.
   * @returns {boolean} - Whether the edge is visible.
   */
  private edgeIsVisible(edgeKey: string | null): boolean {
    return !edgeKey || (this.graph ? !this.graph.getEdgeAttribute(edgeKey, "hidden") : false);
  }
}
