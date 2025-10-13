/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle.specific;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import umlp.generator.gui.JSweetConfigGenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class UMLP2JSweetAdapterAction extends Model2JSweetAdapterAction<UMLP2JSweetAdapterAction.UMLPParams> {

  /**
   * Generates configuration files required for the JSweet transpilation.
   */
  protected void generateJSweetConfig() throws IOException {
    JSweetConfigGenerator generator = getGenerator();
    List<String> srcDirs = getParameters().getSrcDirs()
        .getFiles()
        .stream()
        .map(File::getAbsolutePath)
        .collect(Collectors.toList());
    generator.generate(
        getParameters().getJSweetAdapterDir().get().getAsFile().getAbsolutePath(),
        getParameters().getDomain().get().get(0), true,
        srcDirs
    );
  }

  interface UMLPParams extends Params {
    @Input
    ListProperty<String> getDomain();
  }
}
