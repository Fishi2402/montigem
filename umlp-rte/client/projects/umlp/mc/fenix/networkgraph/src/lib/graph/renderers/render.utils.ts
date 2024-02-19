import { NodeAttributes } from "../types/graph.types";
import { GraphSettings } from "../types/graph-settings.types";
import { PartialButFor } from "sigma/types";
import { TextObject } from "../types/custom-renderer.types";
import { ColorUtil, HEX } from "../../shared/utils/color.util";
import { Settings } from "sigma/settings";

/**
 * Draws a rounded rectangle on a given 2D context.
 *
 * @param {CanvasRenderingContext2D} context - The canvas 2D context where the rounded rectangle will be drawn.
 * @param {number} x - The x-coordinate of the rectangle's top-left corner.
 * @param {number} y - The y-coordinate of the rectangle's top-left corner.
 * @param {number} width - The width of the rectangle.
 * @param {number} height - The height of the rectangle.
 * @param {number} radius - The radius of the rectangle's rounded corners.
 */
export function drawRoundedRect(
  context: CanvasRenderingContext2D,
  x: number,
  y: number,
  width: number,
  height: number,
  radius: number
): void {
  context.beginPath();
  // Start at the bottom-right corner
  context.moveTo(x + radius, y);
  // Draw line to bottom-right corner
  context.lineTo(x + width - radius, y);
  // Draw a quarter of a circle counter-clockwise
  context.arc(x + width - radius, y + radius, radius, 1.5 * Math.PI, 2 * Math.PI);
  // Draw a line from the bottom-right to the top-right
  context.lineTo(x + width, y + height - radius);
  // Draw a quarter of a circle counter-clockwise
  context.arc(x + width - radius, y + height - radius, radius, 0, 0.5 * Math.PI);
  // Draw a line from top-right to top-left
  context.lineTo(x + radius, y + height);
  // Draw a quarter of a circle counter-clockwise
  context.arc(x + radius, y + height - radius, radius, 0.5 * Math.PI, Math.PI);
  // Draw a line from top-left to bottom-left
  context.lineTo(x, y + radius);
  // Draw a quarter of a circle counter-clockwise
  context.arc(x + radius, y + radius, radius, Math.PI, 1.5 * Math.PI);
  context.closePath();
  context.fill();
}

/**
 * Measures the dimensions of a text and updates a TextObject.
 *
 * @param {CanvasRenderingContext2D} context - The canvas 2D context.
 * @param {TextObject} textObject - A TextObject instance containing text details.
 */
export function measureTextDimensions(context: CanvasRenderingContext2D, textObject: TextObject): void {
  context.textBaseline = textObject.baseLine;
  context.font = `${textObject.weight} ${textObject.fontSize}px ${textObject.font}`;
  const metrics = context.measureText(textObject.content);
  textObject.height = metrics.fontBoundingBoxAscent + metrics.fontBoundingBoxDescent;
  textObject.width = metrics.width;
}

/**
 * Creates a TextObject using default and custom settings.
 *
 * @param {T} settings - Graph settings.
 * @param {PartialButFor<TextObject, "content" | "fontSize" | "color">} object - Partial text object.
 * @returns {TextObject} A TextObject instance containing text details.
 */
export function createTextObject(
  settings: Settings,
  object: PartialButFor<TextObject, "content" | "fontSize" | "color">
): TextObject {
  return {
    baseLine: "bottom",
    width: 0,
    height: 0,
    font: settings.labelFont,
    weight: settings.labelWeight,
    ...object,
  };
}

/**
 * Resolves the color of the label based on graph settings and node attributes.
 *
 * @param {NodeAttributes} data - Data for the node.
 * @param {GraphSettings} settings - Graph settings.
 * @returns {string} Resolved label color.
 */
export function resolveLabelColor(data: NodeAttributes, settings: GraphSettings): HEX {
  return ColorUtil.parseIntoHEX(
    settings.labelColor.attribute
      ? data[settings.labelColor.attribute] || settings.labelColor.color || "#000"
      : settings.labelColor.color
  );
}

/**
 * Draws a shadow and rectangle based on node attributes and settings.
 *
 * @param {CanvasRenderingContext2D} context - The canvas 2D context.
 * @param {NodeAttributes} data - Data for the node.
 * @param {GraphSettings} settings - Graph settings.
 * @param {number} rectWidth - Rectangle width.
 * @param {number} rectHeight - Rectangle height.
 */
export function drawShadowAndRect(
  context: CanvasRenderingContext2D,
  data: NodeAttributes,
  settings: GraphSettings,
  rectWidth: number,
  rectHeight: number
): void {
  if (!data.label) return;

  // Extract all required settings
  const { backgroundColor, shadowOffsetX, shadowOffsetY, shadowBlur, shadowColor, cornerRadius } =
    settings.prettyLabelsHoverConfiguration;

  // Set the context for drawing the rectangle
  context.fillStyle = ColorUtil.parseIntoHEX(backgroundColor);
  context.shadowOffsetX = shadowOffsetX;
  context.shadowOffsetY = shadowOffsetY;
  context.shadowBlur = shadowBlur;
  context.shadowColor = ColorUtil.parseIntoHEX(shadowColor);

  // Actually draw the label
  const startX: number = data.x;
  const startY: number = data.y - rectHeight / 2;

  // Actually draw the rectangle
  drawRoundedRect(context, startX, startY, rectWidth, rectHeight, cornerRadius);
  context.fill();

  // Reset the context
  context.shadowOffsetX = 0;
  context.shadowOffsetY = 0;
  context.shadowBlur = 0;
}
