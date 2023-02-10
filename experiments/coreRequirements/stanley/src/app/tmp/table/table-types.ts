/*
Does not have to be generated, translated from Java file

This file would be in frontend RTE, because the model is defined in frontend RTE,
meaning that this file has a wrong location here, and is used only to make code compilable,
BUT the rules for the translation are correct.
```
*/

export class TableColumn {
  protected attrName: string;
  protected colName: string;

  // order of parameters is the same as in CD, all optional
  constructor(attrName?: string, colName?: string) {
    this.attrName = attrName;
    this.colName = colName;
  }

  public setAttrName(v: string): void {
    this.attrName = v;
  }

  public getAttrName(): string {
    return this.attrName;
  }

  public setColName(v: string): void {
    this.colName = v;
  }

  public getColName(): string {
    return this.colName;
  }

}
