/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.config;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.io.paths.MCPath;
import umlp.generator.gui.GUIGeneratorUtil;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GUI2ImportMapGenerator {

  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();
  protected final GeneratorEngine generatorEngine;
  protected GeneratorSetup setup;
  protected MCPath hwcPath;

  public GUI2ImportMapGenerator(GeneratorSetup generatorSetup) {
    this.generatorEngine = new GeneratorEngine(generatorSetup);
    this.setup = generatorSetup;
    this.hwcPath = generatorSetup.getHandcodedPath();
  }

  public void generate(String tslib, Collection<ASTGUICompilationUnit> asts) {
    List<String> qNames = new ArrayList<>();
    for (ASTGUICompilationUnit ast : asts) {
      qNames.add(ast.getComponentQName());
    }
    String out = "import.map";
    generatorEngine.generateNoA("umlp.GUI2ImportMap", Paths.get(out), tslib, qNames);
    Reporting.reportFileCreation(
        Paths.get(setup.getOutputDirectory().getPath(), out).toString());
  }

}
