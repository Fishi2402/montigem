package de.montigem.gradle.specific.ocl;
import umlp.generator.ocl.OCL2JavaGeneratorTool;

import de.monticore.AmbiguityException;
import de.monticore.generating.templateengine.freemarker.MontiCoreFreeMarkerException;

public class OCL2JavaToolInvoker {
  public static void run(String[] args) {
    try {
      OCL2JavaGeneratorTool.gradleMain(args);
    } catch (final AmbiguityException | MontiCoreFreeMarkerException e) {
      RuntimeException newThrow = e;
      if (e.getCause() instanceof AmbiguityException) { // Freemarker adds special Freemarker Exceptions
        newThrow = new RuntimeException("This might be a gradle-setup problem. Are the MontiCore-Plugin and UMLP-Plugin loaded in the same project? \n" +
            "Gradle doesn't isolate plugins, so resources are found twice. \n\n", e);
      }
      throw newThrow;
    }
  }
}
