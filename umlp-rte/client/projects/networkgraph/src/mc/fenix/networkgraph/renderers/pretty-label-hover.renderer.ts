import { NodeAttributes } from "../types/graph.types";
import { GraphSettings } from "../types/graph-settings.types";
import { TextObject } from "../types/custom-renderer.types";
import { createTextObject, drawShadowAndRect, measureTextDimensions, resolveLabelColor } from "./render.utils";
import { ColorUtil } from "../shared/utils/color.util";
import { Settings } from "sigma/settings";

/**
 * Custom drawing method for hover state.
 *
 * @param {CanvasRenderingContext2D} context - The canvas 2D context.
 * @param {NodeAttributes} data - Data for the node.
 * @param {GraphSettings} settings - Graph settings.
 */
export function prettyLabelHoverRenderer(
  context: CanvasRenderingContext2D,
  data: NodeAttributes,
  settings: GraphSettings
): void {
  // Only draw the hover labels when we have enabled hover events
  if (!settings.enableNodeHoverEvents) return;

  // Adjust the label size for hover state
  const reducedLabelSize: number = settings.labelSize - 2;
  // Determine the default color for text
  const defaultColor: string = resolveLabelColor(data, settings);

  // Create text objects for label, tooltip, and group with respective settings
  const textObjects: TextObject[] = [
    createTextObject(settings as Settings, {
      content: data.label ?? "",
      fontSize: data.tooltip ? reducedLabelSize : settings.labelSize,
      color: data.tooltip
        ? ColorUtil.parseIntoHEX(settings.prettyLabelsHoverConfiguration.mutedTextColor)
        : defaultColor,
    }),
    createTextObject(settings as Settings, {
      content: data.tooltip ?? "",
      fontSize: settings.labelSize,
      color: defaultColor,
    }),
    createTextObject(settings as Settings, {
      content: data.group ?? "",
      fontSize: reducedLabelSize,
      color: data.color ?? defaultColor,
    }),
  ].filter((textObject: TextObject): boolean => textObject.content !== "");

  // Initialize dimensions for the containing rectangle
  let totalWidth: number = 0;
  let totalHeight: number = 0;

  // Measure the dimensions of each text object
  textObjects.forEach((textObject: TextObject): void => {
    if (textObject.content) {
      measureTextDimensions(context, textObject);
      totalWidth = Math.max(totalWidth, textObject.width);
      totalHeight += textObject.height;
    }
  });

  // Padding for the rectangle
  const xPadding: number = settings.labelPaddingX / settings.zoomToSizeRatioFunction(1);
  const yPadding: number = settings.labelPaddingY / settings.zoomToSizeRatioFunction(1);
  const gap: number = settings.labelGap / settings.zoomToSizeRatioFunction(1);

  // Dimensions for the rectangle (left and right padding = factor of 2)
  const rectWidth: number = totalWidth + data.size + 2 * xPadding + 2 * gap;
  const rectHeight: number = totalHeight + 2 * yPadding;

  // Calculate positions for rendering rectangle and text
  const xPositionRect: number = data.x;
  const yPositionRect: number = data.y - rectHeight / 2;

  const xPositionText: number = xPositionRect + data.size + gap + xPadding;
  let yPositionText: number = yPositionRect + yPadding;

  // Draw shadow and rectangle around the hover label
  drawShadowAndRect(context, data, settings, rectWidth, rectHeight);

  // Initial Y-offset for drawing text

  // Draw each text object
  textObjects.forEach((textObject: TextObject): void => {
    if (textObject.content) {
      // Set the styles for drawing the text
      context.fillStyle = textObject.color;
      context.font = `${textObject.weight} ${textObject.fontSize}px ${textObject.font}`;

      // Draw text and adjust Y-offset for next text object
      yPositionText += textObject.height; // As we're using baseline bottom, we add the height
      context.fillText(textObject.content, xPositionText, yPositionText);
    }
  });
}
