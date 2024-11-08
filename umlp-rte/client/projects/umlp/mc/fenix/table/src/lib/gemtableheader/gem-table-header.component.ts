/* (c) https://github.com/MontiCore/monticore */

import {Component, Input, OnInit, TemplateRef, ViewChild, ViewContainerRef} from "@angular/core";

@Component({
  selector: "gem-table-header",
  templateUrl: "./gem-table-header.component.html",
})
export class GemTableHeader implements OnInit {
  @ViewChild('tableHeaderTemplate', { static: true }) tableHeaderTemplate: TemplateRef<any>;

  @Input() cols: string[];

  constructor(private viewContainerRef: ViewContainerRef) {}

  ngOnInit() {
    // Remove component's enclosing html tag
    this.viewContainerRef.createEmbeddedView(this.tableHeaderTemplate);
    this.viewContainerRef.element.nativeElement.remove();
  }
}
