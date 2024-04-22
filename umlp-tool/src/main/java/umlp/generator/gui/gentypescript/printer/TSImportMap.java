package umlp.generator.gui.gentypescript.printer;

import umlp.generator.domain.UMLPGeneratorConfig;

import java.util.Optional;

// TODO AGe: move to frontend-rte
public enum TSImportMap {
  // vis libs
  ARRANGE("mc.fenix.arrange", "@umlp/arrange"),
  BASIC("mc.fenix.basic", "@umlp/basic"),
  CHARTS("mc.fenix.charts", "@umlp/charts"),
  COMMON("mc.fenix.common", "@umlp/common"),
  NAVIGATION("mc.fenix.navigation", "@umlp/navigation"),
  INPUT("mc.fenix.input", "@umlp/input"),
  TABLE("mc.fenix.table", "@umlp/table"),
  NETWORKGRAPH("mc.fenix.networkgraph", "@umlp/networkgraph"),
  ARRANGE_DEPRECATED("arrange", "@umlp/arrange"),
  BASIC_DEPRECATED("basic", "@umlp/basic"),
  CHARTS_DEPRECATED("charts", "@umlp/charts"),
  COMMON_DEPRECATED("common", "@umlp/common"),
  NAVIGATION_DEPRECATED("navigation", "@umlp/navigation"),
  INPUT_DEPRECATED("input", "@umlp/input"),
  TABLE_DEPRECATED("table", "@umlp/table"),
  BACKEND_RTE("umlp.backendrte", "@umlp/common");

  private static final String JSWEET_RTE = "@umlp/commonj2ts";

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

  public static Optional<String> getTSPackage(String javaPackage) {
    for (TSImportMap type : TSImportMap.values()) {
      if (javaPackage.startsWith(type.getJavaPackage())) {
        if (type == BACKEND_RTE && UMLPGeneratorConfig.getInstance().isUseJsweet()) {
          return Optional.of(JSWEET_RTE);
        }
        return Optional.of(type.getTSPackage());
      }
    }
    return Optional.empty();
  }

}
