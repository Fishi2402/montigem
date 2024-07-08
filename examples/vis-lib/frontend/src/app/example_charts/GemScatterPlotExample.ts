/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemScatterPlotExampleComponent, config } from '@src/example_charts/GemScatterPlotExampleComponent';
import { GemScatterPlotData, GemScatterPlotAxis, GemScatterPlotSet, GemScatterPlotPoint, GemScatterPlotShape } from '@umlp/charts';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemScatterPlotExample extends GemScatterPlotExampleComponent {
  private chartData: GemScatterPlotData;
  private chartDataHash: string;

  public override getScatterplotData(): GemScatterPlotData {
    if (!this.persons) {
      return null;
    }

    let personsPerYear: Map<number, number> = new Map<number, number>();
    let friendsPerYear: Map<number, number> = new Map<number, number>();

    for (const person of this.persons) {
      //no idea why that works, I copied it from GemBarChartExample
      let yearOfBirth = Number(person?.getBirthday()?.toDateString().substring(11, 15));
      let numberOfFriends = person.getFriendsList().length;
      if (personsPerYear.has(yearOfBirth)) {
        personsPerYear.set(yearOfBirth, personsPerYear.get(yearOfBirth) + 1);
      } else {
        personsPerYear.set(yearOfBirth, 1);
      }
      if (friendsPerYear.has(yearOfBirth)) {
        friendsPerYear.set(yearOfBirth, friendsPerYear.get(yearOfBirth) + numberOfFriends);
      } else {
        friendsPerYear.set(yearOfBirth, numberOfFriends);
      }
    }

    let yearSet: GemScatterPlotSet = {
      label: "Geborene Personen pro Jahr",
      points: [],
    }

    personsPerYear.forEach((persons, year) => {
      yearSet.points.push({
        x: year,
        y: persons,
      });
    });

    let friendsSet: GemScatterPlotSet = {
      label: "Anzahl Freundschaften",
      points: [],
      color: "green",
      shape: GemScatterPlotShape.SQUARE,
    };

    friendsPerYear.forEach((friends, year) => {
      friendsSet.points.push({
        x: year,
        y: friends,
      });
    });

    let data: GemScatterPlotData = {
      sets: [yearSet, friendsSet],
    }

    if (JSON.stringify(data) === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = data;
    this.chartDataHash = JSON.stringify(this.chartData);
    return this.chartData;
  }

  public override getScatterplotXAxis() : GemScatterPlotAxis {
    return {
      label: "Geburtsjahr",
      stepWidth: 1,
    };
  }

  public override getScatterplotYAxis() : GemScatterPlotAxis {
    return {
      label: "Anzahl",
      stepWidth: 1,
    };
  }
}
