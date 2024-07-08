/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemHeatmapChartExampleComponent, config } from '@src/example_charts/GemHeatmapChartExampleComponent';
import { GemHeatmapChartData, GemHeatmapChartEntry } from '@umlp/charts';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemHeatmapChartExample extends GemHeatmapChartExampleComponent {
  private chartData;
  private chartDataHash: string;

  public override getGemHeatmapChart0Data(): GemHeatmapChartData {
    if (!this.persons) {
      return null;
    }
    type Entry = {
      name: string,
      value: number
    }
    let entries: Entry[] = [
      {
        name: "Older than 32",
        value: 0
      }, {
        name: "Between 30 and 32",
        value: 0
      }, {
        name: "Between 28 and 29",
        value: 0
      }, {
        name: "Between 27 and 23",
        value: 0
      }, {
        name: "Younger than 23",
        value: 0
      }
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
        entries[0].value += sumStocks;
      } else if (birthday < 1995) {
        entries[1].value += sumStocks;
      } else if (birthday < 1997) {
        entries[2].value += sumStocks;
      } else if (birthday < 2002) {
        entries[3].value += sumStocks;
      } else {
        entries[4].value += sumStocks;
      }
    }
    let data = {
      entries: entries
    }

    if (JSON.stringify(data) === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = data;
    this.chartDataHash = JSON.stringify(this.chartData);
    return this.chartData;
  }


}
