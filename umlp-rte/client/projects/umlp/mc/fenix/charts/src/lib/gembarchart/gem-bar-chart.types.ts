import { GemBarChartData as GemBarChartDataI } from "./gem-bar-chart-types/gem-bar-chart-types.types-gen";
import { GemBarChartEntry as GemBarChartEntryI } from "./gem-bar-chart-types/gem-bar-chart-types.types-gen";
import { IUMLPList } from '@umlp/commonj2ts';

export class GemBarChartData implements GemBarChartDataI {
  entries?: GemBarChartEntry[];
  labels?: string[];
  constructor(entries?: GemBarChartEntry[] | IUMLPList<GemBarChartEntry>, labels?: string[] | IUMLPList<string>) {
    if("toArray" in entries){
      this.entries = entries.toArray();
    }else{
      this.entries = entries;
    }
    if("toArray" in labels){
      this.labels = labels.toArray();
    }else{
      this.labels = labels;
    }
  }
}

export class GemBarChartEntry implements GemBarChartEntryI {
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
