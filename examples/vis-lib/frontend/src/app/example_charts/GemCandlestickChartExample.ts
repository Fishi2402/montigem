/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemCandlestickChartExampleComponent, config } from '@src/example_charts/GemCandlestickChartExampleComponent';
import { GemData2CandlestickChart } from '@umlp/charts';
import { Person } from '@src/domain/Person';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemCandlestickChartExample extends GemCandlestickChartExampleComponent {
  public chartData: GemData2CandlestickChart[];
  public chartDataHash: string;
  public override getGemCandlestickChart0Data() {
    if (!this.persons) {
      return null;
    }
    let data: GemData2CandlestickChart[] = [];
    let personWithStocks: Person;
    for (let person of this.persons) {
      if (person.getStocks().length > 0) {
        personWithStocks = person;
        break;
      }
    }
    if (!personWithStocks) {
      return [];
    }
    for (let stock of personWithStocks.getStocks()) {
      let value = [stock.getOpen(), stock.getHigh(), stock.getLow(), stock.getClose()];
      let month = this.getMonth(stock.getDate().toDateString().substring(4, 7));
      let entry: GemData2CandlestickChart = {
        label: `${stock.getDate().toDateString().substring(11, 15)}-${month}-${stock.getDate().toDateString().substring(8, 10)}`,
        value: value
      }
      data.push(entry);
    }
    if (JSON.stringify(data) === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = data;
    this.chartDataHash = JSON.stringify(this.chartData);
    return this.chartData;
  }

  private getMonth(month: string): string {
    let res = "01";
    switch (month) {
      case "Jan":
        res = "01";
        break;
      case "Feb":
        res = "02";
        break;
      case "Mar":
        res = "03";
        break;
      case "Apr":
        res = "04";
        break;
      case "May":
        res = "05";
        break;
      case "Jun":
        res = "06";
        break;
      case "Jul":
        res = "07";
        break;
      case "Aug":
        res = "08";
        break;
      case "Sep":
        res = "09";
        break;
      case "Oct":
        res = "10";
        break;
      case "Nov":
        res = "11";
        break;
      case "Dec":
        res = "12";
        break;
    }
    return res;
  }
}
