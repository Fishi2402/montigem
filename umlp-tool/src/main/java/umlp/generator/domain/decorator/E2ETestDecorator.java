package umlp.generator.domain.decorator;

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

import static de.monticore.cd.codegen.CD2JavaTemplates.JAVADOC;
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

    glex.replaceTemplate(JAVADOC, res, new StringHookPoint(
        //@formatter:off
        "/**\n" +
        " * Can be extended by E2E Tests.\n" +
        " * <p>\n" +
        " * Please note that the Frontend will not be restarted in between the tests, but the \n" +
        " * backend will be restarted before every test.\n" +
        " * In order to change the latter adjust the {@link DirtiesContext} annotation.\n" +
        " */"
        //@formatter:on
    ));

    CD4C cd4C = getCD4C();

    cd4C.addImport(res, "umlp.test_rte.playwright.AbstractPlaywrightE2ETest");
    cd4C.addImport(res, Joiners.DOT.join(packageName, cdName + "ServerApplication"));

    //add spring boot annotations
    cd4C.addImport(res, "org.springframework.boot.test.context.SpringBootTest");
    cd4C.addImport(res, "org.springframework.context.annotation.Import");

    setAnnotations(res,
                   new TemplateHookPoint("umlp.test_infrastructure.e2e.Annotations",
                                         cdName,
                                         portConfigClassName));

    var waitForBackendMethod = cd4C.addMethod(res, "umlp.test_infrastructure.e2e.WaitForBackend", cdName);
    glex.replaceTemplate(JAVADOC, waitForBackendMethod, new StringHookPoint(
        //@formatter:off
        "/**\n" +
        " * Waits until all commands have reached the backend and should have been processed.\n" +
        " */"
        //@formatter:on
    ));

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
    cd4C.addImport(res, "umlp.test_rte.playwright.AbstractPlaywrightE2ETest");
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
