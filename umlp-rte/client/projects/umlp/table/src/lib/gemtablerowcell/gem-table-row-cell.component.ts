/* (c) https://github.com/MontiCore/monticore */

import {Component, Input, TemplateRef, ViewChild, ViewContainerRef} from "@angular/core";

@Component({
  selector: "gem-table-row-cell",
  templateUrl: "./gem-table-row-cell.component.html"
})
export class GemTableRowCell {
  @ViewChild('tableRowCellTemplate', { static: true }) tableRowCellTemplate: TemplateRef<any>;

  @Input() colSpan: number = 1;
  @Input() rowSpan: number = 1;
  @Input() content: TemplateRef<any>;

  constructor(private viewContainerRef: ViewContainerRef) {}

  ngOnInit() {
    // Remove component's enclosing html tag
    this.viewContainerRef.createEmbeddedView(this.tableRowCellTemplate);
    this.viewContainerRef.element.nativeElement.remove();
  }
}
