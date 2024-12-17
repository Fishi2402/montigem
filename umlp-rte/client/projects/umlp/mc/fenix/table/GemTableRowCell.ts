/* (c) https://github.com/MontiCore/monticore */

import { Component, TemplateRef, ViewChild, ViewContainerRef } from '@angular/core';
import { config, GemTableRowCellComponent } from './build/gui/mc/fenix/table/GemTableRowCellComponent';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemTableRowCell extends GemTableRowCellComponent {
  @ViewChild('tableRowCellTemplate', { static: true }) tableRowCellTemplate: TemplateRef<any>;

  constructor(private viewContainerRef: ViewContainerRef) {
    super();
  }

  ngOnInit() {
    // Remove component's enclosing html tag
    this.viewContainerRef.createEmbeddedView(this.tableRowCellTemplate);
    this.viewContainerRef.element.nativeElement.remove();
  }
}