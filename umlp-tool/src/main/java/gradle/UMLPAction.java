package gradle;

import de.monticore.AmbiguityException;
import org.gradle.workers.WorkAction;
import umlp.UMLPTool;

public abstract class UMLPAction implements WorkAction<ArgActionParameter> {
  @Override
  public void execute() {
    String[] arrayArgs = this.getParameters().getArgs().get().toArray(new String[0]);

        /* Log.warn("Starting UMLPTool: \n" +
            "\t  java -jar UMLPTool.jar " + Arrays.toString(arrayArgs)); */
    try {
      UMLPTool.main(arrayArgs);
    }catch (AmbiguityException e){
      AmbiguityException wrappedE = new AmbiguityException("This might be a gradle-setup problem. Are the MontiCore-Plugin and UMLP-Plugin loaded in the same project? \n" +
          "Gradle doesn't isolate plugins, so resources are found twice. \n\n" + e.getMessage());
      wrappedE.setStackTrace(e.getStackTrace());
      throw wrappedE;
    }
  }
}
