package umlp.generator.domain.decorator;

import com.google.common.base.Joiner;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;

public class TestSetupDecorator extends AbstractDecorator {

  public static final String PACK_TEST_INFRASTRUCTURE = "test_infrastructure";
  public static final String SUFFIX = "TestSetup";
  public static final String MOCK_TEST_SETUP_SUFFIX = "Mock" + SUFFIX;
  public static final String DB_TEST_SETUP_SUFFIX = "DB" + SUFFIX;
  public static final String SQL_TEST_SETUP_SUFFIX = "SQL" + DB_TEST_SETUP_SUFFIX;
  public static final String NEO4J_TEST_SETUP_SUFFIX = "Neo4J" + DB_TEST_SETUP_SUFFIX;
  private static final String COMMON_CUSTOM_TEMPLATE_PATH = "umlp.test_infrastructure.";
  private static final String DB_TEMPLATE_PATH = COMMON_CUSTOM_TEMPLATE_PATH + "dbTestSetup.";

  private final boolean isNeo4jGen;
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
        diagramObserverDecorator,
        ensureCorrectManager4TypeDecorator);
    ensureCorrectManager4DiagrammDecorator.transform(origCD, decoratedCD);


    ASTCDClass abstractTestSetup = createAbstractTestSetup(origCD);
    aPackage.addCDElement(withOrigin(abstractTestSetup));
    aPackage.addCDElement(withOrigin(createMockTestSetup(origCD, abstractTestSetup)));
    ASTCDClass abstractDBTestSetup = createAbstractDBTestSetup(origCD, abstractTestSetup);
    aPackage.addCDElement(withOrigin(abstractDBTestSetup));
    if (isNeo4jGen) {
      aPackage.addCDElement(withOrigin(createNeo4JDBTestSetup(origCD, abstractTestSetup)));
    } else {
      aPackage.addCDElement(withOrigin(createSQLDBTestSetup(origCD, abstractDBTestSetup)));
    }
  }

  protected ASTCDClass createAbstractTestSetup(ASTCDCompilationUnit origCD) {
    String cdName = origCD.getCDDefinition().getName();

    ASTCDClass res = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC_ABSTRACT.build())
        .setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage(Joiners.DOT.join(
            PathUtil.getBackendRTETestInfrastructurePath(),
            "TestSetup<" + cdName + "TestContext>")))
        .setName(cdName + SUFFIX)
        .build();

    CD4C cd4C = getCD4C();
    String typeName = res.getName();

    // attributes
    String testContextClass = cdName + TestContextDecorator.SUFFIX;
    cd4C.addAttribute(res, true, false, "protected " + testContextClass + " backend;");
    addAttribute(res,
                 true,
                 false,
                 "protected List<" + cdName + TestContextDecorator.SUFFIX + "> clients",
                 "new ArrayList<>()");
    ASTCDAttribute clientSetupAttr = cd4C.addAttribute(res,
                                                 true,
                                                 false,
                                                 "protected ClientSetup clientSetup;");


    GenSummary genSummary = new GenSummary(clientSetupAttr);

    // constructor
    //this should never fail
    @SuppressWarnings("OptionalGetWithoutIsPresent") ASTCDAttribute fakeNumberOfClientsAttr = cd4C.createAttribute(
        res,
        "int numberOfClients").get();

    String basicTemplate = "umlp.common.BasicConstructor";
    String numOfClientsTemplate = COMMON_CUSTOM_TEMPLATE_PATH + "NumberOfClientsConstructor";

    cd4C.addConstructor(res, basicTemplate,
                        typeName,
                        List.of(genSummary.clientSetupAttr()));
    //support legacy calls where constructor is called with only an int describing the number of clients
    cd4C.addConstructor(res, numOfClientsTemplate,
                        typeName, fakeNumberOfClientsAttr);

    // common methods
    cd4C.addMethod(res, COMMON_CUSTOM_TEMPLATE_PATH + "GetClientName", cdName);
    cd4C.addMethod(res, COMMON_CUSTOM_TEMPLATE_PATH + "GetNumberOfClients");
    cd4C.addMethod(res, COMMON_CUSTOM_TEMPLATE_PATH + "Init", cdName);
    cd4C.addMethod(res, COMMON_CUSTOM_TEMPLATE_PATH + "HasDatabase");

    addImports(origCD, res);

    return res;
  }

  protected ASTCDClass createMockTestSetup(ASTCDCompilationUnit origCD, ASTCDClass parentClass) {
    String cdName = origCD.getCDDefinition().getName();

    ASTCDClass res = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(parentClass.getName()))
        .setName(cdName + MOCK_TEST_SETUP_SUFFIX)
        .build();

    CD4C cd4C = getCD4C();

    // constructor
    addSuperConstructors(res, parentClass);

    // methods
    cd4C.addMethod(res, COMMON_CUSTOM_TEMPLATE_PATH + "mockTestSetup.Init", cdName);
    cd4C.addMethod(res, COMMON_CUSTOM_TEMPLATE_PATH + "mockTestSetup.HasDatabase", cdName);

    addImports(origCD, res);
    return res;
  }

  protected ASTCDClass createAbstractDBTestSetup(ASTCDCompilationUnit origCD, ASTCDClass parentClass) {
    String cdName = origCD.getCDDefinition().getName();

    ASTCDClass res = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC_ABSTRACT.build())
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(parentClass.getName()))
        .setName(cdName + DB_TEST_SETUP_SUFFIX)
        .build();

    // constructor
    addSuperConstructors(res, parentClass);

    // methods
    CD4C cd4C = getCD4C();
    cd4C.addMethod(res, DB_TEMPLATE_PATH + "Cleanup", cdName);
    cd4C.addMethod(res, DB_TEMPLATE_PATH + "HasDatabase", cdName);
    cd4C.addMethod(res, DB_TEMPLATE_PATH + "Init", cdName);
    cd4C.addMethod(res, DB_TEMPLATE_PATH + "LegacyCheck");

    addImports(origCD, res);
    return res;
  }

  protected ASTCDClass createSQLDBTestSetup(ASTCDCompilationUnit origCD, ASTCDClass parentClass) {
    String cdName = origCD.getCDDefinition().getName();

    ASTCDClass res = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(parentClass.getName()))
        .setName(cdName + SQL_TEST_SETUP_SUFFIX)
        .build();

    CD4C cd4C = getCD4C();

    // constructor
    addSuperConstructors(res, parentClass);

    // methods
    cd4C.addMethod(res, DB_TEMPLATE_PATH + "sql.Init", cdName);
    cd4C.addMethod(res, DB_TEMPLATE_PATH + "LegacyCheckImpl");

    addImports(origCD, res);
    return res;
  }

  protected ASTCDClass createNeo4JDBTestSetup(ASTCDCompilationUnit origCD, ASTCDClass parentClass) {
    String cdName = origCD.getCDDefinition().getName();

    ASTCDClass res = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(parentClass.getName()))
        .setName(cdName + NEO4J_TEST_SETUP_SUFFIX)
        .build();

    CD4C cd4C = getCD4C();

    // constructor
    addSuperConstructors(res, parentClass);

    // methods
    cd4C.addMethod(res, DB_TEMPLATE_PATH + "neo4j.Init", cdName);
    cd4C.addMethod(res, DB_TEMPLATE_PATH + "LegacyCheckImpl");

    addImports(origCD, res);
    return res;
  }

  protected void addImports(ASTCDCompilationUnit origCD, ASTCDType type){
    String name = origCD.getCDDefinition().getName();

    CD4C cd4C = getCD4C();
    Joiner dot = Joiners.DOT;
    String backendRTECommandPath = PathUtil.getBackendRTECommandPath();
    String backendRTECommonPath = PathUtil.getBackendRTECommonPath();
    String backendRTETestInfrastructurePath = PathUtil.getBackendRTETestInfrastructurePath();
    String packageName = getPackageName(origCD, DEFAULT_PACKAGE);

    cd4C.addImport(type, dot.join(backendRTECommandPath, "CommandManager"));
    cd4C.addImport(type, dot.join(backendRTECommandPath, "TestClientCommandManager"));
    cd4C.addImport(type, dot.join(backendRTECommandPath, "CommandDirectCommunication"));
    cd4C.addImport(type, dot.join(backendRTETestInfrastructurePath, "ClientSetup"));

    cd4C.addImport(type, dot.join(packageName, name + TestContextDecorator.SUFFIX));
    cd4C.addImport(type, dot.join(packageName, name + ManagerDecorator.SUFFIX));
    cd4C.addImport(type, dot.join(packageName, name + DAOFactoryDecorator.SUFFIX));

    cd4C.addImport(type, dot.join(packageName, name + ManagerMockDecorator.SUFFIX));
    cd4C.addImport(type, dot.join(packageName, name + ManagerClientImplDecorator.SUFFIX));

    cd4C.addImport(type, dot.join(packageName, name + ManagerImplDecorator.SUFFIX));
    cd4C.addImport(type, dot.join(packageName, name + DbObserverDecorator.SUFFIX));

    cd4C.addImport(type, dot.join(backendRTECommonPath, "DatabaseAccessManager"));
    cd4C.addImport(type, "java.nio.charset.StandardCharsets");
    cd4C.addImport(type, "org.apache.commons.io.IOUtils");
    cd4C.addImport(type, "java.util.Spliterator");
    cd4C.addImport(type, "java.util.ListIterator");

    if(isNeo4jGen) {
      cd4C.addImport(type, dot.join(backendRTECommonPath, "DatabaseAccessManager4Neo4j"));
    } else {
      cd4C.addImport(type, dot.join(backendRTECommonPath, "DatabaseAccessManager4SQL"));
    }
  }

  protected static final class GenSummary {

    private final ASTCDAttribute clientSetupAttr;

    public GenSummary(ASTCDAttribute clientSetupAttr) {
      this.clientSetupAttr = clientSetupAttr;
    }

    public ASTCDAttribute clientSetupAttr() {
      return clientSetupAttr;
    }
  }
}
