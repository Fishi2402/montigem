/* (c) https://github.com/MontiCore/monticore */

export class Col {
  attrName: string;

  colName: string;

  public constructor(attrName?: any, colName?: any) {
    if (((typeof attrName === 'string') || attrName === null) && ((typeof colName === 'string') || colName === null)) {
      let __args = arguments;
      if (this.attrName === undefined) {
        this.attrName = null;
      }
      if (this.colName === undefined) {
        this.colName = null;
      }
      this.attrName = attrName;
      this.colName = colName;
    } else if (((typeof attrName === 'string') || attrName === null) && colName === undefined) {
      let __args = arguments;
      if (this.attrName === undefined) {
        this.attrName = null;
      }
      if (this.colName === undefined) {
        this.colName = null;
      }
      this.attrName = attrName;
      if (!/* isEmpty */(attrName.length === 0)) {
        this.colName = attrName.substring(0, 1).toUpperCase() + attrName.substring(1);
      }
    } else throw new Error('invalid overload');
  }
}

Col["__class"] = "umlp.backendrte.gui.Col";



