/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;

import java.nio.file.Paths;
import java.util.Map;

public class UI4TeachingGenerator {
  protected GeneratorSetup generatorSetup;
  protected GeneratorEngine generatorEngine;
  protected Map<String, String> fqnClassNameToType;

  public UI4TeachingGenerator(GeneratorSetup generatorSetup) {
    this.generatorSetup = generatorSetup;
    this.generatorEngine = new GeneratorEngine(generatorSetup);
    generatorSetup.setTracing(false);
  }

  public void generate(ASTCDCompilationUnit cdAST){
    generatorEngine.generate("umlp.gui4teaching.html", Paths.get("frontend4teaching","static","index.html"), cdAST);
    generatorEngine.generate("umlp.gui4teaching.js", Paths.get("frontend4teaching", "static","index.js"), cdAST, cdAST.getCDDefinition().getCDClassesList());

  }


}
