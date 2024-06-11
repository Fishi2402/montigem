/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { HierarchicalListComponent, config } from '@src/person/HierarchicalListComponent';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class HierarchicalList extends HierarchicalListComponent {
  protected override initList(): void {
    this._list = [];
    super.initList();
  }
}
