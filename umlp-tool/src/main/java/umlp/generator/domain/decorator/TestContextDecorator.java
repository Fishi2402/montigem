package umlp.generator.domain.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
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
    astcdPackage.addCDElement(createClass(origCD));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass testContextClass = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(name + SUFFIX).build();


    // Attributes
    getCD4C().addAttribute(testContextClass, true, true,"protected " + name + ManagerDecorator.SUFFIX + " manager");
    getCD4C().addAttribute(testContextClass, true, true, "protected CommandManager commandManager");
    getCD4C().addAttribute(testContextClass, true, true, "protected GeneralErrorCollector generalErrorCollector");

    List<ASTCDClass> relevantClasses = origCD.getCDDefinition().getCDClassesList().stream()
        .filter(c -> !c.getModifier().isDerived()).collect(Collectors.toList());

    relevantClasses.forEach(c -> {
          getCD4C().addAttribute(testContextClass, true, true,"protected " + c.getName() + ErrorCollectorDecorator.SUFFIX + " " + StringTransformations.uncapitalize(c.getName()) + ErrorCollectorDecorator.SUFFIX);
        });

    // Constructor
    getCD4C().addConstructor(testContextClass, "umlp.common.BasicConstructor", testContextClass.getName(), testContextClass.getCDAttributeList());

    // Methods
    getCD4C().addMethod(testContextClass, "umlp.testcontext.FromCurrentContext", name, relevantClasses);
    getCD4C().addMethod(testContextClass, "umlp.testcontext.Activate", name, relevantClasses);
    getCD4C().addMethod(testContextClass, "umlp.testcontext.Clear", name, relevantClasses);

    // Imports
    getCD4C().addImport(testContextClass, PathUtil.getBackendRTECommandPath("CommandManager"));
    getCD4C().addImport(testContextClass, PathUtil.getBackendRTECommonPath("GeneralErrorCollector"));

    return testContextClass;
  }
}
