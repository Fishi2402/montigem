/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemBoxPlotExampleComponent, config } from '@src/example_charts/GemBoxPlotExampleComponent';
import {GemBoxPlotEntry, GemBoxPlotEntryBuilder} from '@umlp/charts';
import {GemBoxPlotBox, GemBoxPlotBoxBuilder} from '@umlp/charts';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemBoxPlotExample extends GemBoxPlotExampleComponent {

  public override getBoxplotWidth(): number {
    return this.data.length*150
  }

  public override getBoxplotHeight(): number {
    return this.data.length*150
  }

  public override getSecondBoxplotWidth(): number {
    return this.secondData.length*150
  }

  public override getSecondBoxplotHeight(): number {
    return this.secondData.length*150
  }

  public override getSecondBoxPlotData(): GemBoxPlotEntry[] {

    return this.secondData;
  }

  public override getBoxplotEntries(): GemBoxPlotEntry[] {

    return this.data;
  }

  // Dummy data
  secondData1 :GemBoxPlotEntry =
      new GemBoxPlotEntryBuilder()
          .group("Test A")
          .color("#69b3a2")
          .points( [0,19,11,13,12,22,13,4,15,16,])
          .box(new GemBoxPlotBoxBuilder()
              .median(13)
              .lowerQuantile(11)
              .upperQuantile(18)
              .lowerExtreme(0)
              .upperExtreme(22)
              .build())
          .build()

  secondData = [this.secondData1]

  // Dummy data
  data1 :GemBoxPlotEntry =
      new GemBoxPlotEntryBuilder()
          .group("Test A")
          .color("#69b3a2")
          .points(  [12,19,11,13,12,22,13,4,15,16,18,19,20,12,11,9])
          .box(new GemBoxPlotBoxBuilder()
              .median(13)
              .lowerQuantile(11)
              .upperQuantile(18)
              .lowerExtreme(4)
              .upperExtreme(22)
              .build())
          .build()

  data2 :GemBoxPlotEntry =
      new GemBoxPlotEntryBuilder()
          .group("Test B")
          .color("#BFFFF4")
          .points(  [12,19,11,13,12,22,13,4,12,11,41])
          .box(new GemBoxPlotBoxBuilder()
              .median(12)
              .lowerQuantile(12)
              .upperQuantile(19)
              .lowerExtreme(4)
              .upperExtreme(41)
              .build())
          .build();


  data3 : GemBoxPlotEntry=
      new GemBoxPlotEntryBuilder()
          .group("Very long Description in Label C")
          // No Color defined
          .points(  [12,19,11,13,12,22,13,4,12,11,41])
          .box(new GemBoxPlotBoxBuilder()
              .median(7)
              .lowerQuantile(3)
              .upperQuantile(9)
              .lowerExtreme(0)
              .upperExtreme(20)
              .build())
          .build();

  data4 : GemBoxPlotEntry =
      new GemBoxPlotEntryBuilder()
          .group("Data 4")
          // No Color defined
          .points(  [0,1,2,3,4,5,6,7,7,8,9,20,9,9,9])
          .box(new GemBoxPlotBoxBuilder()
              .median(7)
              .lowerQuantile(3)
              .upperQuantile(9)
              .lowerExtreme(0)
              .upperExtreme(20)
              .build())
          .build();

  data5 : GemBoxPlotEntry =
      new GemBoxPlotEntryBuilder()
          .group("Data 5")
          // No Color defined
          .points(   [0,1,2,3,4,5,6,7,7,8,9,20,9,9,9])
          .box(new GemBoxPlotBoxBuilder()
              .median(7)
              .lowerQuantile(3)
              .upperQuantile(9)
              .lowerExtreme(0)
              .upperExtreme(20)
              .build())
          .build();

  data6 : GemBoxPlotEntry =
      new GemBoxPlotEntryBuilder()
          .group("Data 6")
          // No Color defined
          .points(   [0,1,2,3,4,5,6,7,7,8,9,20,9,9,9])
          .box(new GemBoxPlotBoxBuilder()
              .median(7)
              .lowerQuantile(3)
              .upperQuantile(9)
              .lowerExtreme(0)
              .upperExtreme(20)
              .build())
          .build();

  data7 : GemBoxPlotEntry =
      new GemBoxPlotEntryBuilder()
          .group("Data 7")
          // No Color defined
          .points(   [0,1,2,3,4,5,6,7,7,8,9,20,9,9,9])
          .box(new GemBoxPlotBoxBuilder()
              .median(7)
              .lowerQuantile(3)
              .upperQuantile(9)
              .lowerExtreme(0)
              .upperExtreme(20)
              .build())
          .build();
  data8 : GemBoxPlotEntry =
      new GemBoxPlotEntryBuilder()
          .group("Data 7")
          // No Color defined
          .points(   [0,1,2,3,4,5,6,7,7,8,9,20,9,9,9])
          .box(new GemBoxPlotBoxBuilder()
              .median(7)
              .lowerQuantile(3)
              .upperQuantile(9)
              .lowerExtreme(0)
              .upperExtreme(20)
              .build())
          .build();

  data = [this.data1, this.data2, this.data3, this.data4,this.data5,this.data6,this.data7,this.data8]

}
