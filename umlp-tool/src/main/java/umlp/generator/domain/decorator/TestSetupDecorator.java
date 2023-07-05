package umlp.generator.domain.decorator;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;

public class TestSetupDecorator extends AbstractDecorator {
  public static final String PACK_TEST_INFRASTRUCTURE = "test_infrastructure";
  public static final String MOCK_TEST_SETUP = "MockTestSetup";
  public static final String DB_TEST_SETUP = "DBTestSetup";

  public TestSetupDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage aPackage = getPackage(origCD, decoratedCD, PACK_TEST_INFRASTRUCTURE);
    aPackage.addCDElement(createMockTestSetup(origCD));
    aPackage.addCDElement(createDbTestSetup(origCD));
  }

  protected ASTCDClass createMockTestSetup(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass res = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC_ABSTRACT.build())
        .setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage("umlp.backendrte.test_infrastructure.TestSetup"))
        .setName(name + MOCK_TEST_SETUP).build();

    // Attributes
    String testContextClass = name + TestContextDecorator.SUFFIX;
    getCD4C().addAttribute(res, false, false, "protected " + testContextClass + " backend;");
    ASTCDAttribute clientsAttr = getCD4C().addAttribute(res, false, false, "protected List<" + name + TestContextDecorator.SUFFIX + "> clients;");
    glex.replaceTemplate("cd2java.Value", clientsAttr, new StringHookPoint(" = new ArrayList<>();"));

    // Methods
    getCD4C().addMethod(res, "umlp.test_infrastructure.mockTestSetup.Init", name);

    addImports(origCD, res);
    return res;
  }

  protected ASTCDClass createDbTestSetup(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass res = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC_ABSTRACT.build())
        .setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage("umlp.backendrte.test_infrastructure.TestSetup"))
        .setName(name + DB_TEST_SETUP).build();

    // Attributes
    String testContextClass = name + TestContextDecorator.SUFFIX;
    getCD4C().addAttribute(res, false, false, "protected " + testContextClass + " backend;");
    ASTCDAttribute clientsAttr = getCD4C().addAttribute(res, false, false, "protected List<" + name + TestContextDecorator.SUFFIX + "> clients;");
    glex.replaceTemplate("cd2java.Value", clientsAttr, new StringHookPoint(" = new ArrayList<>();"));

    // Methods
    getCD4C().addMethod(res, "umlp.test_infrastructure.dbTestSetup.Init", name);
    getCD4C().addMethod(res, "umlp.test_infrastructure.dbTestSetup.Cleanup", name);

    addImports(origCD, res);
    return res;
  }

  protected void addImports(ASTCDCompilationUnit origCD, ASTCDType type){
    String name = origCD.getCDDefinition().getName();

    getCD4C().addImport(type, Joiners.DOT.join(PathUtil.getBackendRTECommandPath(), "CommandManager"));
    getCD4C().addImport(type, Joiners.DOT.join(PathUtil.getBackendRTECommandPath(), "TestClientCommandManager"));
    getCD4C().addImport(type, Joiners.DOT.join(PathUtil.getBackendRTECommandPath(), "CommandDirectCommunication"));

    getCD4C().addImport(type, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), name + TestContextDecorator.SUFFIX));
    getCD4C().addImport(type, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), name + ManagerDecorator.SUFFIX));

    getCD4C().addImport(type, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), name + ManagerMockDecorator.SUFFIX));
    getCD4C().addImport(type, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), name + ManagerClientImplDecorator.SUFFIX));

    getCD4C().addImport(type, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), name + ManagerImplDecorator.SUFFIX));
    getCD4C().addImport(type, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), name + DbObserverDecorator.SUFFIX));
    getCD4C().addImport(type, Joiners.DOT.join(PathUtil.getBackendRTECommonPath(), "DatabaseAccessManager"));
    getCD4C().addImport(type, "java.nio.charset.StandardCharsets");
    getCD4C().addImport(type, "org.apache.commons.io.IOUtils");
  }
}
