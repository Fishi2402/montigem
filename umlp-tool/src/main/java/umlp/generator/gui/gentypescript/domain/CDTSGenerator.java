/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd.codegen.CDGenerator;
import de.monticore.generating.GeneratorSetup;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CDTSGenerator extends CDGenerator {

  protected static final String TS_EXTENSION = ".ts";

  public CDTSGenerator(GeneratorSetup generatorSetup) {
    super(generatorSetup);
  }

  protected Path getAsPath(String packageAsPath, String name) {
    return Paths.get(packageAsPath, name + TS_EXTENSION);
  }

}
