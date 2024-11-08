/* (c) https://github.com/MontiCore/monticore */

import {Component, Input, TemplateRef, ViewChild, ViewContainerRef} from "@angular/core";

@Component({
  selector: "gem-table-row",
  templateUrl: "./gem-table-row.component.html"
})
export class GemTableRow {
  @ViewChild('tableRowTemplate', { static: true }) tableRowTemplate: TemplateRef<any>;

  @Input() cells: TemplateRef<any>[];

  constructor(private viewContainerRef: ViewContainerRef) {}

  ngOnInit() {
    // Remove component's enclosing html tag
    this.viewContainerRef.createEmbeddedView(this.tableRowTemplate);
    this.viewContainerRef.element.nativeElement.remove();
  }
}
