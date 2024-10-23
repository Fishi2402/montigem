/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemScatterPlotExampleComponent, config } from '@src/example_charts/GemScatterPlotExampleComponent';
import {
  GemScatterPlotData,
  GemScatterPlotAxis,
  GemScatterPlotSet,
  GemScatterPlotPoint,
  GemScatterPlotShape,
  GemScatterPlotPointBuilder, GemScatterPlotSetBuilder, GemScatterPlotDataBuilder
} from '@umlp/charts';
import {StringifyForBuilder} from "@umlp/commonj2ts";

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemScatterPlotExample extends GemScatterPlotExampleComponent {

  private simpleData: GemScatterPlotData;
  private simpleDataHash: string;

  public override getSimpleData() : GemScatterPlotData {
    let greenSet: GemScatterPlotSet = new GemScatterPlotSetBuilder()
        .label( "Green data")
        .color( "green")
        .points( [
          new GemScatterPlotPointBuilder().x(7).y(5).build(),
          new GemScatterPlotPointBuilder().x(9).y(6).build(),
          new GemScatterPlotPointBuilder().x(10).y(7).build()])
        .build()

    let blueSet: GemScatterPlotSet = new GemScatterPlotSetBuilder()
        .label("Blue data")
        .color( "blue")
        .points([new GemScatterPlotPointBuilder().x(3).y(3).build()])
        .build()

    let data: GemScatterPlotData = new GemScatterPlotDataBuilder().sets([greenSet, blueSet]).build()

    let hash: string = StringifyForBuilder(data);
    if (this.simpleData && this.simpleDataHash === hash) {
      return this.simpleData;
    }

    this.simpleData = data;
    this.simpleDataHash = hash;

    return data;
  }
}
