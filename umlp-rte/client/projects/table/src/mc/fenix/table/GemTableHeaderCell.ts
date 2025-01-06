/* (c) https://github.com/MontiCore/monticore */

import { Component, TemplateRef, ViewChild, ViewContainerRef } from '@angular/core';
import { config, GemTableHeaderCellComponent } from '../../../../build/gui/mc/fenix/table/GemTableHeaderCellComponent';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemTableHeaderCell extends GemTableHeaderCellComponent {
  @ViewChild('tableHeaderCellTemplate', { static: true }) tableHeaderCellTemplate: TemplateRef<any>;

  constructor(private viewContainerRef: ViewContainerRef) {
    super();
  }

  ngOnInit() {
    // Remove component's enclosing html tag
    this.viewContainerRef.createEmbeddedView(this.tableHeaderCellTemplate);
    this.viewContainerRef.element.nativeElement.remove();
  }
}