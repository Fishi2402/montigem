/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle.specific;

import de.se_rwth.commons.logging.Log;
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
    if(getParameters().getUseModuleGUIAdapter().get()) {
      generator.generate(
          getParameters().getJSweetAdapterDir().get().getAsFile().getAbsolutePath(),
          false,
          srcDirs
      );
    } else if (getParameters().getDomain().isPresent() && !getParameters().getDomain().get().isEmpty()) {
      generator.generate(
          getParameters().getJSweetAdapterDir().get().getAsFile().getAbsolutePath(),
          getParameters().getDomain().get().get(0), false,
          srcDirs
      );
    } else if(!getParameters().getDomain().isPresent() || getParameters().getDomain().get().isEmpty()) {
      generator.generate(
          getParameters().getJSweetAdapterDir().get().getAsFile().getAbsolutePath(),
          true,
          srcDirs
      );
    } else {
      Log.error("Could not derive which GUI2JSweetAdapter to use from the given Gradle configuration." +
          " You can either specify a Domain model or set useModuleGUIAdapter to true.");
    }
  }

  interface GUIParams extends Params {
    @Input
    @Optional
    ListProperty<String> getDomain();

    @Input
    Property<Boolean> getUseModuleGUIAdapter();
  }
}
