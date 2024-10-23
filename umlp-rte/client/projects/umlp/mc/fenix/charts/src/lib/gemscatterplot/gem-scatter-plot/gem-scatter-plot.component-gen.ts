import {Directive} from '@angular/core';
import { GemScatterPlotData} from '../../gen/gemscatterplottypes/GemScatterPlotData';
import { GemScatterPlotAxis} from '../../gen/gemscatterplottypes/GemScatterPlotAxis';
import {Input} from '@angular/core';

@Directive()
export class GemScatterPlot  {
  protected  _data: GemScatterPlotData;
  protected _xAxis: GemScatterPlotAxis;
  protected _yAxis: GemScatterPlotAxis;

  public get data(): GemScatterPlotData {
    return this._data;
  }

  @Input()
  public set data(data: GemScatterPlotData) {
    this._data = data;
  }

  public get xAxis(): GemScatterPlotAxis {
    return this._xAxis;
  }

  @Input()
  public set xAxis(axis: GemScatterPlotAxis) {
    this._xAxis = axis;
  }

  public get yAxis(): GemScatterPlotAxis {
    return this._yAxis;
  }

  @Input()
  public set yAxis(axis: GemScatterPlotAxis) {
    this._yAxis = axis;
  }
}
