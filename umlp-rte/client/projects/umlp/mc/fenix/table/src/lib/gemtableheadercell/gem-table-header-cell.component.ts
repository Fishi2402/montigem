/* (c) https://github.com/MontiCore/monticore */

import {Component, Input, TemplateRef, ViewChild, ViewContainerRef} from "@angular/core";

@Component({
  selector: "gem-table-header-cell",
  templateUrl: "./gem-table-header-cell.component.html"
})
export class GemTableHeaderCell {
  @ViewChild('tableHeaderCellTemplate', { static: true }) tableHeaderCellTemplate: TemplateRef<any>;

  @Input() colSpan: number = 1;
  @Input() rowSpan: number = 1;
  @Input() content: TemplateRef<any>;

  constructor(private viewContainerRef: ViewContainerRef) {}

  ngOnInit() {
    // Remove component's enclosing html tag
    this.viewContainerRef.createEmbeddedView(this.tableHeaderCellTemplate);
    this.viewContainerRef.element.nativeElement.remove();
  }
}
