/* (c) https://github.com/MontiCore/monticore */

package umlp;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.generating.templateengine.TemplateStringHookPoint;
import de.se_rwth.commons.logging.Log;
import umlp.generator.util.ComposedHookPoint;

import java.io.IOException;

public class ModuleController {
  protected final GlobalExtensionManagement glex;

  public ModuleController(GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  public void addModuleToHookpoint(String hookpointName, String template){
    try {
      glex.addAfterTemplate(hookpointName, new ComposedHookPoint(
          new TemplateStringHookPoint("${tc.warn(\"[" + hookpointName + "] Executing module config template " + template + "\")}"),
          new TemplateHookPoint(template)
      ));
    } catch (IOException e) {
      Log.error("Error in addModuleToHookpoint for " + hookpointName + ", " + template, e);
    }
  }
}
