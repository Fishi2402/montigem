// GEN-P: translated from Java

import { ChangeDetectorRef, Component, EventEmitter, Input, Output } from "@angular/core";
import { TableColumn } from "../table-types";

@Component({
  selector: "gem-table",
  templateUrl: "./gem-table.component.html",
  styleUrls: ["./gem-table.component.scss"]
})
export class GemTable<T> {

  protected _rows: T[] = [];
  protected _columns: TableColumn[] = [];
  canDelete: boolean = false;

  constructor(protected cdRef: ChangeDetectorRef) {}

  @Output()
  deleteRow: EventEmitter<T> = new EventEmitter();

  @Input()
  set rows(value: T[]) {
    this._rows = value;
    this.updateState();
  }

  get rows(): T[] {
    return this._rows;
  }

  @Input()
  set columns(value: TableColumn[]) {
    this._columns = value;
    this.updateState();
  }

  get columns(): TableColumn[] {
    return this._columns;
  }

  ngAfterViewInit() {
    this.canDelete = this.deleteRow.observed;
    this.cdRef.detectChanges();
  }

  protected updateState(): void {
    if (!this._columns) {
      this._columns = [];
    }
    if (!this._columns.length) {
      if (this._rows && this._rows.length) {
        const tplRow = this.rows[0];
        const attrs = Object.keys(tplRow);
        for (const attr of attrs) {
          if (!Array.isArray(tplRow[attr])) {
            this._columns.push(new TableColumn(attr, GemTable.capitalizeFirstLetter(attr)));
          }
        }
      }
    } else {
      for (const col of this._columns) {
        if (col.getColName() == null) {
          col.setColName(GemTable.capitalizeFirstLetter(col.getAttrName()));
        }
      }
      this._columns = this._columns.slice();
    }
  }

  protected static capitalizeFirstLetter(str: string) {
    return str.charAt(0).toUpperCase() + str.slice(1);
  }

}