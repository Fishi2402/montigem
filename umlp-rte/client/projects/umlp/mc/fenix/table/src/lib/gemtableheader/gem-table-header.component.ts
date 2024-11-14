/* (c) https://github.com/MontiCore/monticore */

import {Component, Input, OnInit, TemplateRef, ViewChild, ViewContainerRef} from "@angular/core";

@Component({
  selector: "gem-table-header",
  templateUrl: "./gem-table-header.component.html",
})
export class GemTableHeader implements OnInit {
  @ViewChild('tableHeaderTemplate', { static: true }) tableHeaderTemplate: TemplateRef<any>;

  protected _cols: TemplateRef<any>[];
  protected _cells: TemplateRef<any>[];

  public get cols(): TemplateRef<any>[] {
    return this._cols;
  }

  public get cells(): TemplateRef<any>[] {
    return this._cells;
  }

  @Input() set cols(value: TemplateRef<any>[]) {
    if (value == null) {
      return;
    }
    this._cols = value;
    this.update();
  }

  @Input() set cells(value: TemplateRef<any>[]) {
    if (value == null) {
      return;
    }
    this._cells = value;
    this.update();
  }

  public content: TemplateRef<any>[] = [];

  constructor(private viewContainerRef: ViewContainerRef) {}

  ngOnInit() {
    // Remove component's enclosing html tag
    this.viewContainerRef.createEmbeddedView(this.tableHeaderTemplate);
    this.viewContainerRef.element.nativeElement.remove();
  }

  private update(): void {
    if (this._cells != null) {
      this.content = this._cells;
    }
    else if (this._cols != null) {
      this.content = this._cols;
    }
  }
}
