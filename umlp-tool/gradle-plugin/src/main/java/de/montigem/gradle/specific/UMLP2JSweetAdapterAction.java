/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle.specific;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.workers.WorkParameters;
import umlp.generator.gui.JSweetConfigGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public abstract class UMLP2JSweetAdapterAction extends Model2JSweetAdapterAction<UMLP2JSweetAdapterAction.UMLPParams> {

  /**
   * Generates configuration files required for the JSweet transpilation.
   */
  protected void generateJSweetConfig() throws IOException {
    JSweetConfigGenerator generator = getGenerator();
    generator.generate(getParameters().getDomain().get(), true, Collections.emptySet());
  }

  interface UMLPParams extends Params {
    @Input
    Property<String> getDomain();
  }
}
