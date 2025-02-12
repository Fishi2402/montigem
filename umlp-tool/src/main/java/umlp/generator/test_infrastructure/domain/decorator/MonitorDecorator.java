package umlp.generator.test_infrastructure.domain.decorator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameterTOP;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MonitorDecorator extends AbstractDecorator {

  public static final String TEST_INFRASTRUCTURE_PACKAGE = "test_infrastructure";
  public static final String MONITOR_SUFFIX = "Monitor";
  public static final String STEREOTYPE_NEEDS_MONITOR = "needs-monitor";

  private final ASTCDCompilationUnit redecoratedCD;

  public MonitorDecorator(GlobalExtensionManagement glex, ASTCDCompilationUnit redecoratedCD) {
    super(glex);
    this.redecoratedCD = redecoratedCD;
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    var targetPackage = getPackage(origCD, decoratedCD, TEST_INFRASTRUCTURE_PACKAGE);

    redecoratedCD
        .getCDDefinition()
        .getCDClassesList()
        .stream()
        .filter(astcdClass -> {
          var modifier = astcdClass.getModifier();
          return modifier.isPresentStereotype() &&
                 modifier.getStereotype().contains(STEREOTYPE_NEEDS_MONITOR);
        })
        .forEach(astcdClass -> targetPackage.addCDElement(withOrigin(createMonitor(astcdClass,
                                                                                   origCD,
                                                                                   redecoratedCD))));
  }

  private ASTCDElement createMonitor(ASTCDClass decoratedBuilderClass,
      ASTCDCompilationUnit origCd,
      ASTCDCompilationUnit redecoratedCD) {
    var builderName = decoratedBuilderClass.getName();
    var packageName = getPackageName(origCd, DEFAULT_PACKAGE);
    var res = UMLPMill
        .cDClassBuilder()
        .setModifier(CDModifier.PUBLIC.build())
        .setName(builderName + MONITOR_SUFFIX)
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(builderName))
        .build();

    var cd4C = getCD4C();

    //constructors
    addSuperConstructors(res, decoratedBuilderClass);

    //override all generated methods
    getMethodsToMonitor(decoratedBuilderClass, redecoratedCD).forEach(method -> {
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

    //imports
    cd4C.addImport(res, Joiners.DOT.join(packageName, "*"));
    cd4C.addImport(res,
                   Joiners.DOT.join(PathUtil.getTestInfrastructurePath(),
                                    "monitor",
                                    "MethodMonitor"));
    //TODO import GemList and NotificationScope from umlp.backendrte.common

    return res;
  }

  private Set<ASTCDMethod> getMethodsToMonitor(ASTCDClass classToMonitor,
      ASTCDCompilationUnit decoratedCD) {
    var methods = classToMonitor.getCDMethodList().stream().filter(method -> {
      var modifier = method.getModifier();
      return !modifier.isStatic() && !modifier.isFinal() && !modifier.isPrivate();
    }).collect(Collectors.toCollection(HashSet::new));

    //super class
    if (classToMonitor.isPresentCDExtendUsage()) {
      //TODO use signatures for comparison, since return type Option<? extends T> and Option<T> are not the same but may refer to the same method
      methods.addAll(classToMonitor
                         .getCDExtendUsage()
                         .getSuperclassList()
                         .stream()
                         .filter(type -> type instanceof ASTMCQualifiedType)
                         .map(type -> (ASTMCQualifiedType) type)
                         .map(type -> type.getMCQualifiedName().getBaseName())
                         .flatMap(superClassName -> decoratedCD
                             .getCDDefinition()
                             .getCDClassesList()
                             .stream()
                             .filter(cdClass -> cdClass.getName().equals(superClassName))
                             .flatMap(superClass -> getMethodsToMonitor(superClass,
                                                                        decoratedCD).stream()))
                         .collect(Collectors.toList()));
    }

    //TODO monitor default methods of interfaces

    return methods;
  }
}
