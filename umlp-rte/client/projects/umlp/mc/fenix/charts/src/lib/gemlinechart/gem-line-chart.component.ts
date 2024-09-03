import { Component, Input } from '@angular/core';
import { ChartDataset, ChartOptions } from 'chart.js';
import { autoColors } from "../colors";
import { GemLineChartData } from './gem-line-chart-types/gem-line-chart-types.types-gen';
import { GemLineChart as GemLineChartTOP } from './gem-line-chart/gem-line-chart.component-gen';

@Component({
  selector: 'gem-line-chart',
  templateUrl: './gem-line-chart.component.html',
  styleUrls: ['./gem-line-chart.component.scss']
})

export class GemLineChart extends GemLineChartTOP {
  /**labels for which will be shown data from the entries */
  public labels: string[];

  /**contains the data for each entry */
  public dataSets: ChartDataset<'line'>[] = [];

  /**contains the colors for each entry */
  public colors = [];

  /**says whether the line fill colors of the entries will be shown */
  public _enableBackgroundColor = false;

  /**the upper bound of the values that will be shown*/
  public _maxValue = undefined;

  /**the lower bound of the values that will be shown */
  public _minValue = undefined;

  /**can add symbols to the values of the entries */
  public _displayValue: (value: number) => string = (v) => String(v);

  /**labels for chart legend */
  public legendLabels: string[] = [];

  /**colors for chart legend */
  public legendColors: string[] = [];

  /** defines the position of the legend */
  public legendPosition: string;

  /**variable that enables or disables the legend */
  public displayLegend = true;

  /**contains all the settings for the chart */
  public options: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: true,
  };

  @Input()
  public showConfig: boolean = true;

  /**gives the colors from the constant autoColors */
  public getColors() {
    return autoColors;
  }

  @Input()
  public set displayValue(displayValue: (value: number) => string) {
    this._displayValue = displayValue;
    this.saveAllOptionsChanges();
  }

  @Input()
  public set enableBackgroundColor(enableBackgroundColor: boolean) {
    this._enableBackgroundColor = enableBackgroundColor;
    this.saveAllOptionsChanges();
  }

  /**setter that adjusts the data for the entries and sets colors for them */
  @Input()
  public set data(data: GemLineChartData) {
    if (!data) {
      return;
    }
    this.dataSets = [];
    const colors = [];
    const labels = [];
    this.labels = data.labels;
    let autocolors = this.getColors();
    let pointerAutoColors = 0;

    for (let i = 0; i < data.entries.length; i++) {
      if (!CSS.supports('color', data.entries[i].borderColor)) {
          data.entries[i].borderColor = autocolors[pointerAutoColors++];
      }
      // TODO: apply alpha value to color string support
      let entry: ChartDataset<'line'> = {
        data: data.entries[i].data,
        label: data.entries[i].label,
        borderColor: data.entries[i].borderColor,
        backgroundColor: data.entries[i].borderColor + "30",
        pointBackgroundColor: data.entries[i].borderColor
      }

      // TODO: option to fill background
      entry.fill = false;
      labels.push(entry.label);
      colors.push(entry.borderColor);
      this.dataSets.push(entry);
    }

    this.legendLabels = labels;
    this.legendColors = colors;
  }

  @Input()
  public set maxValue(maxValue: number) {
    this._maxValue = maxValue;
    this.saveAllOptionsChanges();
  }

  @Input()
  public set minValue(minValue: number) {
    this._minValue = minValue;
    this.saveAllOptionsChanges();
  }

  ngOnInit(): void {
    this.saveAllOptionsChanges();
  }

  /**
   * The function saves the data for all variables with settings for the variable options
   * that are not reset in the specific setter
   */
  public saveAllOptionsChanges() {
    this.options = {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        x: {
          grid: {
            color: '#888'
          },
          ticks: {
            color: '#888'
          }
        },
        y: {
          max: this._maxValue,
          min: this._minValue,
          grid: {
            color: '#888'
          },
          ticks: {
            color: '#888'
          }
        }
      },
      elements: {
        line: {
          fill: this._enableBackgroundColor,
        }
      },
      plugins: {
        tooltip: {
          callbacks: {
            label: (tooltipItem) => {
              const label = tooltipItem.dataset.label || "";
              let value = tooltipItem.parsed.y;
              let res = "";
              if (this._displayValue != undefined && typeof value === 'number') {
                res = this._displayValue(value);
              }
              return label + ": " + res;
            },
          }
        },
        legend: {
          display: false
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
