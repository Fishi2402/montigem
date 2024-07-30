import { Attributes } from "graphology-types";
import { Color } from "../../shared/utils/color.util";
import { EdgeDisplayData, NodeDisplayData, PartialButFor } from "sigma/types";
import { AnimateOptions } from "sigma/utils/animate";

/** Represents a value that can be a number, string, or boolean */
export type MetricValueType = number | string | boolean;

/** Represents a mapping of metric keys to their corresponding values */
export type MetricInformation = {
  [key: string]: MetricValueType | MetricValueType[] | null | undefined;
};

/** Function type to generate custom metrics, receives the NetworkGraphComponent as an argument */
/*export type CustomMetricsGenerator = (component: GemGraphComponent) => MetricInformation;*/

/** Function type to generate metrics */
export type GenerateMetricsFunction = () => MetricInformation;

/** Array to map group indices to colors. */
export type ArrayColorMap = Color[];

/** Object representing a group and its color. */
export type GroupColor = {
  /** The identifier for the group. */
  group: string | number;

  /** The color for the group in HEX format. */
  color: Color;
};

/** Array of GroupColor objects to map group identifiers to colors. */
export type GroupColorMap = GroupColor[];

/** Type for identifiers. Could be either a string or a number. */
export type IdentifierType = string | number;

/** Object representing possible fields for entity identification. */
export type Identifier = {
  /** Identifier key. */
  key?: IdentifierType;

  /** Identifier ID. */
  id?: IdentifierType;
};

/** Common Hover Attributes (note that the first matching one will be picked as hover text) */
export type HoverAttributes = {
  /** Tooltip for the entity */
  tooltip?: IdentifierType;

  /** Hover Text for the entity */
  hover?: IdentifierType;

  /** Hover Label for the entity */
  hoverLabel?: IdentifierType;
};

/** Common Label attributes (note that the first matching one will be picked as hover text) */
export type LabelAttribute = {
  /** Identifier name. */
  name?: IdentifierType;

  /** Label for the entity. */
  label?: IdentifierType;
};

/** Common attributes shared by nodes and edges. */
export type CommonRawAttributes = Identifier &
  LabelAttribute &
  Attributes &
  HoverAttributes &
  GroupAttributes & {
    /** Size of the entity. */
    size?: number;

    /** Whether the entity is hidden. */
    hidden?: boolean;

    /** Color of the entity. */
    color?: Color;
  };

/** Attributes that might specify a group. */
export type GroupAttributes = {
  /** Type of the group. */
  type?: IdentifierType;

  /** Tag for the group. */
  tag?: IdentifierType;

  /** Category of the group. */
  category?: IdentifierType;

  /** Cluster of the group. */
  cluster?: IdentifierType;

  /** Group identifier. */
  group?: IdentifierType;
};

/** Attributes for identifying the source of an edge. */
export type RawEdgeSourceAttributes = {
  /** Source node identifier (alternative to "source"). */
  from?: IdentifierType;

  /** Source node identifier (alternative to "from"). */
  source?: IdentifierType;
};

/** Attributes for identifying the target of an edge. */
export type RawEdgeTargetAttributes = {
  /** Target node identifier (alternative to "target"). */
  to?: IdentifierType;

  /** Target node identifier (alternative to "to"). */
  target?: IdentifierType;
};

/** Direction-related attributes for edges. */
export type RawEdgeDirectionAttributes = {
  /** Whether the edge is directed. */
  directed?: boolean;

  /** Whether the edge is undirected. */
  undirected?: boolean;
};

/** Supported edge program types */
export type EdgePrograms = "line" | "arrow" | "fastLine";

/** Supported node program types */
export type NodePrograms = "circle" | "image" | "triangle" | "square";

/** Node-specific attributes. */
export type RawNodeSpecificAttributes = {
  /** X-coordinate for the node. */
  x?: number;

  /** Y-coordinate for the node. */
  y?: number;

  /** Whether the given node position is fixed */
  fixed?: boolean;

  /** Program used to render the node. */
  nodeProgram?: NodePrograms;

  /** Link to the image of the node. */
  image?: string;
};

/** Edge-specific attributes. */
export type RawEdgeSpecificAttributes = {
  /** Weight of the edge. */
  weight?: number;

  /** Program used to render the edge. */
  edgeProgram?: EdgePrograms;
};

/**
 * `RawNodeAttributes` are attributes that are directly sourced from raw data. These attributes are allowed and
 * understandable as raw data input.
 *
 * @remarks
 *   These attributes can be processed further to generate 'NodeAttributes', which are used for rendering or other
 *   operations.
 */
export type RawNodeAttributes = Attributes & Identifier & CommonRawAttributes & RawNodeSpecificAttributes;

/**
 * `RawEdgeAttributes` are attributes that are directly sourced from raw data. These attributes are allowed and
 * understandable as raw data input.
 *
 * @remarks
 *   These attributes can be processed further to generate 'EdgeAttributes', which are used for rendering or other
 *   operations.
 */
export type RawEdgeAttributes = Attributes &
  Identifier &
  CommonRawAttributes &
  RawEdgeSourceAttributes &
  RawEdgeTargetAttributes &
  RawEdgeDirectionAttributes &
  RawEdgeSpecificAttributes;

/**
 * Graph representation.
 * Aggregation of nodes and edges.
 */
export type Graph = {
  nodes: GraphNode[];
  edges: GraphEdge[];
}

/**
 * Graph node representation.
 *
 * @remarks
 *   Attributes can be either passed in directly, or within an "attributes" object.
 */
export type GraphNode = RawNodeAttributes & {
  /** Attributes specific to this node. */
  attributes?: RawNodeAttributes;
};

/**
 * Graph edge representation.
 *
 * @remarks
 *   Attributes can be either passed in directly, or within an "attributes" object.
 */
export type GraphEdge = RawEdgeAttributes & {
  /** Attributes specific to this edge. */
  attributes?: RawEdgeAttributes;
};

/**
 * NodeAttributes (Suggested rename: ProcessedNodeAttributes) are the processed attributes used for rendering or other
 * operations. These are usually derived from 'RawNodeAttributes'.
 *
 * @remarks
 *   These attributes are more specialized and are tailored to fit the requirements of the rendering engine or other
 *   specific operations.
 */
export type NodeAttributes = PartialButFor<NodeDisplayData, "x" | "y" | "size" | "label" | "color"> & {
  /** Program type used for rendering the node */
  type: NodePrograms;

  /** Group identifier */
  group: string;

  /** Tooltip text */
  tooltip?: string;

  /** Image link */
  image?: string;
};

/**
 * EdgeAttributes (Suggested rename: ProcessedEdgeAttributes) are the processed attributes used for rendering or other
 * operations. These are usually derived from 'RawEdgeAttributes'.
 *
 * @remarks
 *   These attributes are more specialized and are tailored to fit the requirements of the rendering engine or other
 *   specific operations.
 */
export type EdgeAttributes = PartialButFor<EdgeDisplayData, "size" | "label" | "color"> & {
  /** Program type used for rendering the edge */
  type: EdgePrograms;

  /** Group identifier */
  group: string;

  /** Tooltip text */
  tooltip?: string;
};

/** Type for animated zoom (in/out) options */
export type AnimateZoomOptions =
  | number
  | (Partial<AnimateOptions> & {
      factor?: number;
    });
