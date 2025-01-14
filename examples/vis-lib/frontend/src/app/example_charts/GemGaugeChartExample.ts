/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemGaugeChartExampleComponent, config } from 'example_charts/GemGaugeChartExampleComponent';
import { GemGaugeChartData, GemGaugeChartDataBuilder, GemGaugeChartEntry, GemGaugeChartEntryBuilder } from '@umlp/charts';
import {StringifyForBuilder} from "@umlp/commonj2ts";

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
        new GemGaugeChartEntryBuilder()
            .name("People born before 1993")
            .value(0)
            .type( '#friends')
            .build(),
        new GemGaugeChartEntryBuilder()
            .name("People born between 1993 and 1998")
            .value(0)
            .type( '#friends')
            .build(),
        new GemGaugeChartEntryBuilder()
            .name("People born after 1998")
            .value(0)
            .type( '#friends')
            .build()
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
        numberFriendsGroup1 += person.getFriendsList().length;
        numberPeopleInGroup1++;
      } else if (birthday >= 1993 && birthday <= 1998) {
        numberFriendsGroup2 += person.getFriendsList().length;
        numberPeopleInGroup2++;
      }
      else {
        numberFriendsGroup3 += person.getFriendsList().length;
        numberPeopleInGroup3++;
      }
    }

    entries[0].setValue(Math.round(numberFriendsGroup1 / numberPeopleInGroup1) * 10)
    entries[1].setValue(Math.round(numberFriendsGroup2 / numberPeopleInGroup2) * 10);
    entries[2].setValue(Math.round(numberFriendsGroup3 / numberPeopleInGroup3) * 10);

    let data: GemGaugeChartData = new GemGaugeChartDataBuilder().entries(entries).build();

    if (StringifyForBuilder(data) === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = data;
    this.chartDataHash = StringifyForBuilder(this.chartData);
    return this.chartData;
  }

}
