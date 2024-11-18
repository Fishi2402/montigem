/* (c) https://github.com/MontiCore/monticore */

import {Component, Input, OnInit, TemplateRef, ViewChild, ViewContainerRef} from "@angular/core";

@Component({
  selector: "gem-table-header",
  templateUrl: "./gem-table-header.component.html",
})
export class GemTableHeader implements OnInit {
  @ViewChild('tableHeaderTemplate', { static: true }) tableHeaderTemplate: TemplateRef<any>;

  protected _cols: string[];
  protected _cells: TemplateRef<any>[];

  public get cols(): string[] {
    return this._cols;
  }

  public get cells(): TemplateRef<any>[] {
    return this._cells;
  }

  @Input() set cols(value: string[]) {
    if (value == null) {
      return;
    }
    this._cols = value;
  }

  @Input() set cells(value: TemplateRef<any>[]) {
    if (value == null) {
      return;
    }
    this._cells = value;
  }

  constructor(private viewContainerRef: ViewContainerRef) {}

  ngOnInit() {
    // Remove component's enclosing html tag
    this.viewContainerRef.createEmbeddedView(this.tableHeaderTemplate);
    this.viewContainerRef.element.nativeElement.remove();
  }

}
