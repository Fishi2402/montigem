/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle.specific;

import umlp.generator.gui.JSweetConfigGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CD2JSweetAdapterAction extends Model2JSweetAdapterAction<Model2JSweetAdapterAction.Params> {

  /**
   * Generates configuration files required for the JSweet transpilation.
   */
  protected void generateJSweetConfig() throws IOException {
    JSweetConfigGenerator generator = getGenerator();
    Set<String> srcDirs = getParameters().getSrcDirs()
        .getFiles()
        .stream()
        .map(File::getAbsolutePath)
        .collect(Collectors.toSet());
    generator.generate(
        getParameters().getJSweetAdapterDir().get().getAsFile().getAbsolutePath(),
        srcDirs
    );
  }

}
