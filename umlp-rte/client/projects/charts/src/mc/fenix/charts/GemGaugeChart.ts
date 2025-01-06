/* (c) https://github.com/MontiCore/monticore */

import { Component, Input } from '@angular/core';
import { config, GemGaugeChartComponent } from '../../../../build/gui/mc/fenix/charts/GemGaugeChartComponent';
import { NgxChartsModule, ScaleType } from "@swimlane/ngx-charts";
import { GemGaugeChartData } from '../../../../build/cd/mc/fenix/charts/gemgaugecharttypes/GemGaugeChartData';
import { autoColors } from './colors';
import { GemChartLegend } from './GemChartLegend';
import { GemChartOptions } from './GemChartOptions';

@Component({
  standalone: true,
  imports: [...config.imports, NgxChartsModule, GemChartOptions, GemChartLegend],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemGaugeChart extends GemGaugeChartComponent {
  public labels: string[];

  /** data */
  public dataSets: any = [];

  /**all colors of the entries */
  public colors = [];

  /** used intern for showing the axis when function showAxis() is called  */
  public _showAxis: boolean = true;

  /** intern variable to store the sensorType, can be changed via this.sensorType from extern */
  public _sensorType: any = "All";

  /** manually set how many ticks should be displayed */
  public bigTicks: number;

  /** manually set how many small ticks between the big ticks should be displayed */
  public smallTicks: number;

  /** data that is actually shown */
  public selectedDataSet: any = [];

  /**labels for chart legend */
  public legendLabels: string[];

  /**colors for chart legend */
  public legendColors: string[] = [];

  /** defines the position of the legend */
  public legendPosition: string;

  /**variable that enables or disables the legend */
  public displayLegend = true;

  /** default settings */
  view: any[] = [400, 400];

  /**gives the colors from the constant autoColors */
  public getColors() {
    return autoColors;
  }

  /** color scheme for chart */
  public colorScheme = {
    name: 'default',
    selectable: true,
    group: ScaleType.Linear,
    domain: this.getColors()
  };

  ngOnInit(): void {
    this.recalcBigTicks();
  }

  constructor() {
    super();
  }

  /** when set it is displayed under the text value */
  @Input()
  public set unit(unit: string) {
    this._unit = unit;
  }

  /**
   * refreshes the displayed data, when the new given data has at least one element
   * when there is no data given it uses the old data
   */
  @Input()
  public set data(data: GemGaugeChartData) {
    if (!data) {
      return;
    }
    this.labels = data.getEntriesList().map(entry => entry.getName());
    this.dataSets = [];
    const colors = [];
    const labels = [];
    let autocolors = this.getColors();
    let pointerAutoColors = 0;

    for (let i = 0; i < data.getEntriesList().length; i++) {
      if (!CSS.supports('color', data.getEntries(i).getBackgroundColor())) {
        data.getEntries(i).setBackgroundColor(autocolors[pointerAutoColors++]);
      }
      // Used library expects these fields, so we cannot use our generated datatypes
      let entry = {
        value: data.getEntries(i).getValue(),
        name: data.getEntries(i).getName(),
        type: data.getEntries(i).getType(),
        backgroundColor: data.getEntries(i).getBackgroundColor()
      };

      labels.push(entry.name);
      colors.push(entry.backgroundColor);
      this.dataSets.push(entry);
    }

    this.legendLabels = labels;
    this.legendColors = colors;

    let t = this;

    // Rename Sensor 1 to CO2 and Sensor 2 to Temperature
    this.dataSets.forEach(function (entry) {
      if (entry.name == "1") {
        entry.name = "CO2";
      } else if (entry.name == "2") {
        entry.name = "Temperature";
      }
    });

    this.dataSets.forEach(function (entry) {
      if (entry.value < t._min) {
        // Round down to whole tens
        t._min = Math.floor(entry.value / 10) * 10;
        t.recalcBigTicks();
      }
      if (entry.value > t._max) {
        // Round up to whole tens
        t._max = Math.ceil(entry.value / 10) * 10;
        t.recalcBigTicks();
      }

    });

    this.updateSelectedDataset();
  }

  /** set SensorType from extern to filter the data */
  @Input()
  set sensorType(sensorType) {
    this._sensorType = sensorType;
    this.updateSelectedDataset();
  }

  /**
   * function to calculate the count of the big Ticks
   * so the count will be a multiple of 10 or 100 depending on the data
   */
  recalcBigTicks() {
    this.bigTicks = (this._max - this._min) / 10; // Every 10 steps
    while (this.bigTicks > 15) { // As long as more than 10 Ticks, half by quantity
      if (this.bigTicks >= 100) {
        this.bigTicks /= 10;
      } else {
        this.bigTicks /= 10;
      }
    }
    this.bigTicks = Math.round(this.bigTicks);
  }

  /** for input range to select smallTicksCount */
  smallTickResizer(n: string | number) {
    this.smallTicks = +n + 1;
    console.log(n);
  }

  changeMin(min: string) {
    this._min = +min;
  }

  changeMax(max: string) {
    this._max = +max;
  }

  changeUnit(unit: string) {
    this._unit = unit;
  }

  /** get the setted sensorFilter */
  get sensorType(): any {
    return this._sensorType;
  }

  /** only show selected datatype */
  updateSelectedDataset() {
    this.selectedDataSet = [];
    this._textValue = this._sensorType;

    let t = this;

    this.dataSets.forEach(function (entry: any) {
      if (t._sensorType.toLowerCase() == "all"
        || entry.type.toLowerCase() == t._sensorType.toLowerCase()) {
        t.selectedDataSet.push(Object.assign({}, entry));
      }
    });

  }

  /** returns wether to show a when a text is set or don't show it when no text is set */
  public get showText(): boolean {
    return !!this._textValue;
  }

  /** changes the axis visibility and resets the slider */
  public set showAxis(checked: boolean) {
    this._showAxis = checked;
  }

  /** returns if the axis are visible */
  public get showAxis(): boolean {
    return this._showAxis;
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