/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle.specific;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import umlp.generator.gui.JSweetConfigGenerator;

import java.io.IOException;

public abstract class GUI2JSweetAdapterAction extends Model2JSweetAdapterAction<GUI2JSweetAdapterAction.GUIParams> {

  /**
   * Generates configuration files required for the JSweet transpilation.
   */
  protected void generateJSweetConfig() throws IOException {
    JSweetConfigGenerator generator = getGenerator();
    if (getParameters().getDomain().isPresent()) {
      generator.generate(getParameters().getDomain().get(), false);
    }
    else {
      generator.generate();
    }
  }

  interface GUIParams extends Params {
    @Input
    @Optional
    Property<String> getDomain();
  }
}
