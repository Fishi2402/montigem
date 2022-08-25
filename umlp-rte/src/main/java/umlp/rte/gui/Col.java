package umlp.rte.gui;

public class Col {
  String attrName;
  String colName;
  public Col(String attrName) {
    this.attrName = attrName;
    if (!attrName.isEmpty()) {
      colName = attrName.substring(0, 1).toUpperCase() + attrName.substring(1);
    }
  }

  public Col(String attrName, String colName) {
    this.attrName = attrName;
    this.colName = colName;
  }

}