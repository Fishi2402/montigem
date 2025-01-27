package umlp.generator.test_infrastructure.domain.decorator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cdbasis._ast.ASTCDClass;
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

    decoratedCD
        .getCDDefinition()
        .getCDClassesList()
        .stream()
        .filter(astcdClass -> {
          var modifier = astcdClass.getModifier();
          return modifier.isPresentStereotype() && modifier.getStereotype().contains("needs-monitor");
        })
        .forEach(astcdClass -> targetPackage.addCDElement(withOrigin(createMonitor(astcdClass))));
  }

  private ASTCDElement createMonitor(ASTCDClass decoratedBuilderClass) {
    var builderName = decoratedBuilderClass.getName();
    var res = UMLPMill
        .cDClassBuilder()
        .setModifier(CDModifier.PUBLIC.build())
        .setName(builderName + MONITOR_SUFFIX)
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(
            builderName + BuilderDecorator.BUILDER_SUFFIX))
        .build();

    //override all generated methods
    decoratedBuilderClass.getCDMethodList().stream().filter(method -> {
      var modifier = method.getModifier();
      return !modifier.isStatic() && !modifier.isFinal() && !modifier.isPrivate();
    }).forEach(method -> {
      var returnType = method.getMCReturnType().printType();
      var name = method.getName();
      var params = method.getCDParameterList();

      getCD4C().addMethod(res, "umlp.monitor.monitor-method", name, returnType, params);
    });

    return res;
  }
}
