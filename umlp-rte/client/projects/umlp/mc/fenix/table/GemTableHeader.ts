/* (c) https://github.com/MontiCore/monticore */

import { Component, Input, TemplateRef, ViewChild, ViewContainerRef } from '@angular/core';
import { config, GemTableHeaderComponent } from './build/gui/mc/fenix/table/GemTableHeaderComponent';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemTableHeader extends GemTableHeaderComponent {
  @ViewChild('tableHeaderTemplate', { static: true })
  tableHeaderTemplate: TemplateRef<any>;

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

  constructor(private viewContainerRef: ViewContainerRef) {
    super();
  }

  ngOnInit() {
    // Remove component's enclosing html tag
    this.viewContainerRef.createEmbeddedView(this.tableHeaderTemplate);
    this.viewContainerRef.element.nativeElement.remove();
  }

}