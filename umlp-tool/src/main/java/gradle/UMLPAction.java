/* (c) https://github.com/MontiCore/monticore */
package gradle;

import de.monticore.AmbiguityException;
import de.monticore.generating.templateengine.freemarker.MontiCoreFreeMarkerException;
import de.se_rwth.commons.logging.Log;
import org.gradle.workers.WorkAction;
import umlp.UMLPTool;

import java.util.Arrays;

public abstract class UMLPAction implements WorkAction<ArgActionParameter> {
  @Override
  public void execute() {
    String[] arrayArgs = this.getParameters().getArgs().get().toArray(new String[0]);

    Log.info("Starting UMLPTool: \n" +
            "\t  java -jar UMLPTool.jar " + Arrays.toString(arrayArgs), getClass().toString());
    try {
      UMLPTool.main(arrayArgs);
    }catch (final RuntimeException e){
      RuntimeException newThrow = e;
      if(e.getCause() instanceof AmbiguityException){ // Freemarker adds special Freemarker Exceptions
        newThrow = new RuntimeException("This might be a gradle-setup problem. Are the MontiCore-Plugin and UMLP-Plugin loaded in the same project? \n" +
            "Gradle doesn't isolate plugins, so resources are found twice. \n\n", e);
      }
      throw newThrow;
    }
  }
}
