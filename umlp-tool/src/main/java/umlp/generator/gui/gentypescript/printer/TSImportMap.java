package umlp.generator.gui.gentypescript.printer;

import umlp.generator.domain.UMLPGeneratorConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// TODO AGe: move to frontend-rte
public enum TSImportMap {
  INSTANCE;

  // key: javaPackage, value: tsPackage
  private final Map<String, String> importMap = new HashMap<>();
  public static final String JSWEET_RTE = "@umlp/commonj2ts";
  public static final String COMMON  = "@umlp/common";
  public static final String BACKEND_RTE  = "umlp.backendrte";

  TSImportMap() {
    add("mc.fenix.arrange", "@umlp/arrange");
    add("mc.fenix.basic", "@umlp/basic");
    add("mc.fenix.charts", "@umlp/charts");
    add("mc.fenix.common", COMMON);
    add("mc.fenix.navigation", "@umlp/navigation");
    add("mc.fenix.input", "@umlp/input");
    add("mc.fenix.table", "@umlp/table");
    add("mc.fenix.functiondisplay", "@umlp/functiondisplay");
    add("mc.fenix.networkgraph", "@umlp/networkgraph");
    add("arrange", "@umlp/arrange");
    add("basic", "@umlp/basic");
    add("charts", "@umlp/charts");
    add("common", "@umlp/common");
    add("navigation", "@umlp/navigation");
    add("input", "@umlp/input");
    add("table", "@umlp/table");
    add(BACKEND_RTE, "@umlp/common");
    add("montigem", "@montigem/login");
  }

  public void add(String javaPackage, String tsPackage) {
    importMap.put(javaPackage, tsPackage);
  }

  public Optional<String> getTSPackage(String javaPackage) {
    for (String key : importMap.keySet()) {
      if (javaPackage.startsWith(key)) {
        if (key.equals(BACKEND_RTE) && UMLPGeneratorConfig.getInstance().isUseJsweet()) {
          return Optional.of(JSWEET_RTE);
        }
        return Optional.of(importMap.get(key));
      }
    }
    return Optional.empty();
  }

  public boolean isTSPackageImport(String javaPackage)  {
    for (String key : importMap.keySet()) {
      if (javaPackage.startsWith(key)) {
        return false;
      }
    }
    return true;
  }

  public void clear() {
    importMap.clear();
  }

}