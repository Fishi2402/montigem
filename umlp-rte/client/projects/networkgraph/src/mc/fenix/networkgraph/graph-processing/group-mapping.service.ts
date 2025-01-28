import { Injectable } from "@angular/core";
import {
  ArrayColorMap,
  EdgeAttributes,
  Internal_GraphEdge,
  Internal_GraphNode,
  GroupColor,
  GroupColorMap,
  NodeAttributes,
} from "../types/graph.types";
import { Color, ColorFormat, ColorUtil, HEX } from "../shared/utils/color.util";
import { Attributes } from "graphology-types";
import { ProcessorServiceTemplate } from "./processor-service-template.service";
import { EdgeProcessorService } from "./edge-processor.service";
import { TypeCheckerUtil } from "../shared/utils/type-checker.util";

/**
 * The `GroupColorService` extends `ProcessorServiceTemplate`, designed to manage group-to-color mappings for nodes and
 * edges based on graph settings. It supports either predefined color mappings or the generation of distinct colors for
 * different groups within the graph data. By associating nodes and edges with specific groups, it facilitates
 * color-coding in the visualization, aiding in distinguishing and filtering graph elements based on their grouping
 * criteria.
 *
 * @template N - Type of the Node Attributes.
 * @template E - Type of the Edge Attributes.
 * @template G - Type of the Graph Attributes.
 */
@Injectable()
export class GroupMappingService<
  N extends NodeAttributes,
  E extends EdgeAttributes,
  G extends Attributes,
> extends ProcessorServiceTemplate<N, E, G> {
  /** A map for storing the association between group names and their color in HEX format. */
  private groupColorMapping: Map<string, HEX> = new Map();

  /** A map for storing the association between node keys and their groups. */
  private nodeGroupMapping: Map<string, string> = new Map();

  /** Resets the group-color and node-group mappings. */
  public reset(): void {
    this.groupColorMapping = new Map();
    this.nodeGroupMapping = new Map();
  }

  /**
   * Associates a node key with a group.
   *
   * @param {string} nodeKey - The key of the node.
   * @param {string} group - The group to associate with the node key.
   */
  public addNodeGroup(nodeKey: string, group: string): void {
    this.nodeGroupMapping.set(nodeKey, group);
  }

  /**
   * Determines the group of a node based on specified attributes.
   *
   * @param {Internal_GraphNode} node - The node to determine the group for.
   * @returns {string} - The group the node belongs to.
   */
  public getNodeGroup(node: Internal_GraphNode): string {
    return (
      ProcessorServiceTemplate.toStringIfDefined(
        ProcessorServiceTemplate.getAttribute(node, "group", "category", "cluster", "type", "tag")
      ) ?? this.unknownGroupName
    );
  }

  /**
   * Assigns colors to groups based on the graph nodes and settings.
   *
   * @param {Internal_GraphNode[]} nodes - The list of nodes to create a mapping for.
   */
  public assignGroupColors(nodes: Internal_GraphNode[]): void {
    this.groupColorMapping.clear();
    const colorMap: ArrayColorMap | GroupColorMap = this.graphSettings.getSetting("colorMap");
    this.groupColorMapping.set(
      this.unknownGroupName,
      ColorUtil.parseIntoHEX(this.graphSettings.getSetting("defaultNodeColor"))
    );

    if (Array.isArray(colorMap) && colorMap.length > 0) {
      if (TypeCheckerUtil.isObject(colorMap[0]) && colorMap[0] != null && "group" in (colorMap[0] as GroupColor)) {
        this.populateGroupColors(colorMap as GroupColorMap);
      } else {
        this.populateDistinctGroupColors(colorMap as ArrayColorMap, nodes);
      }
    }
  }

  /**
   * Determines the group of an edge.
   *
   * @param {Internal_GraphEdge} edge - The edge to determine the group for.
   * @returns {string} - The group the edge belongs to.
   */
  public getEdgeGroup(edge: Internal_GraphEdge): string {
    const node: string =
      this.graphSettings.getSetting("colorEdgeMode") === "bySource"
        ? EdgeProcessorService.getSourceId(edge)
        : EdgeProcessorService.getTargetId(edge);
    return ProcessorServiceTemplate.toStringIfDefined(this.nodeGroupMapping.get(node)) ?? this.unknownGroupName;
  }

  /**
   * Retrieves the color for a specific group.
   *
   * @param {string} group - The group to get the color for.
   * @param {HEX} defaultColor - The default color used as a fallback when color mapping doesn't return anything.
   * @returns {string} - The color assigned to the group.
   */
  public getColorForGroup(group: string | undefined, defaultColor: Color): HEX {
    const parsedColor: HEX = ColorUtil.parseIntoHEX(defaultColor);
    if (!group) return parsedColor;
    return this.groupColorMapping.get(group) || parsedColor;
  }

  /**
   * Populates the group-to-color mapping based on a provided GroupColorMap.
   *
   * @param {GroupColorMap} colorMap - An array of GroupColor objects containing group and color information.
   */
  private populateGroupColors(colorMap: GroupColorMap): void {
    colorMap.forEach((colorObject: GroupColor): void => {
      this.groupColorMapping.set(String(colorObject.group), ColorUtil.parseIntoHEX(colorObject.color));
    });
  }

  /**
   * Populates distinct colors for each group when a color map is not provided or insufficient. It first identifies
   * distinct groups from the provided nodes, then either uses the provided ArrayColorMap or generates distinct colors
   * if the color map is shorter than the number of distinct groups. Finally, it populates the group-to-color mapping
   * with the determined colors.
   *
   * @param {ArrayColorMap} colorMap - An array of colors as HEX strings.
   * @param {Internal_GraphNode[]} nodes - An array of GraphNode objects used to identify distinct groups.
   */
  private populateDistinctGroupColors(colorMap: ArrayColorMap, nodes: Internal_GraphNode[]): void {
    // Identify distinct groups from the nodes
    const distinctGroups: Set<string> = new Set();
    nodes.forEach((node: Internal_GraphNode): void => {
      const group: string = this.getNodeGroup(node);
      if (!this.isGroupUnknown(group)) distinctGroups.add(group);
    });

    // Generate distinct colors if the provided color map is insufficient
    if (distinctGroups.size > colorMap.length) {
      colorMap = ColorUtil.generateDistinctColors(distinctGroups.size, ColorFormat.HEX) as ArrayColorMap;
    }

    // Populate the group-to-color mapping with the determined colors
    let colorIndex: number = 0;
    distinctGroups.forEach((group: string): void => {
      this.groupColorMapping.set(group, ColorUtil.parseIntoHEX(colorMap[colorIndex++]));
    });
  }
}
