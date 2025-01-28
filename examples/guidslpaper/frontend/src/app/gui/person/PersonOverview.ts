/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { PersonOverviewComponent, config } from 'gui/person/PersonOverviewComponent';

@Component({
  standalone: true,
  imports: config.imports,
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class PersonOverview extends PersonOverviewComponent {

  public override formatDate(date: Date): string {
    return new Date(date).toLocaleDateString("de-DE", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
    });
  }

}


