import { NodeAttributes } from "../types/graph.types";
import { GraphSettings } from "../types/graph-settings.types";
import { TextObject } from "../types/custom-renderer.types";
import { createTextObject, drawRoundedRect, measureTextDimensions, resolveLabelColor } from "./render.utils";
import { ColorUtil } from "../../shared/utils/color.util";
import { Settings } from "sigma/settings";

/**
 * Custom drawing method for labels.
 *
 * @param {CanvasRenderingContext2D} context - The canvas 2D context.
 * @param {NodeAttributes} data - Data for the node.
 * @param {GraphSettings} settings - Graph settings.
 */
export default function prettyLabelRenderer(
  context: CanvasRenderingContext2D,
  data: NodeAttributes,
  settings: GraphSettings
): void {
  if (!data.label) return;

  const textObject: TextObject = createTextObject(settings as Settings, {
    content: data.label,
    fontSize: settings.labelSize,
    color: resolveLabelColor(data, settings),
  });

  // Determine the dimensions of the text (to draw a rectangle around it)
  measureTextDimensions(context, textObject);
  const xPadding: number = settings.labelPaddingX / settings.zoomToSizeRatioFunction(1);
  const yPadding: number = settings.labelPaddingY / settings.zoomToSizeRatioFunction(1);
  const gap: number = settings.labelGap / settings.zoomToSizeRatioFunction(1);

  // Calculate positions for rendering rectangle and text
  const xPositionRect: number = data.x + data.size + gap;
  const yPositionRect: number = data.y - textObject.height / 2 - yPadding;
  const xPositionText: number = xPositionRect + xPadding;
  const yPositionText: number = data.y + textObject.height / 2;

  // Calculate width and height (left and right padding = factor of 2)
  const width: number = textObject.width + 2 * xPadding;
  const height: number = textObject.height + 2 * yPadding;

  // Set the styles for drawing the surrounding rectangle
  const originalAlpha: number = context.globalAlpha;
  context.globalAlpha = settings.prettyLabelsConfiguration.labelBackgroundOpacity;
  context.fillStyle = ColorUtil.parseIntoHEX(settings.prettyLabelsConfiguration.labelBackgroundColor);
  drawRoundedRect(context, xPositionRect, yPositionRect, width, height, 3);
  context.globalAlpha = originalAlpha;

  // Set the styles for drawing the text
  context.fillStyle = textObject.color;
  context.font = `${textObject.weight} ${textObject.fontSize}px ${textObject.font}`;

  // Draw text
  context.fillText(data.label, xPositionText, yPositionText);
}
