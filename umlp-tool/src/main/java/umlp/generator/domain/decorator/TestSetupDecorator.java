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

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class TestSetupDecorator extends AbstractDecorator {
  public static final String PACK_TEST_INFRASTRUCTURE = "test_infrastructure";
  public static final String MOCK_TEST_SETUP = "MockTestSetup";
  public static final String DB_TEST_SETUP = "DBTestSetup";

  private boolean isNeo4jGen;
  private final ObserverDecorator observerDecorator;
  private final DiagramObserverDecorator diagramObserverDecorator;

  public TestSetupDecorator(GlobalExtensionManagement glex,
                            boolean isNeo4jGen,
                            ObserverDecorator observerDecorator,
                            DiagramObserverDecorator diagramObserverDecorator) {
    super(glex);
    this.isNeo4jGen = isNeo4jGen;
    this.observerDecorator = observerDecorator;
    this.diagramObserverDecorator = diagramObserverDecorator;
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage aPackage = getPackage(origCD, decoratedCD, PACK_TEST_INFRASTRUCTURE);

    EnsureCorrectManager4TypeDecorator ensureCorrectManager4TypeDecorator =
        new EnsureCorrectManager4TypeDecorator(
        glex,
        observerDecorator);
    ensureCorrectManager4TypeDecorator.transform(origCD, decoratedCD);

    EnsureCorrectManager4DiagrammDecorator ensureCorrectManager4DiagrammDecorator = new EnsureCorrectManager4DiagrammDecorator(
        glex,
        diagramObserverDecorator, ensureCorrectManager4TypeDecorator);
    ensureCorrectManager4DiagrammDecorator.transform(origCD, decoratedCD);

    aPackage.addCDElement(withOrigin(createMockTestSetup(origCD)));
    aPackage.addCDElement(withOrigin(createDbTestSetup(origCD)));
  }

  protected ASTCDClass createMockTestSetup(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass res = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage("umlp.backendrte.test_infrastructure.TestSetup<" + name + "TestContext>"))
        .setName(name + MOCK_TEST_SETUP).build();

    // Attributes
    String testContextClass = name + TestContextDecorator.SUFFIX;
    getCD4C().addAttribute(res, true, false, "protected " + testContextClass + " backend;");
    addAttribute(res, true, false, "protected List<" + name + TestContextDecorator.SUFFIX + "> clients", "new ArrayList<>()");
    ASTCDAttribute numberOfClientsArg = getCD4C().addAttribute(res, true, false, "protected int numberOfClients;");
    ASTCDAttribute clientNamesArg = getCD4C().addAttribute(res,
                                                           true,
                                                           false,
                                                           "protected List<String> clientNames;");

    String commonCustomTemplatePath = "umlp.test_infrastructure.";

    // Constructor
    String commonConstructorTemplate = "umlp.common.BasicConstructor";
    getCD4C().addConstructor(res,
                             commonConstructorTemplate, res.getName(), List.of(numberOfClientsArg));
    getCD4C().addConstructor(res,
                             commonConstructorTemplate,
                             res.getName(),
                             List.of(numberOfClientsArg, clientNamesArg));
    getCD4C().addConstructor(res, commonCustomTemplatePath + "NamesConstructor",
                             res.getName(),
                             clientNamesArg);

    // Methods
    getCD4C().addMethod(res, commonCustomTemplatePath + "GetClientName", name);
    getCD4C().addMethod(res, commonCustomTemplatePath + "mockTestSetup.Init", name);
    getCD4C().addMethod(res, commonCustomTemplatePath + "mockTestSetup.HasDatabase", name);

    addImports(origCD, res);
    return res;
  }

  protected ASTCDClass createDbTestSetup(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass res = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage("umlp.backendrte.test_infrastructure.TestSetup<" + name + "TestContext>"))
        .setName(name + DB_TEST_SETUP).build();

    // Attributes
    String testContextClass = name + TestContextDecorator.SUFFIX;
    getCD4C().addAttribute(res, true, false, "protected " + testContextClass + " backend;");
    addAttribute(res, true, false, "protected List<" + name + TestContextDecorator.SUFFIX + "> clients", "new ArrayList<>()");
    ASTCDAttribute numberOfClientsArg = getCD4C().addAttribute(res, true, false, "protected int numberOfClients;");
    ASTCDAttribute clientNamesArg = getCD4C().addAttribute(res,
                                                           true,
                                                           false,
                                                           "protected List<String> clientNames;");

    String commonCustomTemplatePath = "umlp.test_infrastructure.";

    // Constructor
    String commonConstructorTemplate = "umlp.common.BasicConstructor";
    getCD4C().addConstructor(res,
                             commonConstructorTemplate,
                             res.getName(),
                             List.of(numberOfClientsArg));
    getCD4C().addConstructor(res,
                             commonConstructorTemplate,
                             res.getName(),
                             List.of(numberOfClientsArg, clientNamesArg));
    getCD4C().addConstructor(res,
                             commonCustomTemplatePath + "NamesConstructor",
                             res.getName(),
                             clientNamesArg);

    // Methods
    getCD4C().addMethod(res, commonCustomTemplatePath + "GetClientName", name);
    if(isNeo4jGen) {
      getCD4C().addMethod(res, "umlp.test_infrastructure.dbTestSetup.InitNeo4j", name);
    } else {
      getCD4C().addMethod(res, "umlp.test_infrastructure.dbTestSetup.Init", name);
    }
    getCD4C().addMethod(res, "umlp.test_infrastructure.dbTestSetup.Cleanup", name);
    getCD4C().addMethod(res, "umlp.test_infrastructure.dbTestSetup.HasDatabase", name);

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
    getCD4C().addImport(type, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), name + DAOFactoryDecorator.SUFFIX));

    getCD4C().addImport(type, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), name + ManagerMockDecorator.SUFFIX));
    getCD4C().addImport(type, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), name + ManagerClientImplDecorator.SUFFIX));

    getCD4C().addImport(type, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), name + ManagerImplDecorator.SUFFIX));
    getCD4C().addImport(type, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), name + DbObserverDecorator.SUFFIX));
    getCD4C().addImport(type, Joiners.DOT.join(PathUtil.getBackendRTECommonPath(), "DatabaseAccessManager"));
    getCD4C().addImport(type, "java.nio.charset.StandardCharsets");
    getCD4C().addImport(type, "org.apache.commons.io.IOUtils");
    getCD4C().addImport(type, "java.util.Spliterator");
    getCD4C().addImport(type, "java.util.ListIterator");

    if(isNeo4jGen) {
      getCD4C().addImport(type, Joiners.DOT.join(PathUtil.getBackendRTECommonPath(), "DatabaseAccessManager4Neo4j"));
    } else {
      getCD4C().addImport(type, Joiners.DOT.join(PathUtil.getBackendRTECommonPath(), "DatabaseAccessManager4SQL"));
    }
  }
}
