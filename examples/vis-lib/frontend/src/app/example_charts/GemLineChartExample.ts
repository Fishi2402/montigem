/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemLineChartExampleComponent, config } from '@src/example_charts/GemLineChartExampleComponent';
import { GemLineChartData, GemLineChartEntry } from '@umlp/charts';
import { Stock } from '@src/domain/Stock';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemLineChartExample extends GemLineChartExampleComponent {
  private chartData: GemLineChartData;
  private chartDataHash: string;

  public override getLinechartData(): GemLineChartData {
    if (!this.persons) {
      return null;
    }
    let labels: string[] = ["01/01/2022", "02/01/2022", "03/01/2022", "04/01/2022", "05/01/2022",];
    let entries: GemLineChartEntry[] = [];
    for (let person of this.persons) {
      if (person.getStocks().length === 0) {
        continue;
      }
      let stocks = [0, 0, 0, 0, 0];
      for (let stock of person.getStocks()) {
        switch (stock.getDate().toDateString().substring(8, 10)) {
          case "01": {
            stocks[0] += stock.getPrice(); 
            break;
          }
          case "02": {
            stocks[1] += stock.getPrice(); 
            break;
          }
          case "03": {
            stocks[2] += stock.getPrice(); 
            break;
          }
          case "04": {
            stocks[3] += stock.getPrice(); 
            break;
          }
          case "05": {
            stocks[4] += stock.getPrice(); 
            break;
          }
        }
      }
      let entry = {
        data: stocks,
        labels: `Stocks bought by ${person.getName()}`
      }
      entries.push(entry);

    }

    let data: GemLineChartData = {
      labels: labels,
      entries: entries
    }

    if (JSON.stringify(data) === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = data;
    this.chartDataHash = JSON.stringify(this.chartData);
    console.log(this.chartData);
    return this.chartData;
  }

}
