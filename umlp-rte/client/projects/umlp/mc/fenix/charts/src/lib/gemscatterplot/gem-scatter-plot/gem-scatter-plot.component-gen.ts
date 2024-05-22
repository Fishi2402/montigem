import {Directive} from '@angular/core';
import {GemScatterPlotData} from '../gem-scatter-plot-types/gem-scatter-plot-types.types-gen';
import {GemScatterPlotAxis} from '../gem-scatter-plot-types/gem-scatter-plot-types.types-gen';
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
