import { Settings } from "sigma/settings";
import { Color } from "../shared/utils/color.util";
import { ArrayColorMap, EdgePrograms, GroupColorMap, NodePrograms } from "./graph.types";
import { Attributes, GraphType } from "graphology-types";

/**
 * Extends the sigma.js Settings interface to include custom settings for graph rendering.
 *
 * @remarks
 *   - We omit the color settings from sigma.js because we want to use our own color type.
 */
export interface GraphSettings
  extends Omit<Settings, "defaultNodeColor" | "defaultEdgeColor" | "labelColor" | "edgeLabelColor"> {
  /** Flag to toggle whether node labels should be forced to be visible. */
  forceNodeLabels: boolean;

  /** Flag to toggle whether edge labels should be forced to be visible. */
  forceEdgeLabels: boolean;

  /** Flag to toggle the highlighting of neighboring elements on node select */
  highlightNeighboringElementsOnNodeSelect: boolean;

  /** Flag to toggle node hover events on or off. */
  enableNodeHoverEvents: boolean;

  /** Flag to toggle node click events on or off. */
  enableNodeClickEvents: boolean;

  /** Flag to determine whether we want to use area-dependent scaling. */
  areaDependentScaling: boolean;

  /** Path where asset files are located. */
  assetsPath: string;

  /** Default size for nodes. */
  defaultNodeSize: number;

  /** Default weight for edges. */
  defaultEdgeWeight: number;

  /** Default color for nodes. */
  defaultNodeColor: Color;

  /** Default color for edges. */
  defaultEdgeColor: Color;

  /**
   * Node Label Colors.
   *
   * @remarks
   *   The color can be either directly or based on an attribute.
   *
   *   - If `attribute` is provided, uses the attribute value as color, falling back to `color` if attribute is missing.
   *   - If `color` is provided directly, `attribute` will be undefined.
   */
  labelColor: ColorOrAttribute;

  /**
   * Node Label Colors.
   *
   * @remarks
   *   The color can be either directly or based on an attribute.
   *
   *   - If `attribute` is provided, uses the attribute value as color, falling back to `color` if attribute is missing.
   *   - If `color` is provided directly, `attribute` will be undefined.
   */
  edgeLabelColor: ColorOrAttribute;

  /**
   * Color map that can be either an array or a map of group colors.
   *
   * @type {ArrayColorMap | GroupColorMap}
   */
  colorMap: ArrayColorMap | GroupColorMap;

  /** Default rendering program for nodes. */
  defaultNodeProgram: NodePrograms;

  /** Default rendering program for undirected edges. */
  defaultUndirectedEdgeType: EdgePrograms;

  /** Default rendering program for directed edges. */
  defaultDirectedEdgeType: EdgePrograms;

  /**
   * Mode to color edges, can be either "bySource" or "byTarget".
   *
   * @type {"bySource" | "byTarget"}
   */
  colorEdgeMode: "bySource" | "byTarget";

  /** Flag to allow or disallow self-loop edges. */
  allowSelfLoops: boolean;

  /** Flag to ignore or consider duplicate edges. */
  ignoreDuplicatedEdges: boolean;

  /** Specifies the type of the graph. */
  graphType: GraphType;

  /** Name for the group when it's unknown. */
  unknownGroupName: string;

  /** Duration of the zoom animation in milliseconds. */
  zoomAnimationDuration: number;

  /** Factor by which to zoom. */
  zoomFactor: number;

  /**
   * Flag to enable or disable the use of styled node hover effects. When set to true, node hover styles are applied
   * according to `customNodeHoverProperties`. When set to false, default styling is applied.
   *
   * @type {boolean}
   */
  highlightNeighboursOnNodeHover: boolean;

  /**
   * Flag to enable or disable node dragging. When set to true, nodes can be dragged around the canvas. When set to
   * false, nodes are static and cannot be dragged.
   *
   * @type {boolean}
   */
  draggableNodes: boolean;

  /** X-Padding around the label. */
  labelPaddingX: number;

  /** Y-Padding around the hover label. */
  labelPaddingY: number;

  /** Gap between the node and the label */
  labelGap: number;

  /** Custom properties for hover labels. */
  prettyLabelsHoverConfiguration: prettyHoverLabelConfiguration;

  /** Custom properties for drawn labels. */
  prettyLabelsConfiguration: prettyLabelsConfiguration;

  /** Custom properties for node and edge styles on hover. */
  prettyNodeHoverConfiguration: prettyNodeHoverConfiguration;
}

/**
 * Type definition for custom properties of hover labels.
 *
 * @typedef {object}
 * @property
 */
export type prettyHoverLabelConfiguration = {
  /** Text color when the label is muted. */
  mutedTextColor: Color;

  /** Background color of the hover label. */
  backgroundColor: Color;

  /** Horizontal shadow offset. */
  shadowOffsetX: number;

  /** Vertical shadow offset. */
  shadowOffsetY: number;

  /** Shadow blur amount. */
  shadowBlur: number;

  /** Shadow color. */
  shadowColor: Color;

  /** Corner radius for the hover label. */
  cornerRadius: number;
};

/**
 * Type definition for custom properties of drawn labels.
 *
 * @typedef {object}
 * @property
 */
export type prettyLabelsConfiguration = {
  /** Background color for the label. */
  labelBackgroundColor: Color;

  /** Opacity for the label background. */
  labelBackgroundOpacity: number;
};

/**
 * Type definition for custom properties of node and edge styles on hover.
 *
 * @typedef {object}
 * @property
 */
export type prettyNodeHoverConfiguration = {
  /**
   * Style settings for an active hovered node.
   *
   * @type {Attributes}
   */
  activeNodeStyle: Attributes;

  /**
   * Style settings for an inactive node when another node is hovered.
   *
   * @type {Attributes}
   */
  inactiveNodeStyle: Attributes;

  /**
   * Style settings for an edge connected to a hovered node.
   *
   * @type {Attributes}
   */
  activeEdgeStyle: Attributes;

  /**
   * Style settings for an inactive edge when another node is hovered.
   *
   * @type {Attributes}
   */
  inactiveEdgeStyle: Attributes;
};

/**
 * Type used to specify colors. The color can be either assigned directly or based on an attribute.
 *
 * @remarks
 *   - If `attribute` is provided, uses the attribute value as color, falling back to `color` if attribute is missing.
 *   - If `color` is provided directly, `attribute` will be undefined.
 */
export type ColorOrAttribute =
  | {
      attribute: string;
      color?: Color;
    }
  | {
      color: Color;
      attribute?: undefined;
    };
