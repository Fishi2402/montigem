import { Component, Input } from '@angular/core';
import { ChartDataset, ChartOptions } from "chart.js";
import { autoColors } from "../colors";
import { GemPieChartData } from '../gen/gempiecharttypes/GemPieChartData';
import { GemPieChart as GemPieChartTOP } from './gem-pie-chart/gem-pie-chart.component-gen';

@Component({
  selector: 'gem-pie-chart',
  templateUrl: './gem-pie-chart.component.html',
  styleUrls: ['./gem-pie-chart.component.scss']
})

export class GemPieChart extends GemPieChartTOP {
  /**all labels of the entries in the chart*/
  public labels: string[];

  /**contains the data for each entry */
  public dataSets: ChartDataset<'pie'>[] = [];

  /**all colors of the entries */
  public colors = [];

  /**variable with the size of the inner radius */
  public _innerRadius: number;

  /**labels for chart legend */
  public legendLabels: string[];

  /**colors for chart legend */
  public legendColors: string[] = [];

  /** defines the position of the legend */
  public legendPosition: string;

  /**variable that enables or disables the legend */
  public displayLegend = true;

  /**variable with the settings of the chart */
  public options: ChartOptions<'doughnut'> = {
    responsive: true,
    maintainAspectRatio: true,
  };

  /**gives the colors from the constant autoColors */
  public getColors() {
    return autoColors;
  }

  /** sets the data of the entries and sets the colors of the entries where the color is not explicitly given */
  @Input()
  public set data(data: GemPieChartData) {
    if (!data) {
      return;
    }
    this.labels = data.getEntriesList().map(entry => entry.getLabel());
    const colors = [];
    const labels = [];
    let autocolors = this.getColors();
    let pointerAutoColors = 0;

    for (let i = 0; i < data.getEntriesList().length; i++) {
      if (!data.getEntries(i).getBackgroundColor() || !CSS.supports('color', data.getEntries(i).getBackgroundColor())) {
        data.getEntries(i).setBackgroundColor(autocolors[pointerAutoColors++]);
      }
      const bgrColor = data.getEntries(i).getBackgroundColor();
      const label = data.getEntries(i).getLabel();

      labels.push(label);
      colors.push(bgrColor);
    }

    this.dataSets = [{
      data: data.getEntriesList().map(entry => entry.getValue()),
      backgroundColor: colors
    }];

    this.legendLabels = labels;
    this.legendColors = colors;
  }

  @Input()
  public set innerRadius(innerRadius: number) {
    this._innerRadius = innerRadius;
    this.saveAllOptionsChanges();
  }

  /**function to set the color of entries where color is not set in input*/
  colorsWithoutInput(colors) {
    let autoColors: string[] = this.getColors();
    let pointerNewColor: number = 0;
    for (let i = 0; i < colors[0].backgroundColor.length; i++) {
      if (colors[0].backgroundColor[i] === undefined) {
        colors[0].backgroundColor[i] = autoColors[pointerNewColor];
        pointerNewColor++;
      }
      else {
        continue;
      }
    }
    return colors;
  }

  /**
   * saves all unchanged settings of the chart when one specific setting is changed
   */
  saveAllOptionsChanges() {
    this.options = {
      responsive: true,
      aspectRatio: 1,
      maintainAspectRatio: false,
      cutout: this._innerRadius,
      plugins: {
        tooltip: {
          callbacks: {
            label: function (tooltipItem) {
              let data = tooltipItem.dataset;
              return data['labels'][tooltipItem['index']] + ': ' + data['datasets'][0]['data'][tooltipItem['index']];
            }
          }
        },
        legend: {
          display: false
        }
      }
    };
  }

  changeInnerRadius(innerRadius: number){
    this._innerRadius = innerRadius;
    this.saveAllOptionsChanges();
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
