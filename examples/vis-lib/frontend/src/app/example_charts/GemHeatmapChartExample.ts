/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemHeatmapChartExampleComponent, config } from 'example_charts/GemHeatmapChartExampleComponent';
import {
  GemHeatmapChartData,
  GemHeatmapChartDataBuilder,
  GemHeatmapChartEntry, GemHeatmapChartEntryBuilder
} from '@umlp/charts';
import {StringifyForBuilder} from "@umlp/commonj2ts";

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemHeatmapChartExample extends GemHeatmapChartExampleComponent {
  private chartData : GemHeatmapChartData;
  private chartDataHash: string;

  public override getGemHeatmapChart0Data(): GemHeatmapChartData {
    if (!this.persons) {
      return null;
    }
    type Entry = {
      name: string,
      value: number
    }
    let entries: GemHeatmapChartEntry[] = [
        new GemHeatmapChartEntryBuilder().name("Older than 32").timestamp(0).build(),
        new GemHeatmapChartEntryBuilder().name("Between 30 and 32").timestamp(0).build(),
        new GemHeatmapChartEntryBuilder().name("Between 28 and 29").timestamp(0).build(),
        new GemHeatmapChartEntryBuilder().name("Between 27 and 23").timestamp(0).build(),
        new GemHeatmapChartEntryBuilder().name("Younger than 23").timestamp(0).build()
    ];
    for (const person of this.persons) {
      if (person.getStocksList().length === 0) {
        continue;
      }
      let birthday = Number(person?.getBirthday()?.toDateString().substring(11, 15));
      let sumStocks: number = 0;
      for (let stock of person.getStocksList()) {
        sumStocks += stock.getPrice();
      }
      if (birthday < 1992) {
        entries[0].setTimestamp(entries[0].getTimestamp() + sumStocks);
      } else if (birthday < 1995) {
        entries[1].setTimestamp(entries[1].getTimestamp() + sumStocks);
      } else if (birthday < 1997) {
        entries[2].setTimestamp(entries[2].getTimestamp() + sumStocks);
      } else if (birthday < 2002) {
        entries[3].setTimestamp(entries[3].getTimestamp() + sumStocks);
      } else {
        entries[4].setTimestamp(entries[4].getTimestamp() + sumStocks);
      }
    }
    let data = new GemHeatmapChartDataBuilder().entries(entries).build()


    if (StringifyForBuilder(data) === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = data;
    this.chartDataHash = StringifyForBuilder(this.chartData);
    return this.chartData;
  }


}
