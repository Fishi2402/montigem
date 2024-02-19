import { ForceLayoutSupervisorParameters } from "graphology-layout-force/worker";
import { ForceAtlas2LayoutParameters } from "graphology-layout-forceatlas2";
import { NoverlapLayoutSupervisorParameters } from "graphology-layout-noverlap/worker";
import { CirclePackLayoutOptions } from "graphology-layout/circlepack";
import { CircularLayoutOptions } from "graphology-layout/circular";
import { RandomLayoutOptions } from "graphology-layout/random";
import Graph from "graphology";
import { Layout } from "../consts/graph.const";

/**
 * Enumeration for initial layout modes. These modes provide a basic layout for the nodes in the graph.
 *
 * @remarks
 *   The differentiation is essential as the {@link ComplexGraphLayoutConfiguration} modes require initial x-y coordinates
 *   to function correctly.
 * @enum {string}
 */
export enum InitialGraphLayouts {
  /**
   * Places nodes in a perfect circle.
   *
   * - Suitable for small graphs focusing on connectivity rather than node attributes.
   * - Nodes are equally spaced along the circumference of a circle.
   */
  CIRCULAR = "CIRCULAR",

  /**
   * Places nodes randomly within the canvas.
   *
   * - Generally not useful for visual analysis but effective for initial scattering.
   * - Nodes are placed without any predictable pattern.
   */
  RANDOM = "RANDOM",

  /**
   * Places nodes in circles packed as closely as possible.
   *
   * - Useful for hierarchical data or to emphasize cluster groups.
   * - Smaller circles are enclosed within larger circles, filling the canvas area.
   */
  CIRCLE_PACK = "CIRCLE_PACK",
}

/**
 * Enumeration for complex layout modes. These modes are used for organizing nodes in a more advanced manner, often
 * based on certain algorithms.
 *
 * @remarks
 *   The {@link ComplexGraphLayoutConfiguration} modes require initial x-y coordinates, provided by one of the
 *   {@link InitialGraphLayouts} modes.
 * @enum {string}
 */
export enum ComplexGraphLayouts {
  /**
   * Utilizes a force-directed algorithm for node placement.
   *
   * - Suitable for larger graphs, optimizing for readability and clustering.
   * - Nodes repel each other while edges act like springs, reaching an equilibrium state.
   */
  FORCE = "FORCE",

  /**
   * An enhanced force-directed algorithm focusing on performance.
   *
   * - Useful for very large graphs, offering better performance than basic force-directed algorithms.
   * - Similar to FORCE but usually faster in reaching an optimized state.
   */
  FORCEATLAS2 = "FORCEATLAS2",

  /**
   * Removes overlap between nodes while maintaining the general shape.
   *
   * - Suitable for graphs where node overlap is a problem, usually as a post-processing step.
   * - Similar to the initial layout but without any overlapping nodes.
   */
  NO_OVERLAP = "NO_OVERLAP",
}

/** Settings for layout iterations. */
export interface LayoutIterationSettings {
  /** Maximum number of iterations. */
  maxIterations?: number;

  /** Maximum time for the layout in milliseconds. */
  maxLayoutTime?: number;
}

/** Type for mapping layout modes to their respective settings. */
export type ComplexLayoutSettingsMap = {
  /** Settings for the layout type `Force` */
  [ComplexGraphLayouts.FORCE]: ForceLayoutSupervisorParameters;

  /** Settings for the layout type `ForceAtlas2` */
  [ComplexGraphLayouts.FORCEATLAS2]: ForceAtlas2LayoutParameters;

  /** Settings for the layout type `No Overlap` */
  [ComplexGraphLayouts.NO_OVERLAP]: NoverlapLayoutSupervisorParameters;
};

export type InitialLayoutSettingsMap = {
  /** Settings for the layout type `Circle Pack` */
  [InitialGraphLayouts.CIRCLE_PACK]: CirclePackLayoutOptions;

  /** Settings for the layout type `Circular` */
  [InitialGraphLayouts.CIRCULAR]: CircularLayoutOptions;

  /** Settings for the layout type ´Random */
  [InitialGraphLayouts.RANDOM]: RandomLayoutOptions;
};

/**
 * Unified type for mapping both initial and complex layout modes to their respective settings.
 *
 * @type {object}
 */
export type LayoutSettingsMap = InitialLayoutSettingsMap & ComplexLayoutSettingsMap;

/**
 * Function type for assigning layouts to a graph based on the specified mode and settings.
 *
 * @template T
 * @type {Function}
 */
export type AssignLayout<T extends keyof LayoutSettingsMap> = (
  /** @param {Graph} graph - The graph to layout. */
  graph: Graph,

  /** @param {LayoutSettingsMap[T]} settings - The settings for the layout mode. */
  settings: LayoutSettingsMap[T]
) => T extends keyof ComplexLayoutSettingsMap ? Layout : void;

/**
 * Template for specifying graph layout settings.
 *
 * @template T
 * @type {object}
 */
export type LayoutTemplate<T extends keyof LayoutSettingsMap> = (T extends keyof ComplexLayoutSettingsMap
  ? LayoutIterationSettings
  : NonNullable<unknown>) & {
  /** @param {T} mode - Layout mode. */
  mode: T;

  /** @param {LayoutSettingsMap[T]} [parameters] - Layout settings specific to the mode (optional). */
  parameters?: LayoutSettingsMap[T];
};

/** Type alias for complex layout settings. */
export type ComplexLayoutTypes = keyof Pick<ComplexLayoutSettingsMap, ComplexGraphLayouts>;

/** Type alias for initial layout settings. */
export type InitialLayoutTypes = keyof Pick<InitialLayoutSettingsMap, InitialGraphLayouts>;

/** Type alias for all layout settings. */
export type AllLayoutTypes = ComplexLayoutTypes | InitialLayoutTypes;

/** Type for specifying complex graph layouts. */
export type ComplexGraphLayoutConfiguration = LayoutTemplate<ComplexLayoutTypes>;

/** Type for specifying initial graph layouts. */
export type InitialGraphLayoutConfiguration = LayoutTemplate<InitialLayoutTypes>;
