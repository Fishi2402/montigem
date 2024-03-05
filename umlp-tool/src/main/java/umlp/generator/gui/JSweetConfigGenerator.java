/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;

import java.nio.file.Paths;

public class JSweetConfigGenerator {

  protected GeneratorSetup generatorSetup;
  protected GeneratorEngine generatorEngine;

  public JSweetConfigGenerator(GeneratorSetup generatorSetup) {
    this.generatorSetup = generatorSetup;
    this.generatorEngine = new GeneratorEngine(generatorSetup);
    generatorSetup.setTracing(false);
  }

  public void generate(String domainName) {
    generatorEngine.generateNoA(
        "umlp.jsweetconfig.JSweetConfigJSON",
        Paths.get("jsweetconfig.json"),
        domainName);
    generatorEngine.generateNoA(
        "umlp.jsweetconfig.JSweetDomainImportAdapter",
        Paths.get("jsweet_extension", "umlp", "jsweet", "extension", "domainAdapters", domainName + "ImportAdapter.java"),
        domainName);
  }

}
