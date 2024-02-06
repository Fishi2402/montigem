/* (c) https://github.com/MontiCore/monticore */

import { TableColumn } from '@umlp/table';
import { Component } from '@angular/core';
import { PersonOverviewComponent, config } from '@src/gui/person/PersonOverviewComponent';

@Component({
  standalone: true,
  imports: config.imports,
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class PersonOverview extends PersonOverviewComponent {

  public override init() {
    this.gemTable5Columns = [
      new TableColumn("_personalnummer", "Personalnummer"),
      new TableColumn("_kuerzel", "Kürzel"),
      new TableColumn("_nachname", "Name"),
      new TableColumn("_vorname", "Vorname"),
      new TableColumn("_beschBeginn", "Anstellungsbeginn"),
      new TableColumn("_beschEnde", "Anstellungsende")
    ];
  }

}


