/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemBarChartExampleComponent, config } from '@src/example_charts/GemBarChartExampleComponent';
import { GemBarChartData, GemBarChartEntry } from '@umlp/charts';

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
      {
        label: "People with less than 2 friends",
        data: [0, 0, 0],
      },
      {
        label: "People with more than 2 friends",
        data: [0, 0, 0],
      },
    ];

    for (const person of this.persons) {
      let birthday = Number(person?.getBirthday()?.toDateString().substring(11, 15));
      let numberFriends = person.getFriends().length;
      if (birthday < 1993) {
        if (numberFriends <= 2) {
          dataEntries[0].data[0] += 1;
        } else {
          dataEntries[1].data[0] += 1;
        }
      } else if (birthday >= 1993 && birthday <= 1998) {
        if (numberFriends <= 2) {
          dataEntries[0].data[1] += 1;
        } else {
          dataEntries[1].data[1] += 1;
        }
      } else {
        if (numberFriends <= 2) {
          dataEntries[0].data[2] += 1;
        } else {
          dataEntries[1].data[2] += 1;
        }
      }
    }

    let data: GemBarChartData = {
      labels: labels,
      entries: dataEntries
    }

    if (JSON.stringify(data) === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = data;
    this.chartDataHash = JSON.stringify(this.chartData);
    console.log(this.chartData.entries);
    console.log(this.chartData.labels);
    return this.chartData;
  }

}
