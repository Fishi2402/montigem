package umlp.generator.gui.gentypescript.printer;

// TODO AGe: move to frontend-rte
public enum TSImportMap {
  // vis libs
  ARRANGE("arrange.src.lib", "@monticore/montigem-arrange"),
  BASIC("basic.src.lib", "@monticore/montigem-basic"),
  CHARTS("charts.src.lib", "@monticore/montigem-charts"),
  NAVIGATION("navigation", "@umlp/navigation"),
  INPUT("input.src.lib", "@monticore/montigem-input"),
  TABLE("table.src.lib", "@monticore/montigem-table");

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
