/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle.specific;

import umlp.generator.gui.JSweetConfigGenerator;

import java.io.IOException;

public abstract class UMLP2JSweetAdapterAction extends Model2JSweetAdapterAction<Model2JSweetAdapterAction.Params> {

  /**
   * Generates configuration files required for the JSweet transpilation.
   */
  protected void generateJSweetConfig() throws IOException {
    JSweetConfigGenerator generator = getGenerator();
    generator.generate(getParameters().getDomain().get(), true);
  }

}
