import { circlepack, circular, random } from "graphology-layout";
import ForceSupervisor from "graphology-layout-force/worker";
import NoverlapLayout from "graphology-layout-noverlap/worker";
import FA2Layout from "graphology-layout-forceatlas2/worker";
import Graph from "graphology";
import forceAtlas2 from "graphology-layout-forceatlas2";
import { AssignLayout, ComplexGraphLayouts, InitialGraphLayouts, LayoutSettingsMap } from "../types/graph-layout.types";

/**
 * Abstract class that serves as a blueprint for layout algorithms implemented in Graphology. Defines methods for
 * managing the lifecycle of a layout algorithm.
 */
export abstract class Layout {
  /** Optional Worker instance used for layout algorithms that leverage Web Workers. */
  worker?: Worker;

  /** Starts the layout algorithm. */
  abstract start(): void;

  /** Stops the layout algorithm. */
  abstract stop(): void;

  /**
   * Terminates the layout algorithm and releases any resources. Also, removes any event listeners attached during the
   * layout.
   */
  abstract kill(): void;

  /**
   * Checks if the layout algorithm is currently running.
   *
   * @returns {boolean} True if the algorithm is running, else false.
   */
  abstract isRunning(): boolean;
}

/** A mapping between initial layout modes and their corresponding assignment functions. */
export const INITIAL_LAYOUT_MAPPING: {
  [key in InitialGraphLayouts]: AssignLayout<key>;
} = {
  [InitialGraphLayouts.CIRCULAR]: circular.assign,
  [InitialGraphLayouts.RANDOM]: random.assign,
  [InitialGraphLayouts.CIRCLE_PACK]: circlepack.assign,
};

/**
 * A mapping between complex layout modes and their corresponding ComplexAssignFunctions. These are factory functions
 * that instantiate the appropriate layout supervisor/worker.
 */
export const COMPLEX_LAYOUT_MAPPING: {
  [key in ComplexGraphLayouts]: AssignLayout<key>;
} = {
  // Force Layout
  [ComplexGraphLayouts.FORCE]: (graph: Graph, settings: LayoutSettingsMap[ComplexGraphLayouts.FORCE]) =>
    new ForceSupervisor(graph, {
      isNodeFixed: "fixed",
      ...settings,
    }),
  // Force Atlas 2
  [ComplexGraphLayouts.FORCEATLAS2]: (graph: Graph, settings: LayoutSettingsMap[ComplexGraphLayouts.FORCEATLAS2]) =>
    new FA2Layout(graph, {
      ...settings,
      settings: {
        ...forceAtlas2.inferSettings(graph),
        ...settings.settings,
      },
    }),
  // No Overlap Layout
  [ComplexGraphLayouts.NO_OVERLAP]: (graph: Graph, settings: LayoutSettingsMap[ComplexGraphLayouts.NO_OVERLAP]) =>
    new NoverlapLayout(graph, settings),
};
