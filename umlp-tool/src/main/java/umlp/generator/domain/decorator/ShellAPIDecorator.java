/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDAttribute;
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
import static de.monticore.cd.facade.CDModifier.PUBLIC_STATIC;

public class ShellAPIDecorator extends AbstractDecorator {

  public static final String SUFFIX = "SHELLAPI";

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
        .setModifier(PUBLIC.build())
        .setName(name+SUFFIX)
        .build();
    glex.replaceTemplate("cd2java.Annotations", shellapi, new StringHookPoint("@ShellComponent"));

    addImports(origCD, shellapi);

    // Attributes
    addAttribute("url", "http://localhost", shellapi);
    addAttribute("port", "8081", shellapi);
    addAttribute("applicationName", name, shellapi);
    addAttribute("apiName", "api", shellapi);

    // Methods
    getCD4C().addMethod(shellapi, "umlp.shellapi.SendCommand");
    getCD4C().addMethod(shellapi, "umlp.shellapi.GetFreeGemId");
    addSetMethod(shellapi, name, "url");
    addSetMethod(shellapi, name, "port");
    addSetMethod(shellapi, name, "applicationName");
    addSetMethod(shellapi, name, "apiName");

    return shellapi;
  }

  private void addAttribute(String name, String value, ASTCDClass shellapiClass) {
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PUBLIC_STATIC.build(),
        "String", name);
    glex.replaceTemplate("cd2java.Value", attribute, new StringHookPoint(String.format("= \"%s\"", value)));
    shellapiClass.addCDMember(attribute);
  }

  private void addSetMethod(ASTCDClass shellapiClass, String name, String attributeName) {
    ASTCDMethodSignature m = getCD4C().addMethod(shellapiClass,
        "umlp.shellapi.SetProperty",
        name + SUFFIX, name, attributeName);
    String annotation = String.format("@ShellMethod(key = \"set-%s\", value = \"Set %s\")",
        attributeName, attributeName);
    glex.replaceTemplate("cd2java.Annotations", m, new StringHookPoint(annotation));
    addAnnotationToParameters(m, Arrays.asList("@ShellOption"));
  }

  private void addImports(ASTCDCompilationUnit origCD, ASTCDClass clazz) {
    getCD4C().addImport(clazz, "org.springframework.shell.standard.ShellComponent");
    getCD4C().addImport(clazz, "org.springframework.shell.standard.ShellMethod");
    getCD4C().addImport(clazz, "org.springframework.shell.standard.ShellOption");
    getCD4C().addImport(clazz, "org.springframework.http.HttpHeaders");
    getCD4C().addImport(clazz, "org.springframework.http.MediaType");
    getCD4C().addImport(clazz, "org.springframework.web.reactive.function.client.WebClient");
    getCD4C().addImport(clazz, getPackageName(origCD, REQUESTS_PACKAGE) + ".GetFreeGemIdRequest");
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandObjectMapper"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandResult"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("SimpleResult"));
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
