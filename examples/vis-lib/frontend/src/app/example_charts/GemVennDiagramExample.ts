/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {
  config,
  GemVennDiagramExampleComponent
} from 'example_charts/GemVennDiagramExampleComponent';
import {
  GemVennDiagramData,
  GemVennDiagramDataBuilder,
  GemVennDiagramEntry,
  GemVennDiagramEntryBuilder
} from '@umlp/charts';
import {Person} from 'domain/Person';
import {stringifyWithoutRealBuilder} from "StringifyWithoutRealBuilder";

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
      let node = new GemVennDiagramEntryBuilder()
          .label(person.getName())
          .sets([`${person.getName()}`])
          .size(person.getFriendsList().length)
          .build();
      mapOfriends.set(person.getName(), person.getFriendsList());
      entries.push(node);

    }

    mapOfriends.forEach((friends, person) => {
      for (let friend of friends) {
        if (mapOfriends.has(friend.getName())) {
          let node = new GemVennDiagramEntryBuilder()
              .sets([`${person}`, `${friend.getName()}`])
              .size(1)
              .build();
          entries.push(node);
        }
      }
    });
    let data = new GemVennDiagramDataBuilder()
        .entries(entries).build();
    let dataString = stringifyWithoutRealBuilder(data);
    if (dataString === this.chartDataHash && this.chartData) {
      return this.chartData;
    }
    this.chartData = data;
    this.chartDataHash = stringifyWithoutRealBuilder(this.chartData);
    return this.chartData;
  }
}
