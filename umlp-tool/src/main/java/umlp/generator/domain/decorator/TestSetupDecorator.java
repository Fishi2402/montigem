package umlp.generator.domain.decorator;

import com.google.common.base.Joiner;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
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
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;

public class TestSetupDecorator extends AbstractDecorator {

  public static final String PACK_TEST_INFRASTRUCTURE = "test_infrastructure";
  public static final String SUFFIX = "TestSetup";
  public static final String MOCK_TEST_SETUP_SUFFIX = "Mock" + SUFFIX;
  public static final String DB_TEST_SETUP_SUFFIX = "DB" + SUFFIX;
  public static final String SQL_TEST_SETUP_SUFFIX = "SQL" + DB_TEST_SETUP_SUFFIX;
  public static final String NEO4J_TEST_SETUP_SUFFIX = "Neo4J" + DB_TEST_SETUP_SUFFIX;

  private final ObserverDecorator observerDecorator;
  private final DiagramObserverDecorator diagramObserverDecorator;

  public TestSetupDecorator(GlobalExtensionManagement glex,
                            ObserverDecorator observerDecorator,
                            DiagramObserverDecorator diagramObserverDecorator) {
    super(glex);
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

    aPackage.addCDElement(withOrigin(createTestSetupInterface(origCD)));
    ASTCDClass abstractTestSetup = createAbstractTestSetup(origCD);
    aPackage.addCDElement(withOrigin(abstractTestSetup));
    aPackage.addCDElement(withOrigin(createMockTestSetup(origCD, abstractTestSetup)));
    ASTCDClass abstractDBTestSetup = createAbstractDBTestSetup(origCD, abstractTestSetup);
    aPackage.addCDElement(withOrigin(abstractDBTestSetup));
    aPackage.addCDElement(withOrigin(createNeo4JDBTestSetup(origCD, abstractDBTestSetup)));
    aPackage.addCDElement(withOrigin(createSQLDBTestSetup(origCD, abstractDBTestSetup)));
  }

  private ASTCDElement createTestSetupInterface(ASTCDCompilationUnit origCD) {
    String cdName = origCD.getCDDefinition().getName();

    ASTCDType res = UMLPMill.cDInterfaceBuilder()
        .setModifier(PUBLIC_ABSTRACT.build())
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(Joiners.DOT.join(
            PathUtil.getTestInfrastructurePath(),
            "TestSetup<" + cdName + "TestContext>")))
        .setName(cdName + SUFFIX)
        .build();

    String packageName = getPackageName(origCD, DEFAULT_PACKAGE);
    getCD4C().addImport(res, Joiners.DOT.join(packageName, cdName  + TestContextDecorator.SUFFIX));

    // Add @jsweet.lang.Erased annotation
    glex.replaceTemplate("cd2java.Annotations", res,
        new StringHookPoint("@jsweet.lang.Erased"));
    return res;
  }

  protected ASTCDClass createAbstractTestSetup(ASTCDCompilationUnit origCD) {
    String cdName = origCD.getCDDefinition().getName();

    ASTCDClass res = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC_ABSTRACT.build())
        .setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage(cdName + SUFFIX))
        .setName("Abstract" + cdName + SUFFIX)
        .build();

    // Add @jsweet.lang.Erased annotation
    glex.replaceTemplate("cd2java.Annotations", res,
        new StringHookPoint("@jsweet.lang.Erased"));

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
    String numOfClientsTemplate = "umlp.test_infrastructure.NumberOfClientsConstructor";

    cd4C.addConstructor(res, basicTemplate,
                        typeName,
                        List.of(genSummary.clientSetupAttr()));
    //support legacy calls where constructor is called with only an int describing the number of clients
    cd4C.addConstructor(res, numOfClientsTemplate,
                        typeName, fakeNumberOfClientsAttr);

    // common methods
    cd4C.addMethod(res, "umlp.test_infrastructure.GetClientName", cdName);
    cd4C.addMethod(res, "umlp.test_infrastructure.GetNumberOfClients");
    cd4C.addMethod(res, "umlp.test_infrastructure.Init", cdName);
    cd4C.addMethod(res, "umlp.test_infrastructure.HasDatabase");

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

    // Add @jsweet.lang.Erased annotation
    glex.replaceTemplate("cd2java.Annotations", res,
        new StringHookPoint("@jsweet.lang.Erased"));

    CD4C cd4C = getCD4C();

    // constructor
    addSuperConstructors(res, parentClass);

    // methods
    cd4C.addMethod(res, "umlp.test_infrastructure.mockTestSetup.Init", cdName);
    cd4C.addMethod(res, "umlp.test_infrastructure.mockTestSetup.HasDatabase", cdName);

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

    // Add @jsweet.lang.Erased annotation
    glex.replaceTemplate("cd2java.Annotations", res,
        new StringHookPoint("@jsweet.lang.Erased"));

    // constructor
    addSuperConstructors(res, parentClass);

    // methods
    CD4C cd4C = getCD4C();
    cd4C.addMethod(res, "umlp.test_infrastructure.dbTestSetup.Cleanup", cdName);
    cd4C.addMethod(res, "umlp.test_infrastructure.dbTestSetup.HasDatabase", cdName);
    cd4C.addMethod(res, "umlp.test_infrastructure.dbTestSetup.Init", cdName);

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

    // Add @jsweet.lang.Erased annotation
    glex.replaceTemplate("cd2java.Annotations", res,
        new StringHookPoint("@jsweet.lang.Erased"));

    CD4C cd4C = getCD4C();

    // constructor
    addSuperConstructors(res, parentClass);

    // methods
    cd4C.addMethod(res, "umlp.test_infrastructure.dbTestSetup.sql.Init", cdName);

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

    // Add @jsweet.lang.Erased annotation
    glex.replaceTemplate("cd2java.Annotations", res,
        new StringHookPoint("@jsweet.lang.Erased"));

    CD4C cd4C = getCD4C();

    // constructor
    addSuperConstructors(res, parentClass);

    // attributes
    addAttribute(res, "protected Neo4jDatabaseStarter dbStarter", " = new Neo4jDatabaseStarter()");
    cd4C.addImport(res, "umlp.backendrte.common.Neo4jDatabaseStarter");
    cd4C.addImport(res, "java.nio.file.Paths");
    cd4C.addImport(res, "java.nio.file.Path");

    // methods
    cd4C.addMethod(res, "umlp.test_infrastructure.dbTestSetup.neo4j.Init", cdName);
    cd4C.addMethod(res, "umlp.test_infrastructure.dbTestSetup.neo4j.Neo4jCleanup", cdName);
    cd4C.addMethod(res, "umlp.test_infrastructure.dbTestSetup.neo4j.GetDBPath", cdName);

    addImports(origCD, res);
    return res;
  }

  protected void addImports(ASTCDCompilationUnit origCD, ASTCDType type){
    String name = origCD.getCDDefinition().getName();

    CD4C cd4C = getCD4C();
    Joiner dot = Joiners.DOT;
    String backendRTECommandPath = PathUtil.getBackendRTECommandPath();
    String backendRTECommonPath = PathUtil.getBackendRTECommonPath();
    String backendRTETestInfrastructurePath = PathUtil.getTestInfrastructurePath();
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

    cd4C.addImport(type, dot.join(backendRTECommonPath, "DatabaseAccessManager4Neo4j"));
    cd4C.addImport(type, dot.join(backendRTECommonPath, "DatabaseAccessManager4SQL"));
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
