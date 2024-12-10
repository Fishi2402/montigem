import { Sigma } from "sigma";
import { Coordinates, PlainObject } from "sigma/types";
import { ExportUtils } from "../../shared/utils/export.utils";
import { Settings } from "sigma/settings";
import { getDefaultGraphSettings } from "../consts/graph-settings.const";

/** All available sigma layers of the Canvas */
type SigmaLayer = "edgeLabels" | "edges" | "hoverNodes" | "hovers" | "labels" | "mouse" | "nodes";

/** Type specifying a bounding box */
type BoundingBox = { topLeft: Coordinates; bottomRight: Coordinates };

/** Default factor to scale up the quality for a high quality image */
const DEFAULT_HIGH_QUALITY_SCALE: number = 2;

/** Layers of SigmaJS Canvas that should be considered for export */
const INPUT_LAYERS: SigmaLayer[] = ["edges", "nodes", "edgeLabels", "labels"];

/** Default Background Color */
const BACKGROUND_COLOR: string = "rgba(0, 0, 0, 0)";

/**
 * Downloads a Sigma graph as a high-quality PNG image.
 *
 * @param {Sigma} graph - Sigma graph to be downloaded.
 * @param {string} fileName - File name for the downloaded PNG.
 * @param {number} [highQualityScale] - Scale factor for high-quality rendering. Default is
 *   {@link DEFAULT_HIGH_QUALITY_SCALE}
 */
export function downloadChartAsPNG(
  graph: Sigma,
  fileName: string,
  highQualityScale: number = DEFAULT_HIGH_QUALITY_SCALE
): void {
  const pixelRatio: number = window.devicePixelRatio || 1;

  // Calculate real bounding box
  const { width, height } = graph.getDimensions();
  const scaledWidth: number = width * highQualityScale;
  const scaledHeight: number = height * highQualityScale;

  // Setup temporary renderer with scaled dimensions
  const temporaryRenderer: Sigma = setupTemporaryRenderer(graph, scaledWidth, scaledHeight, highQualityScale);

  // Create and setup a high-quality canvas
  const canvas: HTMLCanvasElement = createHighQualityCanvas(scaledWidth * pixelRatio, scaledHeight * pixelRatio);

  // Draw the layers of the graph onto the high-quality canvas
  drawLayersToCanvas(temporaryRenderer, canvas, scaledWidth * pixelRatio, scaledHeight * pixelRatio);

  // Export the high-quality canvas as a PNG image
  trimCanvas(canvas);
  ExportUtils.downloadCanvasAsPNG(canvas, fileName);

  // Clean up the temporary renderer
  temporaryRenderer.kill();
  temporaryRenderer.getContainer().remove();
}

/**
 * Sets up a temporary Sigma renderer for high-quality export.
 *
 * @param {Sigma} graph - The original Sigma graph.
 * @param {number} width - Original canvas width.
 * @param {number} height - Original canvas height.
 * @param {number} scale - Scale factor for high-quality rendering.
 * @returns {Sigma} A new temporary Sigma renderer.
 */
function setupTemporaryRenderer(graph: Sigma, width: number, height: number, scale: number): Sigma {
  const temporaryRoot: HTMLElement = document.createElement("DIV");
  Object.assign(temporaryRoot.style, {
    width: `${width}px`,
    height: `${height}px`,
    // Position the chart on top of the top-left corner so that it is invisible
    position: "absolute",
    right: "101%",
    bottom: "101%",
  });
  document.body.append(temporaryRoot);

  const originalSettings: Settings = graph.getSettings();
  originalSettings.nodeProgramClasses = getDefaultGraphSettings().nodeProgramClasses;

  // Create a new sigma instance with the same settings but the new dimensions
  const temporaryRenderer: Sigma = new Sigma(graph.getGraph(), temporaryRoot, {
    ...originalSettings,
    zoomToSizeRatioFunction: (ratio: number) => originalSettings.zoomToSizeRatioFunction(ratio) / scale,
    labelSize: originalSettings.labelSize * scale,
    edgeLabelSize: originalSettings.edgeLabelSize * scale,
    labelRenderedSizeThreshold: originalSettings.labelRenderedSizeThreshold * scale,
    labelGridCellSize: originalSettings.labelGridCellSize * scale,
    stagePadding: originalSettings.stagePadding * scale,
  });

  temporaryRenderer.getCamera().setState(graph.getCamera().getState());
  temporaryRenderer.refresh();

  return temporaryRenderer;
}

