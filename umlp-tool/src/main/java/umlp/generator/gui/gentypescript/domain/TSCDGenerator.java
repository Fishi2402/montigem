/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd.codegen.CDGenerator;
import de.monticore.generating.GeneratorSetup;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TSCDGenerator extends CDGenerator {

  protected static final String TS_EXTENSION = ".ts";

  public TSCDGenerator(GeneratorSetup generatorSetup) {
    super(generatorSetup);
  }

  protected Path getAsPath(String packageAsPath, String name) {
    return Paths.get(packageAsPath, name + TS_EXTENSION);
  }

}
