package umlp.generator.domain.decorator;

import de.monticore.ast.Comment;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;

/**
 * Generates an abstract class which can be extended by playwright E2E tests and a PortConfig class
 * as side product
 */
public class E2ETestDecorator extends AbstractDecorator {

  public static final String TEST_INFRASTRUCTURE_PACKAGE_NAME = "test_infrastructure";
  private static final String PLAYWRIGHT_TEST_CLASS_NAME = "AbstractPlaywrightE2ETest";

  public E2ETestDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage testInfrastructurePackage = getPackage(origCD,
                                                        decoratedCD,
                                                        TEST_INFRASTRUCTURE_PACKAGE_NAME);

    String cdName = origCD.getCDDefinition().getName();

    String e2eTestClassName = "Abstract" + cdName + "E2ETest";
    String portConfigClassName = cdName + "E2ETestPortConfig";

    testInfrastructurePackage.addCDElement(withOrigin(createAbstractE2ETest(origCD,
                                                                            e2eTestClassName,
                                                                            portConfigClassName)));
    testInfrastructurePackage.addCDElement(withOrigin(createPortConfig(e2eTestClassName,
                                                                       portConfigClassName)));
  }

  private ASTCDElement createAbstractE2ETest(ASTCDCompilationUnit origCD, String className, String portConfigClassName) {

    String cdName = origCD.getCDDefinition().getName();
    String packageName = getPackageName(origCD, DEFAULT_PACKAGE);

    ASTCDClass res = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC_ABSTRACT.build())
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(PLAYWRIGHT_TEST_CLASS_NAME))
        .setName(className)
        .build();

    CD4C cd4C = getCD4C();

    cd4C.addImport(res, "umlp.backendrte.test_infrastructure.playwright.AbstractPlaywrightE2ETest");
    cd4C.addImport(res, Joiners.DOT.join(packageName, cdName + "ServerApplication"));

    //add spring boot annotations
    cd4C.addImport(res, "org.springframework.boot.test.context.SpringBootTest");
    cd4C.addImport(res, "org.springframework.context.annotation.Import");
    cd4C.addImport(res, "org.springframework.test.annotation.DirtiesContext");

    setAnnotations(res,
                   new TemplateHookPoint("umlp.test_infrastructure.e2e.SpringAnnotations",
                                         cdName,
                                         portConfigClassName));

    //attributes
    addAttribute(res, "static int backendServerPort", "getFreePort()");
    addAttribute(res, "static int frontendServerPort", "getFreePort()");
    addAttribute(res, "static Optional<Process> frontendProcess", "Optional.empty()");

    //TODO reuse frontend, always restart backend -> Add docs on how to change the later behaviour


    //setup/tearDown methods
    cd4C.addImport(res, "org.junit.jupiter.api.BeforeAll");
    cd4C.addImport(res, "org.junit.jupiter.api.BeforeEach");
    cd4C.addImport(res, "org.junit.jupiter.api.AfterEach");
    cd4C.addImport(res, "org.junit.jupiter.api.AfterAll");
    cd4C.addImport(res, "umlp.backendrte.common.DatabaseAccessManager");
    cd4C.addImport(res, "java.io.IOException");
    cd4C.addImport(res, "java.util.Comparator");
    cd4C.addImport(res, "java.nio.file.Path");
    cd4C.addImport(res, "java.nio.file.Paths");
    cd4C.addImport(res, "java.nio.file.Files");
    cd4C.addImport(res, "java.io.File");

    //TODO split start frontend in smaller overridable methods
    cd4C.addMethod(res, "umlp.test_infrastructure.e2e.GetNpmPath");
    cd4C.addMethod(res, "umlp.test_infrastructure.e2e.GetFrontendGenDirectory");
    cd4C.addMethod(res, "umlp.test_infrastructure.e2e.StartNpmProcess");
    cd4C.addMethod(res, "umlp.test_infrastructure.e2e.WaitForFrontend");

    var stopFrontendMethod = cd4C.addMethod(res, "umlp.test_infrastructure.e2e.StopFrontend");
    setAnnotations(stopFrontendMethod, new StringHookPoint("@AfterAll"));
    var cleanupBackendMethod = cd4C.addMethod(res, "umlp.test_infrastructure.e2e.CleanupBackend");
    setAnnotations(cleanupBackendMethod, new StringHookPoint("@AfterEach"));

    var waitForBackendMethod = cd4C.addMethod(res, "umlp.test_infrastructure.e2e.WaitForBackend");
    waitForBackendMethod.add_PreComment(new Comment("/**\n" + " * Waits until all commands have reached the backend and should have been processed.\n" + " */"));

    cd4C.addMethod(res, "umlp.test_infrastructure.e2e.GetFrontendServerPort");

    return res;
  }

  private ASTCDElement createPortConfig(String e2eTestClassName, String className) {
    ASTCDClass res = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage(
            "WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>"))
        .setName(className)
        .build();

    CD4C cd4C = getCD4C();
    cd4C.addImport(res, "org.springframework.boot.web.server.WebServerFactoryCustomizer");
    cd4C.addImport(res,
                   "org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory");

    //add component annotation
    cd4C.addImport(res, "org.springframework.stereotype.Component");

    glex.replaceTemplate("cd2java.Annotations", res, new StringHookPoint("@Component"));

    cd4C.addMethod(res, "umlp.test_infrastructure.e2e.PortConfig_customize", e2eTestClassName);

    return res;
  }
}
