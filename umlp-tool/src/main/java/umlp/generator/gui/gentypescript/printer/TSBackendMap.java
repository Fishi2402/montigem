/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.printer;

import umlp.generator.domain.UMLPGeneratorConfig;

public class TSBackendMap {

  // Gem, NotificationScope, etc.
  private static final String BACKEND_RTE = "umlp.backendrte";
  private static final String COMMON_RTE = TSImportMap.COMMON.getTSPackage();
  private static final String JSWEET_RTE = "umlp-frontend-rte";

  public static boolean isBackendRTEPackage(String packageName) {
    return packageName.startsWith(BACKEND_RTE);
  }

  public static String getTSRTEPackage() {
    return UMLPGeneratorConfig.getInstance().isUseJsweet() ? JSWEET_RTE : COMMON_RTE;
  }
}
