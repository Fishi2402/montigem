package umlp.generator.test_infrastructure.domain.decorator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4codebasis._ast.ASTCDParameterTOP;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.util.PathUtil;

import java.util.stream.Collectors;

public class MonitorDecorator extends AbstractDecorator {

  public static final String TEST_INFRASTRUCTURE_PACKAGE = "test_infrastructure";
  public static final String MONITOR_SUFFIX = "Monitor";

  public MonitorDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    var targetPackage = getPackage(origCD, decoratedCD, TEST_INFRASTRUCTURE_PACKAGE);

    var builderDecorator = new BuilderDecorator(glex);
    var redecoratedCD = UMLPMill
        .cDCompilationUnitBuilder()
        .setCDDefinition(origCD.getCDDefinition())
        .build();
    builderDecorator.transform(origCD, redecoratedCD);

    redecoratedCD
        .getCDDefinition()
        .getCDClassesList()
        .stream()
        .filter(astcdClass -> {
          var modifier = astcdClass.getModifier();
          return modifier.isPresentStereotype() &&
                 modifier.getStereotype().contains("needs-monitor");
        })
        .forEach(astcdClass -> targetPackage.addCDElement(withOrigin(createMonitor(origCD,
                                                                                   astcdClass))));
  }

  private ASTCDElement createMonitor(ASTCDCompilationUnit origCd,
      ASTCDClass decoratedBuilderClass) {
    var builderName = decoratedBuilderClass.getName();
    var packageName = getPackageName(origCd, DEFAULT_PACKAGE);
    var res = UMLPMill
        .cDClassBuilder()
        .setModifier(CDModifier.PUBLIC.build())
        .setName(builderName + MONITOR_SUFFIX)
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(builderName))
        .build();

    var cd4C = getCD4C();
    cd4C.addImport(res, Joiners.DOT.join(packageName, "*"));
    cd4C.addImport(res,
                   Joiners.DOT.join(PathUtil.getTestInfrastructurePath(),
                                    "monitor",
                                    "MethodMonitor"));

    //constructors
    addSuperConstructors(res, decoratedBuilderClass);

    //override all generated methods
    decoratedBuilderClass.getCDMethodList().stream().filter(method -> {
      var modifier = method.getModifier();
      return !modifier.isStatic() && !modifier.isFinal() && !modifier.isPrivate();
    }).forEach(method -> {
      var returnType = method.getMCReturnType().printType();
      var name = method.getName();
      var params = method.getCDParameterList();
      var paramSignature = params
          .stream()
          .map(param -> param.getMCType().printType() + " " + param.getName())
          .collect(Collectors.joining(", "));
      var paramCallsite = params
          .stream()
          .map(ASTCDParameterTOP::getName)
          .collect(Collectors.joining(", "));
      var isVoid = returnType.equals("void");

      cd4C.addMethod(res,
                     "umlp.monitor.monitor-method",
                     name,
                     returnType,
                     paramSignature,
                     paramCallsite,
                     isVoid);
    });

    return res;
  }
}
