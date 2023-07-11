import { Component, Input } from '@angular/core';
import { ChartDataset, ChartOptions } from 'chart.js';
import { autoColors } from "../colors";
import { GemBarChartData } from './gem-bar-chart-types/gem-bar-chart-types.types-gen';
import { GemBarChart as GemBarChartTOP } from './gem-bar-chart/gem-bar-chart.component-gen';

@Component({
  selector: 'gem-bar-chart',
  templateUrl: './gem-bar-chart.component.html',
  styleUrls: ['./gem-bar-chart.component.scss']
})

export class GemBarChart extends GemBarChartTOP {

  /**labels on x axis in a vertical bar chart respectively on y axis in a horizontal bar*/
  public labels: string[];

  /** contains the data for all entries that will be displayed in the chart*/
  public dataSets: ChartDataset<'bar'>[] = [];

  /** contains all the data provided by the user */
  public rawData;

  /**contains the colors for each entry */
  public colors = [];

  /** the maximal value to which the data will be displayed*/
  public _maxValue: number = undefined;

  /** the minimal value from which the data will begin to be displayed*/
  public _minValue: number = undefined;

  /** variable that shows if the data will be stacked*/
  public _stacked: boolean;

  /** adds by the desire of the user additional symbols to the values  */
  public _displayValue: (value: number) => string;

  /**labels for chart legend */
  public legendLabels: string[] = [];

  /**colors for chart legend */
  public legendColors: string[] = [];

  /** defines the position of the legend */
  public legendPosition: string;

  /**variable that enables or disables the legend */
  public displayLegend = true;

  /** variable that contains the settings for the chart */
  public options: ChartOptions = {
    responsive: true,
    maintainAspectRatio: true,
  };

  /**gives the colors from the constant autoColors */
  public getColors() {
    return autoColors;
  }

  /** setter that processes the data to be shown correctly by the chart and the legend.
   * Also sets the colors for each entry when no color is given as it uses the provided colors service 
   */
  @Input()
  public set data(data: GemBarChartData) {
    this.dataSets = [];
    const colors = [];
    const labels = [];
    let pointerAutoColors = 0;
    let autocolors = this.getColors();
    this.labels = data.labels;

    for (let i = 0; i < data.entries.length; i++) {
      if (!CSS.supports('color', data.entries[i].backgroundColor)) {
        data.entries[i].backgroundColor = autocolors[pointerAutoColors++];
      }

      let entry: ChartDataset<'bar'> = {
        data: data.entries[i].data,
        label: data.entries[i].label,
        stack: data.entries[i].stack,
        backgroundColor: data.entries[i].backgroundColor,
        hoverBackgroundColor: data.entries[i].backgroundColor,
        hoverBorderWidth: 2,
      };

      labels.push(entry.label);
      colors.push(entry.backgroundColor);
      this.dataSets.push(entry);
    }
    console.log()
    this.legendLabels = labels;
    this.legendColors = colors;
  }

  @Input()
  public set minValue(minValue: number) {
    this._minValue = minValue;
    this.saveAllOptionsChanges();
  }

  @Input()
  public set maxValue(maxValue: number) {
    this._maxValue = maxValue;
    this.saveAllOptionsChanges();
  }

  @Input()
  public set displayValue(displayValue: (value: number) => string) {
    this._displayValue = displayValue;
    this.saveAllOptionsChanges();
  }

  @Input()
  public set stacked(stacked: boolean) {
    this._stacked = stacked;
    this.saveAllOptionsChanges();
    this.setData();
  }
  
  constructor() {
    super();
  }

  ngOnInit(): void {
  }

  /**
   * handles a problem connected with different order of variables in the input 
   */
  public setData(): void {
    if (this._stacked && this.rawData != undefined) {
      for (let i = 0; i < this.dataSets.length; i++) {
        this.dataSets[i].stack = this.rawData.entries[i].stack;
      }
    }
    else if (!this._stacked && this.rawData != undefined) {
      for (let i = 0; i < this.dataSets.length; i++) {
        this.dataSets[i].stack = undefined;
      }
    }
    else {
      return;
    }
  }

  /** function that saves all settings of the variables that are not reset in the new input */
  public saveAllOptionsChanges(): void {
    this.options = {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        x: {
          stacked: this._stacked,
        },
        y: {
          min: this._minValue,
          max: this._maxValue,
          stacked: this._stacked
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
            }
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