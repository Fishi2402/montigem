import { HEX } from "../shared/utils/color.util";

/** Represents a text element with styling and layout information for rendering on a canvas. */
export interface TextObject {
  /** The text's baseline setting, determining how text is aligned vertically. */
  baseLine: CanvasTextBaseline;

  /** The actual text content to be displayed. */
  content: string;

  /** The font family for the text. */
  font: string;

  /** The font weight (e.g., 'normal', 'bold'). */
  weight: string;

  /** The size of the font in pixels. */
  fontSize: number;

  /** The width of the text element in pixels. */
  width: number;

  /** The height of the text element in pixels. */
  height: number;

  /** The color of the text in HEX format. */
  color: HEX;
}
