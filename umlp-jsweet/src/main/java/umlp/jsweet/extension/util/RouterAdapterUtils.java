/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.util;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.util.Util;

import javax.lang.model.util.Types;


public class RouterAdapterUtils extends UMLPAdapterUtils {
  public static final String APP_ROUTER = "AppRouter";
  
  public static final String ROUTER = "Router";
  
  public static final String ROUTER_FULL_NAME = "app." + ROUTER;
  
  public static final String ROUTER_VAR = "router";
  
  public static final String ROUTER_GLOBAL_VAR = "_router";
  
  public static final String APP_ROUTER_NAVIGATE_PREFIX = "navigateTo";
  
  public static final String NAVIGATE = "navigate";
  
  public RouterAdapterUtils(PrinterAdapter adapter, Types types, JSweetContext context, Util util) {
    super(adapter, types, context, util);
  }
}
