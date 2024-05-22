import { Component, Input } from '@angular/core';
import { ChartDataset, ChartOptions, TooltipItem } from 'chart.js';
import { autoColors } from "../colors";
import { GemScatterPlotData, GemScatterPlotAxis, GemScatterPlotSet, GemScatterPlotShape } from './gem-scatter-plot-types/gem-scatter-plot-types.types-gen';
import { GemScatterPlot as GemScatterPlotTOP } from './gem-scatter-plot/gem-scatter-plot.component-gen';

interface CustomTooltipItemPointWithLabel extends TooltipItem<'scatter'> {
    raw: {
      x: number,
      y: number,
      label: string,
    }
}

@Component({
  selector: 'gem-scatter-plot',
  templateUrl: './gem-scatter-plot.component.html',
  styleUrls: ['./gem-scatter-plot.component.scss']
})
export class GemScatterPlot extends GemScatterPlotTOP {

  /** contains the data for all entries that will be displayed in the chart*/
  public dataSets: ChartDataset<'scatter'>[] = [];

  //stuff that has to be used when changing the options
  public _xAxis: GemScatterPlotAxis = {};
  public _yAxis: GemScatterPlotAxis = {};

  /**colors for chart legend */
  public legendColors: string[] = [];

  /** defines the position of the legend */
  public legendPosition: string;

  /**variable that enables or disables the legend */
  public displayLegend = true;

  /** variable that contains the settings for the chart */
  //will be overridden on init
  public options: ChartOptions = {
    responsive: true,
    maintainAspectRatio: true,
  };

  @Input()
  public showConfig: boolean = true;

  /**gives the colors from the constant autoColors */
  public getColors() {
    return autoColors;
  }

  /** setter that processes the data to be shown correctly by the chart and the legend.
   * Also sets the colors for each entry when no color is given as it uses the provided colors service
   */
  @Input()
  public set data(data: GemScatterPlotData) {
    if (!data) {
      return;
    }
    this.dataSets = [];
    const colors = [];
    const labels = [];
    let pointerAutoColors = 0;
    let autocolors = this.getColors();

    for (let i = 0; i < data.sets.length; i++) {
      const set = data.sets[i];
      if (!CSS.supports('color', set.color)) {
        set.color = autocolors[pointerAutoColors++];
      }
      if (!CSS.supports('color', set.hoverColor)) {
        set.hoverColor = autocolors[pointerAutoColors++];
      }

      const points = [];
      set.points.forEach((point) => {
        points.push({
          x: point.x,
          y: point.y,
          label: point.label
        });
      });

      let entry: ChartDataset<'scatter'> = {
        label: set.label,
        pointStyle: this.getPointStyle(set.shape),
        data: points,
        backgroundColor: set.color,
        hoverBackgroundColor: set.hoverColor,
        hoverBorderWidth: 2,
        pointRadius: set.pointRadius
      };

      this.dataSets.push(entry);
    }
    this.legendColors = colors;
  }

  public getPointStyle(shape?: GemScatterPlotShape): string {
    //https://www.chartjs.org/docs/latest/configuration/elements.html#point-styles
    let name: string = "circle";
    switch (shape) {
      case GemScatterPlotShape.SQUARE:
        name = "rect";
        break;
      case GemScatterPlotShape.ROUNDED_SQUARE:
        name = "rectRounded";
        break;
      case GemScatterPlotShape.ROTATED_SQUARE:
        name = "rectRot";
        break;
      case GemScatterPlotShape.CROSS:
        name = "cross";
        break;
      case GemScatterPlotShape.ROTATED_CROSS:
        name = "crossRot";
        break;
      case GemScatterPlotShape.DASH:
        name = "dash";
        break;
      case GemScatterPlotShape.LINE:
        name = "line";
        break;
      case GemScatterPlotShape.STAR:
        name = "star";
        break;
      case GemScatterPlotShape.TRIANGLE:
        name = "triangle";
        break;
    }
    return name;
  }

  @Input()
  public set xAxis(axis: GemScatterPlotAxis) {
    this._xAxis = axis;
    this.saveAllOptionsChanges();
  }

  @Input()
  public set yAxis(axis: GemScatterPlotAxis) {
    this._yAxis = axis;
    this.saveAllOptionsChanges();
  }

  constructor() {
    super();
  }

  ngOnInit(): void {
    this.saveAllOptionsChanges();
  }

  /** function that saves all settings of the variables that are not reset in the new input */
  public saveAllOptionsChanges(): void {
    this.options = {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        x: {
          title: {
            display: this._xAxis.label !== null,
            text: this._xAxis.label,
          },
          min: this._xAxis.minValue,
          max: this._xAxis.maxValue,
          grid: {
            color: '#888',
          },
          ticks: {
            color: '#888',
            stepSize: this._xAxis.stepWidth,
          }
        },
        y: {
          title: {
            display: this._yAxis.label !== null,
            text: this._yAxis.label,
          },
          min: this._yAxis.minValue,
          max: this._yAxis.maxValue,
          grid: {
            color: '#888',
          },
          ticks: {
            color: '#888',
            stepSize: this._yAxis.stepWidth,
          }
        }
      },
      plugins: {
        tooltip: {
          callbacks: {
            label: function(context) {
              let newContext = context as CustomTooltipItemPointWithLabel;
              let label = newContext.raw.label;
              return "coordinates: (" + newContext.raw.x + ", " + newContext.raw.y + ")" + ", label: " + label;
            }
          }
        }
      }
    }
  }


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
