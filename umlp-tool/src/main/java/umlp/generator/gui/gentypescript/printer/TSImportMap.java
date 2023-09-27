package umlp.generator.gui.gentypescript.printer;

// TODO AGe: move to frontend-rte
public enum TSImportMap {
  // vis libs
  ARRANGE("arrange.src.lib", "@monticore/montigem-arrange"),
  BASIC("basic.src.lib", "@monticore/montigem-basic"),
  CHARTS("charts.src.lib", "@monticore/montigem-charts"),
  INPUT("input.src.lib", "@monticore/montigem-input"),
  TABLE("table.src.lib", "@monticore/montigem-table"),

  // Gem, NotificationScope, etc.
  BACKEND_RTE("umlp.backendrte", "umlp-frontend-rte");

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