/**
 * Creates and sets up a high-quality canvas to draw the graph on for exporting.
 *
 * @param {number} width - Width for the high-quality canvas.
 * @param {number} height - Height for the high-quality canvas.
 * @returns {HTMLCanvasElement} A new high-quality canvas.
 */
function createHighQualityCanvas(width: number, height: number): HTMLCanvasElement {
  const canvas: HTMLCanvasElement = document.createElement("CANVAS") as HTMLCanvasElement;
  canvas.width = width;
  canvas.height = height;

  const context: CanvasRenderingContext2D | null = canvas.getContext("2d", { willReadFrequently: true });
  if (!context) return canvas;
  context.fillStyle = BACKGROUND_COLOR;
  context.fillRect(0, 0, width, height);

  return canvas;
}

/**
 * Draws all the layers of a Sigma renderer to a canvas.
 *
 * @param {Sigma} graph - Sigma renderer to get layers from.
 * @param {HTMLCanvasElement} canvas - Canvas to draw on.
 * @param {number} width - Width of the canvas.
 * @param {number} height - Height of the canvas.
 */
function drawLayersToCanvas(graph: Sigma, canvas: HTMLCanvasElement, width: number, height: number): void {
  const context: CanvasRenderingContext2D = canvas.getContext("2d")!;
  const canvases: PlainObject<HTMLCanvasElement> = graph.getCanvases();
  const layers: string[] = INPUT_LAYERS.filter((id: string): boolean => !!canvases[id]);

  layers.forEach((id: string): void => {
    context.drawImage(canvases[id], 0, 0, width, height, 0, 0, width, height);
  });
}

/**
 * Finds the bounding box of the area that needs to be trimmed.
 *
 * @param {ImageData} imageData - The image data from the canvas.
 * @param {number} w - The width of the canvas.
 * @param {number} h - The height of the canvas.
 * @param {number} threshold - The threshold for trimming [0-255].
 * @returns {object} - An object containing the top-left and bottom-right corners.
 */
function getCanvasBoundingBox(imageData: ImageData, w: number, h: number, threshold: number): BoundingBox {
  const { data } = imageData;
  const topLeft: Coordinates = { x: w + 1, y: h + 1 };
  const bottomRight: Coordinates = { x: -1, y: -1 };

  let index: number = 0;
  for (let y: number = 0; y < h; y++) {
    for (let x: number = 0; x < w; x++) {
      if (data[index] + data[index + 1] + data[index + 2] + data[index + 3] > threshold * 4) {
        topLeft.x = Math.min(x, topLeft.x);
        topLeft.y = Math.min(y, topLeft.y);
        bottomRight.x = Math.max(x, bottomRight.x);
        bottomRight.y = Math.max(y, bottomRight.y);
      }
      index += 4; // Move to the next pixel's rgb-channel
    }
  }
  return { topLeft, bottomRight };
}

/**
 * Trims the canvas based on the alpha channel of pixels.
 *
 * @remarks
 *   Based on the solution suggested here: https://stackoverflow.com/a/69993651
 * @param {canvas} canvas A canvas element to trim. This element will be trimmed (reference)
 * @param {int} threshold Alpha threshold. Allows for trimming semi-opaque pixels too. Range: 0 - 255
 */
function trimCanvas(canvas: HTMLCanvasElement, threshold: number = 0): void {
  const context: CanvasRenderingContext2D | null = canvas.getContext("2d");
  const w: number = canvas.width;
  const h: number = canvas.height;
  const imageData: ImageData | undefined = context?.getImageData(0, 0, w, h);

  if (!imageData || !context) return;

  // Get the bounding box
  const { topLeft, bottomRight }: BoundingBox = getCanvasBoundingBox(imageData, w, h, threshold);

  // Extract the Canvas content
  const cut: ImageData = context.getImageData(
    topLeft.x,
    topLeft.y,
    bottomRight.x - topLeft.x,
    bottomRight.y - topLeft.y
  );

  // Crop the Canvas to the bounding box
  canvas.width = bottomRight.x - topLeft.x;
  canvas.height = bottomRight.y - topLeft.y;

  // Re-insert the Content
  context.putImageData(cut, 0, 0);
}
