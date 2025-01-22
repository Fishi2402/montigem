package umlp.generator.test_infrastructure.domain.decorator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.BuilderDecorator;

public class MonitorDecorator extends AbstractDecorator {

  public static final String TEST_INFRASTRUCTURE_PACKAGE = "test_infrastructure";
  public static final String MONITOR_SUFFIX = "Monitor";

  public MonitorDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    var targetPackage = getPackage(origCD, decoratedCD, TEST_INFRASTRUCTURE_PACKAGE);
    getConcreteDomainClasses(origCD).forEach(c -> targetPackage.addCDElement(withOrigin(
        createMonitor(origCD))));
  }

  private ASTCDElement createMonitor(ASTCDCompilationUnit origCD) {
    var cdName = origCD.getCDDefinition().getName();
    var res = UMLPMill
        .cDClassBuilder()
        .setModifier(CDModifier.PUBLIC.build())
        .setName(cdName + MONITOR_SUFFIX)
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(
            cdName + BuilderDecorator.BUILDER_SUFFIX))
        .build();
    //TODO override all the generated methods
    return res;
  }
}
