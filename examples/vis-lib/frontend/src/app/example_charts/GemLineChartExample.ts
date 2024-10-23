/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemLineChartExampleComponent, config } from '@src/example_charts/GemLineChartExampleComponent';
import {
  GemLineChartData,
  GemLineChartDataBuilder,
  GemLineChartEntry,
  GemLineChartEntryBuilder
} from '@umlp/charts';
import { Stock } from '@src/domain/Stock';
import { Person } from '@src/domain/Person';
import {StringifyForBuilder} from "@umlp/commonj2ts";

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
    type AgeGroups = {
      before_1987: Person[],
      between_1987_and_1991: Person[],
      between_1991_and_1993: Person[],
      between_1993_and_1998: Person[],
      after_1998: Person[]
    }
    let ageGroups: AgeGroups = {
      before_1987: [],
      between_1987_and_1991: [],
      between_1991_and_1993: [],
      between_1993_and_1998: [],
      after_1998: []
    }
    let person: Person;
    for (person of this.persons) {
      let birthday = Number(person?.getBirthday()?.toDateString().substring(11, 15));
      if (birthday < 1987) {
        ageGroups.before_1987.push(person);
      } else if (birthday < 1991) {
        ageGroups.between_1987_and_1991.push(person);
      } else if (birthday < 1993) {
        ageGroups.between_1991_and_1993.push(person);
      } else if (birthday < 1998) {
        ageGroups.between_1993_and_1998.push(person);
      } else {
        ageGroups.after_1998.push(person);
      }
    }
    for (const [groupName, groupArr] of Object.entries(ageGroups)) {
      let stocks = [0, 0, 0, 0, 0];
      for (let person of groupArr) {
        if (person.getStocksList().length === 0) {
          continue;
        }
        for (let stock of person.getStocksList()) {
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
      }
      let ageGroupname: String = groupName.replace(/_/g, " ");
      let entry = new GemLineChartEntryBuilder()
          .data(stocks)
          .label(`Stocks bought by people born ${ageGroupname}`)
          .build()
      entries.push(entry);
    }
    let data: GemLineChartData = new GemLineChartDataBuilder().labels(labels).entries(entries).build()

    if (StringifyForBuilder(data) === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = data;
    this.chartDataHash = StringifyForBuilder(this.chartData);
    return this.chartData;
  }

}
