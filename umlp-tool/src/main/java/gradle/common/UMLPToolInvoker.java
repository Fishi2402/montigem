package gradle.common;

import de.monticore.AmbiguityException;
import de.monticore.generating.templateengine.freemarker.MontiCoreFreeMarkerException;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.gradle.api.GradleException;
import umlp.UMLPTool;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UMLPToolInvoker {
  public static void run(String[] args){
    Log.info("Starting UMLPTool: \n" +
        "\t  java -jar UMLPTool.jar " + Arrays.toString(args), UMLPAction.class.getName());
    Log.enableFailQuick(false); // Just to be sure: Disable fail-quick here
    try {
      UMLPTool.mainRet(args);
      List<Finding> errors = getErrors();
      if (!errors.isEmpty()) {
        // TODO: Add optional Stacktrace to findings
        throw new GradleException(errors.stream().map(Finding::toString).collect(Collectors.joining(System.lineSeparator())));
      }
    } catch (final AmbiguityException | MontiCoreFreeMarkerException e){
      RuntimeException newThrow = e;
      if(e.getCause() instanceof AmbiguityException){ // Freemarker adds special Freemarker Exceptions
        newThrow = new RuntimeException("This might be a gradle-setup problem. Are the MontiCore-Plugin and UMLP-Plugin loaded in the same project? \n" +
            "Gradle doesn't isolate plugins, so resources are found twice. \n\n", e);
      }
      throw newThrow;
    } finally {
      UMLPTool.cleanUpAfterMemoryLeakingSingletons();
    }
  }

  static List<Finding> getErrors() {
    return Log.getFindings().stream().filter(Finding::isError).collect(Collectors.toList());
  }
}
