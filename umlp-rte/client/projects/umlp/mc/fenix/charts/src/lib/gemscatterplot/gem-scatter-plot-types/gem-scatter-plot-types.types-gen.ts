export interface GemScatterPlotData {
  sets?: GemScatterPlotSet[];
}

export interface GemScatterPlotAxis {
  label?: string;
  minValue?: number;
  maxValue?: number;
  stepWidth?: number;
}

export interface GemScatterPlotSet {
  label?: string;
  shape?: GemScatterPlotShape;
  points?: GemScatterPlotPoint[];
  color?: string;
  hoverColor?: string;
}

export interface GemScatterPlotPoint {
  x?: number;
  y?: number;
}

export enum GemScatterPlotShape {
  CIRCLE,
  SQUARE,
  ROUNDED_SQUARE,
  ROTATED_SQUARE,
  CROSS,
  ROTATED_CROSS,
  DASH,
  LINE,
  STAR,
  TRIANGLE,
}
