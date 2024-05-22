/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript;

import de.monticore.generating.GeneratorSetup;
import de.monticore.prettyprint.IndentPrinter;
import umlp.generator.gui.genjava.GUI2JavaGenerator;
import umlp.generator.gui.gentypescript.printer.TSUtilsPrinter;
import umlp.generator.gui.gentypescript.printer.TypeScriptTypesFullPrettyPrinter;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GUI2TSGenerator extends GUI2JavaGenerator {

  protected static final String TS_EXTENSION = ".ts";

  public GUI2TSGenerator(GeneratorSetup genSetup) {
    super(genSetup);
    genSetup.getGlex().setGlobalValue("cdPrinter", new TSUtilsPrinter());
  }

  protected Path getAsPath(String packageAsPath, String name) {
    return Paths.get(packageAsPath, name + TS_EXTENSION);
  }

}
