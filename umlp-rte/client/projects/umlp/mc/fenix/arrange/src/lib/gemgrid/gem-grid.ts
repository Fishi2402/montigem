import { Component, HostBinding, Input, TemplateRef, ViewChild } from "@angular/core";

@Component({
  selector: "gem-grid",
  templateUrl: "./gem-grid.html",
  styleUrls: ["./gem-grid.scss"],
})
export class GemGrid {
  @HostBinding('style.height')
  protected _height: string = '100%';

  @HostBinding('style.width')
  protected _width: string = '100%';

  @HostBinding('style.row-gap')
  protected _rowGap: string = "0";

  @HostBinding('style.column-gap')
  protected _colGap: string = "0";

  @HostBinding('style.grid-template-rows')
  protected _gridTemplateRows: string = "1fr";
  protected _maxRows: number = 1;

  @HostBinding('style.grid-template-columns')
  protected _gridTemplateCols: string = "1fr";
  protected _maxCols: number = 1;

  @ViewChild('_gemGridEmpty', { static: true })
  protected emptyTpl: TemplateRef<any>;

  public _x: string[] = [];
  public _y: string[] = [];

  public _rows: TemplateRef<any>[][] = [];
  public _cols: TemplateRef<any>[][] = [];

  public get rows(): TemplateRef<any>[][] {
    return this._rows;
  }

  @Input()
  public set rows(val: TemplateRef<any>[][]) {
    if (val == null) {
      return;
    }
    let max: number = 1;
    for (const v of val) {
      if (v.length > max) {
        max = v.length;
      }
    }
    this._maxCols = max;
    this._maxRows = val.length;
    this.updateGridTemplateCols();

    const res: TemplateRef<any>[][] = [];
    for (let row of val) {
      const r: TemplateRef<any>[] = [];
      for (let col of row) {
        r.push(col);
      }
      if (row.length < this._maxCols) {
        for (let i = row.length; i < this._maxCols; ++i) {
          r.push(this.emptyTpl);
        }
      }
      res.push(r);
    }
    this._rows = res;
  }

  public get cols(): TemplateRef<any>[][] {
    return this._cols;
  }

  @Input()
  public set cols(val: TemplateRef<any>[][]) {
    if (val == null) {
      return;
    }
    let max: number = 1;
    for (const v of val) {
      if (v.length > max) {
        max = v.length;
      }
    }
    this._maxRows = max;
    this._maxCols = val.length;
    this.updateGridTemplateRows();

    const res: TemplateRef<any>[][] = [];
    for (let col of val) {
      const c: TemplateRef<any>[] = [];
      for (let row of col) {
        c.push(row);
      }
      if (col.length < this._maxRows) {
        for (let i = col.length; i < this._maxRows; ++i) {
          c.push(this.emptyTpl);
        }
      }
      res.push(c);
    }
    // transpose
    this._cols = res[0].map((_, i) => res.map(row => row[i]));
  }

  @Input()
  public set x(val: string[]) {
    this._x = val;
    console.error(this._x)
    this.updateGridTemplateCols();
  }

  @Input()
  public set y(val: string[]) {
    this._y = val;
    this.updateGridTemplateRows();
  }


  protected updateGridTemplateCols() {
    if (this._x.length > 0) {
      this._gridTemplateCols = this._x.join(" ");
    } else {
      this._gridTemplateCols = `repeat(${this._maxCols}, 1fr)`;
      this._gridTemplateRows = `repeat(${this._maxRows}, 1fr)`;
    }
  }

  protected updateGridTemplateRows() {
    if (this._y.length > 0) {
      this._gridTemplateRows = this._y.join(" ");
    } else {
      this._gridTemplateRows = `repeat(${this._maxRows}, 1fr)`;
      this._gridTemplateCols = `repeat(${this._maxCols}, 1fr)`;
    }
  }

  public get height(): string {
    return this._height;
  }

  @Input()
  public set height(val: string) {
    this._height = val;
  }

  public get width(): string {
    return this._width;
  }

  @Input()
  public set width(val: string) {
    this._width = val;
  }

  public get rowGap(): string {
    return this._rowGap;
  }

  @Input()
  public set rowGap(val: string) {
    this._rowGap = val;
  }

  public get colGap(): string {
    return this._colGap;
  }

  @Input()
  public set colGap(val: string) {
    this._colGap = val;
  }

}
