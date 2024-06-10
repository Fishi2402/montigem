/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemSunburstChartExampleComponent, config } from '@src/example_charts/GemSunburstChartExampleComponent';
import { GemSunburstData } from '@umlp/charts';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemSunburstChartExample extends GemSunburstChartExampleComponent {
  private chartData: GemSunburstData;
  private chartDataHash: string;

  public override getGemSunburstChart2Data(): GemSunburstData {
    if (!this.persons) {
      return null;
    }
    let data: GemSunburstData = {
      entry: {
        label: "People separated in age groups",
        value: this.persons.length,
      },
      subEntries: [
        {
          entry: {
            label: "Number of people born before 1993",
            value: 0,
          },
          subEntries: [
            {
              entry: {
                label: "Number of people born before 1987",
                value: 0,
              },
            },
            {
              entry: {
                label: "Number of people born between 1987 and 1991",
                value: 0,
              },
            },
            {
              entry: {
                label: "Number of people after 1991",
                value: 0,
              }
            },
          ]
        },
        {
          entry: {
            label: "People born between 1993 and 1998",
            value: 0,
          },
          subEntries: [
            {
              entry: {
                label: "Number of people born before 1995",
                value: 0,
              },
            },
            {
              entry: {
                label: "Number of people born between 1995 and 1997",
                value: 0,
              },
            },
            {
              entry: {
                label: "Number of people born after 1997",
                value: 0,
              }
            },
          ]
        },
        {
          entry: {
            label: "People born after 1998",
            value: 0,
          },
          subEntries: [
            {
              entry: {
                label: "Number of people born before 2001",
                value: 0,
              },
            },
            {
              entry: {
                label: "Number of people born between 2001 and 2002",
                value: 0,
              },
            },
            {
              entry: {
                label: "Number of people born after 2002",
                value: 0,
              }
            },
          ],
        },
      ],
    };

    for (const person of this.persons) {
      let birthday = Number(person?.getBirthday()?.toDateString().substring(11, 15));
      if (birthday < 1993) {
        data.subEntries[0].entry.value++;
        if (birthday < 1987) {
          data.subEntries[0].subEntries[0].entry.value++;
        } else if (birthday < 1992) {
          data.subEntries[0].subEntries[1].entry.value++;
        } else {
          data.subEntries[0].subEntries[2].entry.value++;
        }
      } else if (birthday >= 1993 && birthday <= 1998) {
        data.subEntries[1].entry.value++;
        if (birthday < 1995) {
          data.subEntries[1].subEntries[0].entry.value++;
        } else if (birthday < 1998) {
          data.subEntries[1].subEntries[1].entry.value++;
        } else {
          data.subEntries[1].subEntries[2].entry.value++;
        }
      } else {
        data.subEntries[2].entry.value++;
        if (birthday < 2001) {
          data.subEntries[2].subEntries[0].entry.value++;
        } else if (birthday < 2003) {
          data.subEntries[2].subEntries[1].entry.value++;
        } else {
          data.subEntries[2].subEntries[2].entry.value++;
        }
      }
    }
    if (JSON.stringify(data) === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = data;
    this.chartDataHash = JSON.stringify(this.chartData);
    return this.chartData;
  }

}
