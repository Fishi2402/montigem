/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemVennDiagramExampleComponent, config } from '@src/example_charts/GemVennDiagramExampleComponent';
import { GemVennDiagramData, GemVennDiagramEntry } from '@umlp/charts';
import { Person } from '@src/domain/Person';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: 'GemVennChartExample',
  templateUrl: config.templateUrl,
})
export class GemVennDiagramExample extends GemVennDiagramExampleComponent {
  private chartData: GemVennDiagramData;
  private chartDataHash: string;

  public override getVenndiagrammData(): GemVennDiagramData {
    if (!this.persons) {
      return null;
    }
    let entries: GemVennDiagramEntry[] = [];
    let mapOfriends = new Map<string, Person[]>;

    for (const person of this.persons) {
      let node = {
        label: person.getName(),
        sets: [`${person.getName()}`],
        size: person.getFriendsList().length
      };
      mapOfriends.set(person.getName(), person.getFriendsList());
      entries.push(node);

    }

    mapOfriends.forEach((friends, person) => {
      for (let friend of friends) {
        if (mapOfriends.has(friend.getName())) {
          let node = {
            sets: [`${person}`, `${friend.getName()}`],
            size: 1
          };
          entries.push(node);
        }
      }
    })
    let data: GemVennDiagramData = {
      entries: entries,
    }

    if (JSON.stringify(data) === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = data;
    this.chartDataHash = JSON.stringify(this.chartData);
    return this.chartData;
  }
}
