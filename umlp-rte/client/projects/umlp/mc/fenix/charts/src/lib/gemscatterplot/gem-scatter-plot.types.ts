import { GemScatterPlotData as GemScatterPlotDataI } from "./gem-scatter-plot-types/gem-scatter-plot-types.types-gen";
import { GemScatterPlotEntry as GemScatterPlotEntryI } from "./gem-scatter-plot-types/gem-scatter-plot-types.types-gen";
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
  entries?: GemScatterPlotEntry[];
  constructor(entries?: GemScatterPlotEntry[] | IUMLPList<GemScatterPlotEntry>) {
    if("toArray" in entries){
      this.entries = entries.toArray();
    }else{
      this.entries = entries;
    }
  }
}

export class GemScatterPlotEntry implements GemScatterPlotEntryI {
  label?: string;
  stack?: string;
  data?: number[];
  backgroundColor?: string;
  hoverBackgroundColor?: string;
  constructor(label?: string, stack?: string, data?: number[] | IUMLPList<number>, backgroundColor?: string, hoverBackgroundColor?: string) {
    this.label = label;
    this.stack = stack;
    if("toArray" in data){
      this.data = data.toArray();
    }else{
      this.data = data;
    }
    this.backgroundColor = backgroundColor;
    this.hoverBackgroundColor = hoverBackgroundColor;
  }
}
