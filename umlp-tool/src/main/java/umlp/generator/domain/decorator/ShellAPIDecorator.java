/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class ShellAPIDecorator extends AbstractDecorator {

  public static final String SUFFIX = "ShellAPI";

  public ShellAPIDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, SHELLAPI_PACKAGE);
    astcdPackage.addCDElement(createClass(origCD));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass shellapi = UMLPMill.cDClassBuilder()
        .setName(name+SUFFIX)
        .setModifier(PUBLIC.build())
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(PathUtil.getBackendRTEShellApiPath("UMLPShellAPI")))
        .build();
    glex.replaceTemplate("cd2java.Annotations", shellapi, new StringHookPoint("@ShellComponent"));

    addImports(origCD, shellapi);

    // Methods
    ASTCDMethodSignature init = getCD4C().addMethod(shellapi, "umlp.shellapi.Init");
    String annotation = "@ShellMethod(key = \"init\", value = \"Initialize the connection to the server\")";
    glex.replaceTemplate("cd2java.Annotations", init, new StringHookPoint(annotation));
    addAnnotationToParameters(init, Arrays.asList(
        "@ShellOption(value = \"--url\", defaultValue = \"localhost\")",
        "@ShellOption(value = \"--port\", defaultValue = \"8081\")",
        String.format("@ShellOption(value = \"--appName\", defaultValue = \"%s\")", name.toLowerCase()),
        "@ShellOption(value = \"--api\", defaultValue = \"command\")",
        "@ShellOption(value = \"--user\", defaultValue = \"\")",
        "@ShellOption(value = \"--password\", defaultValue = \"\")",
        "@ShellOption(value = \"scheme\", defaultValue = \"ws\")"
    ));

    return shellapi;
  }

  private void addImports(ASTCDCompilationUnit origCD, ASTCDClass clazz) {
    getCD4C().addImport(clazz, "org.springframework.shell.standard.ShellComponent");
    getCD4C().addImport(clazz, "org.springframework.shell.standard.ShellMethod");
    getCD4C().addImport(clazz, "org.springframework.shell.standard.ShellOption");
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandResult"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("SimpleResult"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("LoadingState"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTEShellApiPath("UMLPShellAPI"));
  }

  private void addAnnotationToParameters(ASTCDMethodSignature m, List<String> annotations) {
    if (annotations.size() != m.getCDParameterList().size()) {
      return;
    }
    for (int i = 0; i < m.getCDParameterList().size(); i++) {
      m.getCDParameter(i).setAnnotation(Optional.of(annotations.get(i)));
    }
  }
}
