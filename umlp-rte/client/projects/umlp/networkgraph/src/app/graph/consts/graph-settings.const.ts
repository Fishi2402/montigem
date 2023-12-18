import { DEFAULT_EDGE_PROGRAM_CLASSES, DEFAULT_NODE_PROGRAM_CLASSES, DEFAULT_SETTINGS } from "sigma/settings";
import { GraphSettings } from "../types/graph-settings.types";
import getNodeImageProgram from "sigma/rendering/webgl/programs/node.image";
import drawLabel from "sigma/rendering/canvas/label";
import drawHover from "sigma/rendering/canvas/hover";
import { prettyLabelHoverRenderer } from "../renderers/pretty-label-hover.renderer";
import prettyLabelRenderer from "../renderers/pretty-label.renderer";
import EdgeLineProgram from "sigma/rendering/webgl/programs/edge.line";

/**
 * Function to construct and return new default graph settings object. The use of a function rather than one `settings
 * object` is required to return a fresh render programs for each instance (i.e., `nodeImageProgram` needs to be
 * different for each instance).
 *
 * @returns {GraphSettings} A new GraphSettings object with default values.
 */
export function getDefaultGraphSettings(): GraphSettings {
  return {
    ...DEFAULT_SETTINGS,
    forceNodeLabels: false,
    forceEdgeLabels: false,
    highlightNeighboringElementsOnNodeSelect: true,
    enableNodeHoverEvents: true,
    enableNodeClickEvents: true,
    labelDensity: 1,
    labelGridCellSize: 100,
    labelRenderedSizeThreshold: 6,
    renderEdgeLabels: true,
    ignoreDuplicatedEdges: true,
    areaDependentScaling: true,
    zoomAnimationDuration: 500,
    zoomFactor: 1.5,
    assetsPath: "assets",
    defaultNodeSize: 10,
    defaultEdgeWeight: 1,
    defaultNodeColor: "#999",
    colorMap: ["#8F71E3", "#88DBE4", "#ff9d00", "#95e000", "#5e87fc", "#ff6d6d", "#ff19f0"],
    defaultNodeProgram: "circle",
    defaultUndirectedEdgeType: "line",
    defaultDirectedEdgeType: "arrow",
    colorEdgeMode: "bySource",
    allowSelfLoops: true,
    graphType: "mixed",
    unknownGroupName: "unknown",
    defaultEdgeType: "",
    labelRenderer: prettyLabelRenderer as typeof drawLabel,
    hoverRenderer: prettyLabelHoverRenderer as typeof drawHover,
    nodeProgramClasses: {
      ...DEFAULT_NODE_PROGRAM_CLASSES,
      image: getNodeImageProgram(),
    },
    edgeProgramClasses: {
      ...DEFAULT_EDGE_PROGRAM_CLASSES,
      fastLine: EdgeLineProgram,
    },
    highlightNeighboursOnNodeHover: true,
    draggableNodes: true,
    labelPaddingX: 40,
    labelPaddingY: 20,
    labelGap: 2,
    prettyLabelsHoverConfiguration: {
      mutedTextColor: "#777",
      backgroundColor: "#fff",
      shadowOffsetX: 0,
      shadowOffsetY: 2,
      shadowBlur: 7,
      shadowColor: "#000000bb",
      cornerRadius: 5,
    },
    prettyLabelsConfiguration: {
      labelBackgroundColor: "#fff",
      labelBackgroundOpacity: 0.8,
    },
    prettyNodeHoverConfiguration: {
      activeNodeStyle: {
        zIndex: 1,
        forceLabel: true,
      },
      inactiveNodeStyle: {
        forceLabel: false,
        zIndex: 0,
        label: "",
        color: "#ccc",
        image: null,
        highlighted: false,
      },
      activeEdgeStyle: {
        forceLabel: false,
      },
      inactiveEdgeStyle: {
        hidden: true,
      },
    },
  };
}
