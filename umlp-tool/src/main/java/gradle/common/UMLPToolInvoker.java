package gradle.common;

import de.monticore.AmbiguityException;
import de.monticore.generating.templateengine.freemarker.MontiCoreFreeMarkerException;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPTool;

import java.util.Arrays;

public class UMLPToolInvoker {
  public static void run(String[] args){
    Log.info("Starting UMLPTool: \n" +
        "\t  java -jar UMLPTool.jar " + Arrays.toString(args), UMLPAction.class.getName());
    try {
      UMLPTool.main(args);
    }catch (final AmbiguityException | MontiCoreFreeMarkerException e){
      RuntimeException newThrow = e;
      if(e.getCause() instanceof AmbiguityException){ // Freemarker adds special Freemarker Exceptions
        newThrow = new RuntimeException("This might be a gradle-setup problem. Are the MontiCore-Plugin and UMLP-Plugin loaded in the same project? \n" +
            "Gradle doesn't isolate plugins, so resources are found twice. \n\n", e);
      }
      throw newThrow;
    }
  }
}
