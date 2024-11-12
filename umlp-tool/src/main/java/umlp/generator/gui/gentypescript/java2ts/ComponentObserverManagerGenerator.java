/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.reporting.Reporting;
import umlp.generator.Filter;
import umlp.generator.gui.GUIGeneratorUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ComponentObserverManagerGenerator {

  protected GeneratorSetup generatorSetup;
  protected GeneratorEngine generatorEngine;

  public ComponentObserverManagerGenerator(GeneratorSetup generatorSetup) {
    this.generatorSetup = generatorSetup;
    this.generatorEngine = new GeneratorEngine(generatorSetup);
    generatorSetup.setTracing(false);
  }

  public void generate(ASTCDCompilationUnit ast) {
    Reporting.off();
    String cdName = ast.getCDDefinition().getName();
    Path targetPath = Paths.get(cdName + "ComponentObserverManager.ts");
    List<String> domainClasses = getConcreteDomainClasses(ast).stream().map(domainClass -> domainClass.getName()).collect(Collectors.toList());
    generatorEngine.generateNoA("umlp.gui2ts.TSComponentObserverManager", targetPath, ast, cdName, domainClasses, cdName.toLowerCase());

    Reporting.on(cdName);
    Reporting.reportFileCreation(targetPath.toString());
  }

  protected List<ASTCDClass> getConcreteDomainClasses(ASTCDCompilationUnit cd) {
    return getDomainClasses(cd, Filter.DERIVED.negate());
  }

  protected List<ASTCDClass> getDomainClasses(
      ASTCDCompilationUnit cd, Predicate<Filter.Wrapper> filter) {
    return cd.getCDDefinition()
        .getCDClassesList()
        .stream()
        .filter(c -> filter.test(Filter.Wrapper.wrap(c)))
        .collect(Collectors.toList());
  }
}
