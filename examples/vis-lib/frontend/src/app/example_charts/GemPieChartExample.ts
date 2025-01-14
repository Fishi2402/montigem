/* (c) https://github.com/MontiCore/monticore */

import { GemPieChartExampleComponent, config } from 'example_charts/GemPieChartExampleComponent';
import { Component } from '@angular/core';
import {GemPieChartData, GemPieChartDataBuilder, GemPieChartEntryBuilder} from '@umlp/charts';
import { GemPieChartEntry } from '@umlp/charts';
import {StringifyForBuilder} from "@umlp/commonj2ts";

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemPieChartExample extends GemPieChartExampleComponent {
  private chartData: GemPieChartData;
  private chartDataHash: string;

  public override getChartData(): GemPieChartData {
    if (!this.persons) {
      return null;
    }
    let dataEntries: GemPieChartEntry[] = [
        new GemPieChartEntryBuilder().label("People born before 1993").value(0).build(),
        new GemPieChartEntryBuilder().label("People born in and after 1993").value(0).build()
    ];

    let group1 = 0;
    let group2 = 0;

    for (const person of this.persons) {
      let personBirthday = Number(person?.getBirthday()?.toDateString().substring(11, 15));
      if (personBirthday < 1993) {
        group1++;
      } else {
        group2++;
      }
    }
    dataEntries[0].setValue(group1);
    dataEntries[1].setValue(group2);
    const newGraphData = new GemPieChartDataBuilder().entries(dataEntries).build()
    if (StringifyForBuilder(newGraphData) === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = newGraphData;
    this.chartDataHash = StringifyForBuilder(this.chartData);
    return this.chartData;
  }
}
