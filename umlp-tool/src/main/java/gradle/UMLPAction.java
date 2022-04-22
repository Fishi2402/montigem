package gradle;

import org.gradle.workers.WorkAction;
import umlp.UMLPTool;

public abstract class UMLPAction implements WorkAction<ArgActionParameter> {
  @Override
  public void execute() {
    String[] arrayArgs = this.getParameters().getArgs().get().toArray(new String[0]);

        /* Log.warn("Starting UMLPTool: \n" +
            "\t  java -jar UMLPTool.jar " + Arrays.toString(arrayArgs)); */
    UMLPTool.main(arrayArgs);
  }
}
