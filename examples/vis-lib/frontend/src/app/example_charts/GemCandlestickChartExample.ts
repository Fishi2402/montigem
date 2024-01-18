/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemCandlestickChartExampleComponent, config } from '@src/example_charts/GemCandlestickChartExampleComponent';
import { GemData2CandlestickChart } from '@umlp/charts';
import { Stock } from '@src/domain/Stock';

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
    let mapStocks = new Map<string, Stock[]>;
    for (let person of this.persons) {
      if (person.getStocks().length === 0) {
        continue;
      }
      let stock: Stock;
      for (stock of person.getStocks()) {
        if (mapStocks.has(stock.getDate().toDateString())) {
          let oldArr = mapStocks.get(stock.getDate().toDateString());
          if (!oldArr) {
            let newArr: Stock[] = [stock];
            mapStocks.set(stock.getDate().toDateString(), newArr);
          } else {
            oldArr.push(stock);
            mapStocks.set(stock.getDate().toDateString(), oldArr);
          }
        } else {
          let newArr: Stock[] = [stock];
          mapStocks.set(stock.getDate().toDateString(), newArr);
        }
      }
    }
    if (mapStocks.size <= 0) {
      return [];
    }

    for (let [date, arr] of mapStocks) {
      let value = [this.getOpen(arr), this.getHigh(arr), this.getLow(arr), this.getClose(arr)];
      let month = this.getMonth(date.substring(4, 7));
      let entry: GemData2CandlestickChart = {
        label: `${date.substring(11, 15)}-${month}-${date.substring(8, 10)}`,
        value: value
      }
      data.push(entry)
    }
    if (JSON.stringify(data) === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = data;
    this.chartDataHash = JSON.stringify(this.chartData);
    console.log(this.chartData);
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

  private getOpen(stocks: Stock[] | undefined): number {
    if (!stocks) {
      return 0;
    }
    let earliestHour: number = 25;
    let open: number = 0;
    for (let stock of stocks) {
      if (stock.getDate().getHours()< earliestHour) {
        earliestHour = stock.getDate().getHours();
        open = stock.getPrice();
      }
    }
    return open;
  }
  private getHigh(stocks: Stock[] | undefined): number {
    if (!stocks) {
      return 0;
    }
    let high: number = 0;
    for (let stock of stocks) {
      if (stock.getPrice() > high) {
        high = stock.getPrice();
      }
    }
    return high;
  }
  private getLow(stocks: Stock[] | undefined): number {
    if (!stocks) {
      return 0;
    }
    let low: number = 100000000;
    for (let stock of stocks) {
      if (stock.getPrice() < low) {
        low = stock.getPrice();
      }
    }
    return low;
  }
  private getClose(stocks: Stock[] | undefined): number {
    if (!stocks) {
      return 0;
    }
    let latestHour: number = -1;
    let close: number = 0;
    for (let stock of stocks) {
      if (stock.getDate().getHours() > latestHour) {
        latestHour = stock.getDate().getHours();
        close = stock.getPrice();
      }
    }
    return close;
  }

}
