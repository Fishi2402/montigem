import { GemScatterPlotData as GemScatterPlotDataI } from "./gem-scatter-plot-types/gem-scatter-plot-types.types-gen";
import { GemScatterPlotAxis as GemScatterPlotAxisI } from "./gem-scatter-plot-types/gem-scatter-plot-types.types-gen";
import { GemScatterPlotSet as GemScatterPlotSetI } from "./gem-scatter-plot-types/gem-scatter-plot-types.types-gen";
import { GemScatterPlotPoint as GemScatterPlotPointI } from "./gem-scatter-plot-types/gem-scatter-plot-types.types-gen";
import { GemScatterPlotShape } from "./gem-scatter-plot-types/gem-scatter-plot-types.types-gen";
import { IUMLPList } from '@umlp/commonj2ts';

export class GemScatterPlotAxis implements GemScatterPlotAxisI {
  label?: string;
  minValue?: number;
  maxValue?: number;
  stepWidth?: number;

  constructor(label?: string, minValue?: number, maxValue?: number, stepWidth?: number) {
    this.label = label;
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.stepWidth = stepWidth;
  }
}

export class GemScatterPlotData implements GemScatterPlotDataI {
  sets?: GemScatterPlotSet[];

  constructor(sets?: GemScatterPlotSet[] | IUMLPList<GemScatterPlotSet>) {
    if("toArray" in sets){
      this.sets = sets.toArray();
    }else{
      this.sets = sets;
    }
  }
}

export class GemScatterPlotSet implements GemScatterPlotSetI {
  label?: string;
  shape?: GemScatterPlotShape;
  points?: GemScatterPlotPoint[];
  color?: string;
  hoverColor?: string;
  pointRadius?: number;


  constructor(label?: string,
      shape?: GemScatterPlotShape,
      points?: GemScatterPlotPoint[] | IUMLPList<GemScatterPlotPoint>,
      color?: string,
      hoverColor?: string,
      pointRadius?: number) {
    this.label = label;
    this.shape = shape;
    if ("toArray" in points) {
      this.points = points.toArray();
    } else {
      this.points = points;
    }
    this.color = color;
    this.hoverColor = hoverColor;
    this.pointRadius = pointRadius;
  }
}

export class GemScatterPlotPoint implements GemScatterPlotPointI {
  x?: number;
  y?: number;
  label?: string;

  constructor(x?: number, y?: number, label?: string) {
    this.x = x;
    this.y = y;
    this.label = label;
  }
}

export { GemScatterPlotShape };
