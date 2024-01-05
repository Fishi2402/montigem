/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemLineChartExampleComponent, config } from '@src/example_charts/GemLineChartExampleComponent';
import { GemLineChartData, GemLineChartEntry } from '@umlp/charts';

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
    let labels: string[] = ["People born before 1993", "People born between 1993 and 1998", "People born between 1998 and 2003", "People born after 2003"];

    let entries: GemLineChartEntry[] = [
      { data: [0, 0, 0, 0], label: '#born people with less than 2 friends' },
      { data: [0, 0, 0, 0], label: '#born people with 2 friends' },
      { data: [0, 0, 0, 0], label: '#born people with more than 2 friends' },
    ];

    for (const person of this.persons) {
      let birthday = Number(person?.getBirthday()?.toDateString().substring(11, 15));
      let numberFriends = person.getFriends().length;
      if (birthday < 1993) {
        if (numberFriends < 2) {
          entries[0].data[0]++;
        } else if (numberFriends === 2) {
          entries[1].data[0]++;
        } else {
          entries[2].data[0]++;
        }
      } else if (birthday >= 1993 && birthday <= 1998) {
        if (numberFriends < 2) {
          entries[0].data[1]++;
        } else if (numberFriends === 2) {
          entries[1].data[1]++;
        } else {
          entries[2].data[1]++;
        }
      } else if (birthday >= 1999 && birthday <= 2003) {
        if (numberFriends < 2) {
          entries[0].data[2]++;
        } else if (numberFriends === 2) {
          entries[1].data[2]++;
        } else {
          entries[2].data[2]++;
        }
      } else {
        if (numberFriends <= 2) {
          entries[0].data[3]++;
        } else if (numberFriends === 2) {
          entries[1].data[3]++;
        } else {
          entries[2].data[3]++;
        }
      }
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
    return this.chartData;
  }

}
