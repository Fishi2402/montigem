/*
This file would be generated in frontend RTE, because the model is defined in frontend RTE,
meaning that this file has a wrong package here, and is used only to make code compilable,
BUT the rules for the generation (except for package) are correct.
Example, someone creates cd file in an app:
```
package my.ex;

classdiagramm A {
  class B {}
}
```
The result is java file:
```
package my.ex.a;

public class B {}
```
*/

package stanley.tmp.table.tabletypes;

public class TableColumn {
  protected String attrName;
  protected String colName;

  public TableColumn() {}

  // order of parameters is the same as in CD
  public TableColumn(String attrName, String colName) {
    this.attrName = attrName;
    this.colName = colName;
  }

  public void setAttrName(String attrName) {
    this.attrName = attrName;
  }

  public String getAttrName() {
    return attrName;
  }

  public void setColName(String colName) {
    this.colName = colName;
  }

  public String getColName() {
    return colName;
  }

}
