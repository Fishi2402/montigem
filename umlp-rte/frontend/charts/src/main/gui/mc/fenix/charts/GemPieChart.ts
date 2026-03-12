/* (c) https://github.com/MontiCore/monticore */

import { Component, Input } from '@angular/core';
import { config, GemPieChartComponent } from '../../../../../../build/gui2java2ts/mc/fenix/charts/GemPieChartComponent';
import { ChartDataset, ChartOptions } from "chart.js";
import { NgChartsModule } from 'ng2-charts';
import { GemPieChartData } from '../../../../../../build/gui2java2ts/mc/fenix/charts/gempiecharttypes/GemPieChartData';
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
export class GemPieChart extends GemPieChartComponent {
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
        data.getEntries(i).setBackgroundColor(autocolors[(pointerAutoColors++) % autocolors.length]);
      }
      const bgrColor = data.getEntries(i).getBackgroundColor();
      const label = data.getEntries(i).getLabel();

      labels.push(label);
      colors.push(bgrColor);
    }

    const hoverColors = colors.map(c => this.lightenColor(c, 20));

    this.dataSets = [{
      data: data.getEntriesList().map(entry => entry.getValue()),
      backgroundColor: colors,
      hoverBackgroundColor: hoverColors,
      hoverBorderColor: hoverColors
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
   * Helper function to lighten a hex color by a percentage
   * @param color The hex color string (e.g. #FF0000)
   * @param percent The percentage to lighten (e.g. 20)
   */
  private lightenColor(color: string, percent: number): string {
    if (!color || !color.startsWith('#')) {
      return color; // Return original if not a valid hex code
    }
    const num = parseInt(color.replace('#', ''), 16);
    const amt = Math.round(2.55 * percent);
    const R = (num >> 16) + amt;
    const G = ((num >> 8) & 0x00FF) + amt;
    const B = (num & 0x0000FF) + amt;

    return '#' + (0x1000000 +
      (R < 255 ? (R < 1 ? 0 : R) : 255) * 0x10000 +
      (G < 255 ? (G < 1 ? 0 : G) : 255) * 0x100 +
      (B < 255 ? (B < 1 ? 0 : B) : 255)
    ).toString(16).slice(1);
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
              return tooltipItem.label + ': ' + tooltipItem.raw;
            }
          }
        },
        legend: {
          display: false
        }
      }
    };
  }

  changeInnerRadius(innerRadius: number) {
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

  public ngOnInit(): void { }
}
