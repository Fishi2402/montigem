/* (c) https://github.com/MontiCore/monticore */

import { PieChartComponent } from '@src/person/PieChartComponent';
import { Component } from '@angular/core';
import { GemPieChartData } from '@umlp/charts';
import { GemPieChartEntry } from '@umlp/charts';

@Component({
  selector: 'pie-chart',
  templateUrl: '../../../build/frontendGen/build/appgui/person/PieChart.html',
})
export class PieChart extends PieChartComponent {

  private chartData: GemPieChartData;
  private chartDataHash: string;

  public override getChartData(): GemPieChartData {
    if (!this.persons) {
      return null;
    }
    let dataEntries: GemPieChartEntry[] = [
      {
        label: "People born before 1993",
        value: 0,
      },
      {
        label: "People born in and after 1993",
        value: 0,
      },
    ];

    let group1 = 0;
    let group2 = 0;

    for (const person of this.persons) {
      let personBirthday = Number(person?.getBirthday()?.toDateString().substring(11, 15));
      if (personBirthday < 1993) {
        group1++;
      } else {
        group2++;
      }
    }
    dataEntries[0].value = group1;
    dataEntries[1].value = group2;
    const newGraphData = {
      entries: dataEntries
    }
    if (JSON.stringify(newGraphData) === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = newGraphData;
    this.chartDataHash = JSON.stringify(this.chartData);
    console.log("People before 1993" + dataEntries[0].value);
    console.log("People after and in 1993" + dataEntries[1].value);
    return this.chartData;
  }


}