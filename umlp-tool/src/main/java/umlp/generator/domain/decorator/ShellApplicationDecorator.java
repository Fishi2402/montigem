package umlp.generator.domain.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class ShellApplicationDecorator extends AbstractDecorator {

  public static final String SUFFIX = "ShellAPIApplication";

  public ShellApplicationDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(withOrigin(createClass(origCD)));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();
    ASTCDClass shellApplicationClass = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(name + SUFFIX)
        .build();

    // methods
    getCD4C().addMethod(shellApplicationClass, "umlp.shellapplication.shellApiMain.ftl", name);
    glex.replaceTemplate("cd2java.Annotations", shellApplicationClass,
        new StringHookPoint(
            "@SpringBootApplication(scanBasePackages={\"" + origCD.getCDDefinition().getName().toLowerCase() + ".shellapi\"})"
        ));

    // Imports
    addImports(shellApplicationClass);
    return shellApplicationClass;
  }

  protected void addImports(ASTCDClass clazz) {
    getCD4C().addImport(clazz,"de.se_rwth.commons.logging.Log");
    getCD4C().addImport(clazz,"org.springframework.boot.WebApplicationType");
    getCD4C().addImport(clazz,"org.springframework.boot.autoconfigure.SpringBootApplication");
    getCD4C().addImport(clazz,"org.springframework.boot.builder.SpringApplicationBuilder");
  }

}
