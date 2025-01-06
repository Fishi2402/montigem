/* (c) https://github.com/MontiCore/monticore */

import { Component, Input, TemplateRef, ViewChild, ViewContainerRef } from '@angular/core';
import { config, GemTableRowComponent } from '../../../../build/gui/mc/fenix/table/GemTableRowComponent';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemTableRow extends GemTableRowComponent {
  @ViewChild('tableRowTemplate', { static: true }) tableRowTemplate: TemplateRef<any>;

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

  constructor(private viewContainerRef: ViewContainerRef) {
    super();
  }

  ngOnInit() {
    // Remove component's enclosing html tag
    this.viewContainerRef.createEmbeddedView(this.tableRowTemplate);
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