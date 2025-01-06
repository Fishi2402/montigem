/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemSunburstChartExampleComponent, config } from 'example_charts/GemSunburstChartExampleComponent';
import {GemSunburstData, GemSunburstDataBuilder, GemSunburstEntryBuilder} from '@umlp/charts';
import {StringifyForBuilder} from "@umlp/commonj2ts";

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
    let data: GemSunburstData =
        new GemSunburstDataBuilder()
            .entry(
                new GemSunburstEntryBuilder().label("People separated in age groups").value(this.persons.length).build()
            )
            .subEntries( [
                new GemSunburstDataBuilder()
                    .entry(
                        new GemSunburstEntryBuilder().label("Number of people born before 1993").value(0).build()
                    )
                    .subEntries([
                      new GemSunburstDataBuilder()
                          .entry(
                              new GemSunburstEntryBuilder().label("Number of people born before 1987").value(0).build()
                          )
                          .build(),
                      new GemSunburstDataBuilder()
                          .entry(
                              new GemSunburstEntryBuilder().label("Number of people born between 1987 and 1991").value(0).build()
                          )
                          .build(),
                      new GemSunburstDataBuilder()
                          .entry(
                              new GemSunburstEntryBuilder().label("Number of people after 1991").value(0).build()
                          )
                          .build(),
                    ])
                    .build(),
                new GemSunburstDataBuilder()
                    .entry(
                        new GemSunburstEntryBuilder().label("People born between 1993 and 1998").value(0).build()
                    )
                    .subEntries([
                      new GemSunburstDataBuilder()
                          .entry(
                              new GemSunburstEntryBuilder().label( "Number of people born before 1995").value(0).build()
                          )
                          .build(),
                      new GemSunburstDataBuilder()
                          .entry(
                              new GemSunburstEntryBuilder().label("Number of people born between 1995 and 1997").value(0).build()
                          )
                          .build(),
                      new GemSunburstDataBuilder()
                          .entry(
                              new GemSunburstEntryBuilder().label("Number of people born after 1997").value(0).build()
                          )
                          .build(),
                    ])
                    .build(),
                new GemSunburstDataBuilder()
                    .entry(
                        new GemSunburstEntryBuilder().label("People born after 1998").value(0).build()
                    )
                    .subEntries([
                      new GemSunburstDataBuilder()
                          .entry(
                              new GemSunburstEntryBuilder().label( "Number of people born before 2001").value(0).build()
                          )
                          .build(),
                      new GemSunburstDataBuilder()
                          .entry(
                              new GemSunburstEntryBuilder().label("Number of people born between 2001 and 2002").value(0).build()
                          )
                          .build(),
                      new GemSunburstDataBuilder()
                          .entry(
                              new GemSunburstEntryBuilder().label("Number of people born after 2002").value(0).build()
                          )
                          .build(),
                    ])
                    .build()
            ])
            .build()

    for (const person of this.persons) {
      let birthday = Number(person?.getBirthday()?.toDateString().substring(11, 15));
      if (birthday < 1993) {
        const entry = data.getSubEntries(0).getEntry()
        entry.setValue(entry.getValue()+1);
        if (birthday < 1987) {
          const entry = data.getSubEntries(0).getSubEntries(0).getEntry()
          entry.setValue(entry.getValue()+1);
        } else if (birthday < 1992) {
          const entry = data.getSubEntries(0).getSubEntries(1).getEntry();
          entry.setValue(entry.getValue()+1);
        } else {
          const entry = data.getSubEntries(0).getSubEntries(2).getEntry()
          entry.setValue(entry.getValue()+1);
        }
      } else if (birthday >= 1993 && birthday <= 1998) {
        const entry = data.getSubEntries(1).getEntry()
        entry.setValue(entry.getValue()+1);
        if (birthday < 1995) {
          const entry = data.getSubEntries(1).getSubEntries(0).getEntry()
          entry.setValue(entry.getValue()+1);
        } else if (birthday < 1998) {
          const entry = data.getSubEntries(1).getSubEntries(1).getEntry()
          entry.setValue(entry.getValue()+1);
        } else {
          const entry = data.getSubEntries(1).getSubEntries(2).getEntry()
          entry.setValue(entry.getValue()+1);
        }
      } else {
        const entry = data.getSubEntries(2).getEntry()
        entry.setValue(entry.getValue()+1);
        if (birthday < 2001) {
          const entry = data.getSubEntries(2).getSubEntries(0).getEntry()
          entry.setValue(entry.getValue()+1);
        } else if (birthday < 2003) {
          const entry = data.getSubEntries(2).getSubEntries(1).getEntry()
          entry.setValue(entry.getValue()+1);
        } else {
          const entry = data.getSubEntries(2).getSubEntries(2).getEntry()
          entry.setValue(entry.getValue()+1);
        }
      }
    }
    if (StringifyForBuilder(data) === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = data;
    this.chartDataHash = StringifyForBuilder(this.chartData);
    return this.chartData;
  }

}
