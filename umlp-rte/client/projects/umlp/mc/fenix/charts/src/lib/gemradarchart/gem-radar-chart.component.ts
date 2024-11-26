import {Component, Input} from '@angular/core';
import {GemRadarChartData} from "../gen/gemradarcharttypes/GemRadarChartData";
import {GemRadarChartDataSet} from "../gen/gemradarcharttypes/GemRadarChartDataSet";
import {ChartEvent, ChartOptions} from "chart.js";
import {autoColors} from "../colors";

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
    },
    scales: {
      r: {
        ticks: {

        }
      }
    }
  };

  @Input()
  public set data(data: GemRadarChartData) {
    if (!data) {
      return;
    }
    let autocolors = this.getColors();
    this.legendLabels = [];
    this.legendColors = [];

    this._dataSets = data.getDataSetsList().map((ds: GemRadarChartDataSet, index: number) => {
      let label = ds.getLabel();
      let backgroundColor = autoColors[index];
      if (ds.getBackgroundColor() !== null) {
        backgroundColor = ds.getBackgroundColor();
      }

      let res = {
        data: ds.getDataList(),
        label: label,
        fill: ds.getFill(),
        backgroundColor: backgroundColor,
      }

      if (ds.getBorderColor() !== null) {
        res["borderColor"] = ds.getBorderColor();
      }
      if (ds.getBorderWidth() !== null) {
        res["borderWidth"] = ds.getBorderWidth();
      }

      this.legendLabels.push(label);
      this.legendColors.push(backgroundColor);
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
    this.options.scales['r'].min = min;
  }

  @Input()
  public set max(max: number) {
    this.options.scales['r'].max = max;
  }

  @Input()
  public set stepSize(stepSize: number) {
    this.options.scales['r'].ticks.stepSize = stepSize;
  }

  chartHovered($event: { event: ChartEvent; active: {}[] }) {

  }

  chartClicked($event: { event?: ChartEvent; active?: {}[] }) {

  }

  // <------------------- Legend ------------------->

  @Input()
  public showConfig: boolean = true;

  /**gives the colors from the constant autoColors */
  public getColors() {
    return autoColors;
  }

  /**labels for chart legend */
  public legendLabels: string[] = [];

  /**colors for chart legend */
  public legendColors: string[] = [];

  /** defines the position of the legend */
  public legendPosition: string;

  /**variable that enables or disables the legend */
  public displayLegend = true;

  public setLegendPosition(legendPosition: string) {
    this.legendPosition = legendPosition;
  }

  /** changes container style depending on legend position */
  public changeContainer() {
    switch (this.legendPosition) {
      case 'left':
        return 'wholeChartWithLegendLeftContainer';
      case 'top':
        return 'wholeChartWithLegendTopContainer';
      case 'bottom':
        return 'wholeChartWithLegendBottomContainer';
      case 'right':
        return 'wholeChartWithLegendRightContainer';
      default:
        return 'wholeChartWithLegendBottomContainer';
    }
  }
}
