/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.printer;

import umlp.generator.domain.UMLPGeneratorConfig;

@Deprecated
public class TSBackendMap {

  public static boolean isBackendRTEPackage(String packageName) {
    return packageName.startsWith(TSImportMap.BACKEND_RTE);
  }

  public static String getTSRTEPackage() {
    return UMLPGeneratorConfig.getInstance().isUseJsweet() ? TSImportMap.JSWEET_RTE : TSImportMap.COMMON;
  }
}
