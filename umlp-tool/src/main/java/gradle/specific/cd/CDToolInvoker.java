package gradle.specific.cd;

import de.monticore.AmbiguityException;
import de.monticore.generating.templateengine.freemarker.MontiCoreFreeMarkerException;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPTool;

import java.util.Arrays;

public class CDToolInvoker {
  public static void run(String[] args){
    Log.info("Starting CDGeneratorTool: \n" +
        "\t  java -jar CDGeneratorTool.jar " + Arrays.toString(args), CDAction.class.getName());
    try {
      de.monticore.CDGeneratorTool.main(args);
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
