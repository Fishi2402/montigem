/* (c) https://github.com/MontiCore/monticore */

import { Component, Input } from '@angular/core';
import { config, GemBarChartComponent } from '../../../../build/gui/mc/fenix/charts/GemBarChartComponent';
import { ChartDataset, ChartOptions } from 'chart.js';
import { NgChartsModule } from 'ng2-charts';
import { GemBarChartData } from '../../../../build/cd/mc/fenix/charts/gembarcharttypes/GemBarChartData';
import { autoColors } from './colors';
import { GemChartLegend } from './GemChartLegend';
import { GemChartOptions } from './GemChartOptions';

@Component({
  standalone: true,
  imports: [...config.imports, NgChartsModule, GemChartOptions, GemChartLegend],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemBarChart extends GemBarChartComponent {

  /**labels on x axis in a vertical bar chart respectively on y axis in a horizontal bar*/
  public labels: string[];

  /** contains the data for all entries that will be displayed in the chart*/
  public dataSets: ChartDataset<'bar'>[] = [];

  /** contains all the data provided by the user */
  public rawData;

  /**contains the colors for each entry */
  public colors = [];

  /** adds by the desire of the user additional symbols to the values  */
  public _displayValue: (value: number) => string = (v) => String(v);

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
    if (!data) {
      return;
    }
    this.dataSets = [];
    const colors = [];
    const labels = [];
    let pointerAutoColors = 0;
    let autocolors = this.getColors();
    this.labels = data.getLabelsList();

    for (let i = 0; i < data.getEntriesList().length; i++) {
      if (!CSS.supports('color', data.getEntries(i).getBackgroundColor())) {
        data.getEntries(i).setBackgroundColor(autocolors[pointerAutoColors++]);
      }

      let entry: ChartDataset<'bar'> = {
        data: data.getEntries(i).getDataList(),
        label: data.getEntries(i).getLabel(),
        backgroundColor: data.getEntries(i).getBackgroundColor(),
        hoverBackgroundColor: data.getEntries(i).getBackgroundColor(),
        hoverBorderWidth: 2,
      };

      labels.push(entry.label);
      colors.push(entry.backgroundColor);
      this.dataSets.push(entry);
    }
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
    this.saveAllOptionsChanges();
  }

  /**
   * handles a problem connected with different order of variables in the input
   */
  public setData(): void {
    if (this._stacked && this.rawData != undefined) {
      for (let i = 0; i < this.dataSets.length; i++) {
        this.dataSets[i].stack = this.rawData.getEntries(i).stack;
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
          grid: {
            color: '#888'
          },
          ticks: {
            color: '#888'
          }
        },
        y: {
          min: this._minValue,
          max: this._maxValue,
          stacked: this._stacked,
          grid: {
            color: '#888'
          },
          ticks: {
            color: '#888'
          }
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