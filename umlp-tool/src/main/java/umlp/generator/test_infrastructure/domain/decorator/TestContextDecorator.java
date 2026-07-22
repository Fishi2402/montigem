package umlp.generator.test_infrastructure.domain.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.ErrorCollectorDecorator;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.util.PathUtil;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class TestContextDecorator extends AbstractDecorator {
  public static final String SUFFIX = "TestContext";


  public TestContextDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(withOrigin(createClass(origCD)));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass testContextClass = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage("TestContext"))
        .setName(name + SUFFIX).build();

    // Add @jsweet.lang.Erased annotation
    // Add @jsweet.lang.Erased annotation
    glex.replaceTemplate("cd2java.Annotations", testContextClass,
        new StringHookPoint("@jsweet.lang.Erased"));



    // Attributes
    getCD4C().addAttribute(testContextClass, true, true,"protected " + name + ManagerDecorator.SUFFIX + " manager");
    List<ASTCDCompilationUnit> extendedDefinitions = getExtendedDefinitions(origCD);
    List<String> extendedCDNames = extendedDefinitions.stream()
        .map(cd -> cd.getCDDefinition().getName())
        .collect(Collectors.toList());
    extendedCDNames.forEach(extendedCDName -> {
      getCD4C().addAttribute(testContextClass, true, true,
          "protected " + extendedCDName + ManagerDecorator.SUFFIX + " "
              + StringTransformations.uncapitalize(extendedCDName + ManagerDecorator.SUFFIX));
    });
    getCD4C().addAttribute(testContextClass, true, true, "protected CommandManager commandManager");
    getCD4C().addAttribute(testContextClass, true, true, "protected GeneralErrorCollector generalErrorCollector");

    List<ASTCDClass> relevantClasses = getNonDerivedDomainClasses(origCD);

    relevantClasses.forEach(c -> {
          getCD4C().addAttribute(testContextClass, true, true,"protected " + c.getName() + ErrorCollectorDecorator.SUFFIX + " " + StringTransformations.uncapitalize(c.getName()) + ErrorCollectorDecorator.SUFFIX);
        });

    // Constructor
    getCD4C().addConstructor(testContextClass, "umlp.common.BasicConstructor", testContextClass.getName(), testContextClass.getCDAttributeList());

    // Methods
    getCD4C().addMethod(testContextClass, "umlp.testcontext.FromCurrentContext", name, relevantClasses, extendedCDNames);
    getCD4C().addMethod(testContextClass, "umlp.testcontext.Activate", name, relevantClasses, extendedCDNames);
    getCD4C().addMethod(testContextClass, "umlp.testcontext.Clear", name, relevantClasses, extendedCDNames);

    // Imports
    extendedDefinitions.forEach(extendedDefinition -> getCD4C().addImport(testContextClass,
        getPackageName(extendedDefinition, DEFAULT_PACKAGE) + "."
            + extendedDefinition.getCDDefinition().getName() + ManagerDecorator.SUFFIX));
    getCD4C().addImport(testContextClass, PathUtil.getBackendRTECommandPath("CommandManager"));
    getCD4C().addImport(testContextClass, PathUtil.getBackendRTECommonPath("GeneralErrorCollector"));
    getCD4C().addImport(testContextClass, PathUtil.getBackendRTEUtilPath("TestContext"));

    return testContextClass;
  }
}
