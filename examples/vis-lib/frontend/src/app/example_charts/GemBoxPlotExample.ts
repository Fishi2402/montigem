/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemBoxPlotExampleComponent, config } from '@src/example_charts/GemBoxPlotExampleComponent';
import {GemBoxPlotEntry} from '@umlp/charts';

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
  secondData1 :GemBoxPlotEntry ={
    group: "Test A",
    color: "#69b3a2",
    points: [0,19,11,13,12,22,13,4,15,16,],
    box: {
      median: 13,
      lowerQuantile: 11,
      upperQuantile: 18,
      lowerExtreme: 0,
      upperExtreme: 22
    }
  }

  secondData = [this.secondData1]

  // Dummy data
  data1 :GemBoxPlotEntry ={
    group: "Test A",
    color: "#69b3a2",
    points: [12,19,11,13,12,22,13,4,15,16,18,19,20,12,11,9],
    box: {
      median: 13,
      lowerQuantile: 11,
      upperQuantile: 18,
      lowerExtreme: 4,
      upperExtreme: 22
    }
  }
  data2 :GemBoxPlotEntry ={
    group: "Test B",
    color: "#BFFFF4",
    points: [12,19,11,13,12,22,13,4,12,11,41],
    box: {
      median: 12,
      lowerQuantile: 12,
      upperQuantile: 19,
      lowerExtreme: 4,
      upperExtreme: 41
    }
  }

  data3 : GemBoxPlotEntry = {
    group: "Very long Description in Label C",
    points: [0,1,2,3,4,5,6,7,7,8,9,20,9,9,9],
    box: {
      median: 7,
      lowerQuantile: 3,
      upperQuantile: 9,
      lowerExtreme: 0,
      upperExtreme: 20
    }
  }
  data4 : GemBoxPlotEntry = {
    group: "Data 4",
    points: [0,1,2,3,4,5,6,7,7,8,9,20,9,9,9],
    box: {
      median: 7,
      lowerQuantile: 3,
      upperQuantile: 9,
      lowerExtreme: 0,
      upperExtreme: 20
    }
  }
  data5 : GemBoxPlotEntry = {
    group: "Data 5",
    points: [0,1,2,3,4,5,6,7,7,8,9,20,9,9,9],
    box: {
      median: 7,
      lowerQuantile: 3,
      upperQuantile: 9,
      lowerExtreme: 0,
      upperExtreme: 20
    }
  }
  data6 : GemBoxPlotEntry = {
    group: "Data 6",
    points: [0,1,2,3,4,5,6,7,7,8,9,20,9,9,9],
    box: {
      median: 7,
      lowerQuantile: 3,
      upperQuantile: 9,
      lowerExtreme: 0,
      upperExtreme: 20
    }
  }
  data7 : GemBoxPlotEntry = {
    group: "Data 7",
    points: [0,1,2,3,4,5,6,7,7,8,9,20,9,9,9],
    box: {
      median: 7,
      lowerQuantile: 3,
      upperQuantile: 9,
      lowerExtreme: 0,
      upperExtreme: 20
    }
  }
  data8 : GemBoxPlotEntry = {
    group: "Data 8",
    points: [0,1,2,3,4,5,6,7,7,8,9,20,9,9,9],
    box: {
      median: 7,
      lowerQuantile: 3,
      upperQuantile: 9,
      lowerExtreme: 0,
      upperExtreme: 20
    }
  }

  data = [this.data1, this.data2, this.data3, this.data4,this.data5,this.data6,this.data7,this.data8]

}
