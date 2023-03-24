/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import de.se_rwth.commons.Joiners;

public class RTEPathUtil {
  private static final String RTE_PACKAGE_PATH = "umlp.backendrte";
  private static final String COMMAND_POSTFIX = "command";
  private static final String COMMON_POSTFIX = "common";
  private static final String GUI_POSTFIX = "gui";
  private static final String REQUEST_POSTFIX = "request";
  private static final String UTIL_POSTFIX = "util";
  private static final String SHELLAPI_POSTFIX = "shellapi";
  public static String getBackendRTEPath() {
    return RTE_PACKAGE_PATH;
  }

  public static String getBackendRTEPath(String... furtherPaths) {
    return Joiners.DOT.join(getBackendRTEPath(), String.join(".", furtherPaths));
  }

  public static String getBackendRTECommandPath() {
    return getBackendRTEPath(COMMAND_POSTFIX);
  }

  public static String getBackendRTECommandPath(String... furtherPaths) {
    return Joiners.DOT.join(getBackendRTECommandPath(), String.join(".", furtherPaths));
  }

  public static String getBackendRTECommonPath() {
    return getBackendRTEPath(COMMON_POSTFIX);
  }

  public static String getBackendRTECommonPath(String... furtherPaths) {
    return Joiners.DOT.join(getBackendRTECommonPath(), String.join(".", furtherPaths));
  }

  public static String getBackendRTEGUIPath() {
    return getBackendRTEPath(GUI_POSTFIX);
  }

  public static String getBackendRTEGUIPath(String... furtherPaths) {
    return Joiners.DOT.join(getBackendRTEGUIPath(), String.join(".", furtherPaths));
  }

  public static String getBackendRTERequestPath() {
    return getBackendRTEPath(REQUEST_POSTFIX);
  }

  public static String getBackendRTERequestPath(String... furtherPaths) {
    return Joiners.DOT.join(getBackendRTERequestPath(), String.join(".", furtherPaths));
  }

  public static String getBackendRTEUtilPath() {
    return getBackendRTEPath(UTIL_POSTFIX);
  }

  public static String getBackendRTEUtilPath(String... furtherPaths) {
    return Joiners.DOT.join(getBackendRTEUtilPath(), String.join(".", furtherPaths));
  }

  public static String getBackendRTEShellApiPath() {
    return getBackendRTEPath(SHELLAPI_POSTFIX);
  }

  public static String getBackendRTEShellApiPath(String... furtherPaths) {
    return Joiners.DOT.join(getBackendRTEShellApiPath(), String.join(".", furtherPaths));
  }
}
