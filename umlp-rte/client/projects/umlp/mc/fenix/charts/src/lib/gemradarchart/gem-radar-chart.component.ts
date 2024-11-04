import {Component, Input} from '@angular/core';
import {GemRadarChartData} from "../gen/gemradarcharttypes/GemRadarChartData";
import {GemRadarChartDataSet} from "../gen/gemradarcharttypes/GemRadarChartDataSet";
import {ChartEvent, ChartOptions} from "chart.js";

@Component({
  selector: 'gem-radar-chart',
  templateUrl: './gem-radar-chart.component.html',
  styleUrls: ['./gem-radar-chart.component.scss']
})
export class GemRadarChart  {

  public _dataSets: any; // {data: number[], label: string}
  public _labels: string[];

  public options: ChartOptions<'radar'> = {
    elements: {
      line: {
        borderWidth: 3
      }
    },
    plugins: {
      legend: {
        display: false
      }
    }
  };

  @Input()
  public set data(data: GemRadarChartData) {
    if (!data) {
      return;
    }
    this._dataSets = data.getDataSetsList().map(ds => {
      let res = {
        data: ds.getDataList(),
        label: ds.getLabel(),
        fill: ds.getFill(),
      }
      if (ds.getBackgroundColor() !== null) {
        res["backgroundColor"] = ds.getBackgroundColor();
      }
      if (ds.getBorderColor() !== null) {
        res["borderColor"] = ds.getBorderColor();
      }
      if (ds.getBorderWidth() !== null) {
        res["borderWidth"] = ds.getBorderWidth();
      }
      return res;
    });
    this._labels = data.getLabelsList();
  }

  @Input()
  public set borderWidth(boarderWidth: number) {
    this.options.elements.line.borderWidth = boarderWidth;
  }

  @Input()
  public set fill(fill: boolean) {
    this.options.elements.line.fill = fill;
  }

  @Input()
  public set min(min: number) {
    this.options["scales"]["r"]["min"] = min;
  }

  @Input()
  public set max(max: number) {
    this.options["scales"]["r"]["max"] = max;
  }

  @Input()
  public set stepSize(stepSize: number) {
    this.options["scales"]["r"]["ticks"]["stepSize"] = stepSize;
  }

  chartHovered($event: { event: ChartEvent; active: {}[] }) {

  }

  chartClicked($event: { event?: ChartEvent; active?: {}[] }) {

  }
}
