/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle.specific;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import umlp.generator.gui.JSweetConfigGenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class GUI2JSweetAdapterAction extends Model2JSweetAdapterAction<GUI2JSweetAdapterAction.GUIParams> {

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
    if (getParameters().getDomain().isPresent() && !getParameters().getDomain().get().isEmpty()) {
      generator.generate(
          getParameters().getJSweetAdapterDir().get().getAsFile().getAbsolutePath(),
          getParameters().getDomain().get().get(0), false,
          srcDirs
      );
    }
    else {
      generator.generate(
          getParameters().getJSweetAdapterDir().get().getAsFile().getAbsolutePath(),
          srcDirs
      );
    }
  }

  interface GUIParams extends Params {
    @Input
    @Optional
    ListProperty<String> getDomain();
  }
}
