/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemBarChartExampleComponent, config } from 'example_charts/GemBarChartExampleComponent';
import {
  GemBarChartData,
  GemBarChartDataBuilder,
  GemBarChartEntry,
  GemBarChartEntryBuilder
} from '@umlp/charts';
import {StringifyForBuilder} from "@umlp/commonj2ts";

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemBarChartExample extends GemBarChartExampleComponent {
  private chartData: GemBarChartData;
  private chartDataHash: string;

  public override getBarchartData(): GemBarChartData {
    if (!this.persons) {
      return null;
    }
    let labels: string[] = ["People born before 1993", "People born between 1993 and 1998", "People born after 1998"];

    let dataEntries: GemBarChartEntry[] = [
      new GemBarChartEntryBuilder()
          .label("People with less than 2 friends")
          .data([0, 0, 0])
          .build(),
      new GemBarChartEntryBuilder()
          .label("People with more than 2 friends")
          .data([0, 0, 0])
          .build()
    ];

    for (const person of this.persons) {
      let birthday = Number(person?.getBirthday()?.toDateString().substring(11, 15));
      let numberFriends = person.getFriendsList().length;
      if (birthday < 1993) {
        if (numberFriends <= 2) {
          dataEntries[0]._data[0] += 1;
        } else {
          dataEntries[1]._data[0] += 1;
        }
      } else if (birthday >= 1993 && birthday <= 1998) {
        if (numberFriends <= 2) {
          dataEntries[0]._data[1] += 1;
        } else {
          dataEntries[1]._data[1] += 1;
        }
      } else {
        if (numberFriends <= 2) {
          dataEntries[0]._data[2] += 1;
        } else {
          dataEntries[1]._data[2] += 1;
        }
      }
    }

    let data = new GemBarChartDataBuilder()
        .labels(labels)
        .entries(dataEntries)
        .build();

    let dataString =StringifyForBuilder(data)
    if (dataString === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = data;

    let chartDataString = StringifyForBuilder(this.chartData);
    this.chartDataHash = chartDataString;
    return this.chartData;
  }

}
