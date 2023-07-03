package umlp.generator.domain.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class TestSetupDecorator extends AbstractDecorator {
  public static final String PACK_TEST_INFRASTRUCTURE = "test_infrastructure";
  public static final String SUFFIX = "MockTestSetup";

  public TestSetupDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage aPackage = getPackage(origCD, decoratedCD, PACK_TEST_INFRASTRUCTURE);
    aPackage.addCDElement(createMockTestSetup(origCD));
  }

  protected ASTCDClass createMockTestSetup(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass res = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(name + SUFFIX).build();

    // Attributes
    String testContextClass = name + TestContextDecorator.SUFFIX;
    getCD4C().addAttribute(res, false, false, "protected " + testContextClass + " backend;");
    getCD4C().addAttribute(res, false, false, "protected List<" + name + TestContextDecorator.SUFFIX + "> clients;");

    // Methods

    getCD4C().addMethod(res, "umlp.test_infrastructure.mockTestSetup.Init", name);

    addImports(origCD, res);
    return res;
  }

  protected void addImports(ASTCDCompilationUnit origCD, ASTCDType type){
    String name = origCD.getCDDefinition().getName();

    getCD4C().addImport(type, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), name + TestContextDecorator.SUFFIX));
    getCD4C().addImport(type, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), name + ManagerMockDecorator.SUFFIX));
  }
}
