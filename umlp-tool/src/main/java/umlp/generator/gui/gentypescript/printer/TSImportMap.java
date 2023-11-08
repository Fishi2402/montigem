package umlp.generator.gui.gentypescript.printer;

// TODO AGe: move to frontend-rte
public enum TSImportMap {
  // vis libs
  ARRANGE("arrange", "@umlp/arrange"),
  BASIC("basic", "@umlp/basic"),
  CHARTS("charts", "@umlp/charts"),
  COMMON("common", "@umlp/common"),
  NAVIGATION("navigation", "@umlp/navigation"),
  INPUT("input", "@umlp/input"),
  TABLE("table", "@umlp/table");

  private final String tsPackage;
  private final String javaPackage;

  TSImportMap(String javaPackage, String tsPackage) {
    this.javaPackage = javaPackage;
    this.tsPackage = tsPackage;
  }

  public String getJavaPackage() {
    return javaPackage;
  }

  public String getTSPackage() {
    return tsPackage;
  }

}
