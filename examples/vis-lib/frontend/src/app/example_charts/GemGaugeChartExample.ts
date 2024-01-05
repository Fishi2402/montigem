/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemGaugeChartExampleComponent, config } from '@src/example_charts/GemGaugeChartExampleComponent';
import { GemGaugeChartData, GemGaugeChartEntry } from '@umlp/charts';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemGaugeChartExample extends GemGaugeChartExampleComponent {
  public chartData: GemGaugeChartData;
  public chartDataHash: string;

  public override getGemGaugeChart0Data(): GemGaugeChartData {
    if (!this.persons) {
      return null;
    }
    let entries: GemGaugeChartEntry[] = [
      {
        name: "People born before 1993",
        value: 0,
        type: '#friends'
      },
      {
        name: "People born between 1993 and 1998",
        value: 0,
        type: '#friends'
      },
      {
        name: "People born after 1998",
        value: 0,
        type: '#friends'
      }
    ];
    let numberFriendsGroup1 = 0;
    let numberPeopleInGroup1 = 0;
    let numberFriendsGroup2 = 0;
    let numberPeopleInGroup2 = 0;
    let numberFriendsGroup3 = 0;
    let numberPeopleInGroup3 = 0;
    for (const person of this.persons) {
      let birthday = Number(person?.getBirthday()?.toDateString().substring(11, 15));
      if (birthday < 1993) {
        numberFriendsGroup1 += person.getFriends().length;
        numberPeopleInGroup1++;
      } else if (birthday >= 1993 && birthday <= 1998) {
        numberFriendsGroup2 += person.getFriends().length;
        numberPeopleInGroup2++;
      }
      else {
        numberFriendsGroup3 += person.getFriends().length;
        numberPeopleInGroup3++;
      }
    }

    entries[0].value = Math.round(numberFriendsGroup1 / numberPeopleInGroup1);
    entries[1].value = Math.round(numberFriendsGroup2 / numberPeopleInGroup2);
    entries[2].value = Math.round(numberFriendsGroup3 / numberPeopleInGroup3);

    let data: GemGaugeChartData = { entries: entries };

    if (JSON.stringify(data) === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = data;
    this.chartDataHash = JSON.stringify(this.chartData);
    return this.chartData;
  }

}
