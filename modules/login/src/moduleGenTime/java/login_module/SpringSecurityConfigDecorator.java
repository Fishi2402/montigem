/* (c) https://github.com/MontiCore/monticore */

package login_module;

import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.util.PathUtil;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class SpringSecurityConfigDecorator extends AbstractDecorator {
  public static String SUFFIX = "SecurityConfig";

  public SpringSecurityConfigDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(withOrigin(createClass(origCD)));
  }

  protected ASTCDElement createClass(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();
    ASTCDClass ast = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(name + SUFFIX)
        .setCDExtendUsage(CDExtendUsageFacade.getInstance().createCDExtendUsage(
            "WebSecurityConfigurerAdapter"))
        .build();

    glex.replaceTemplate("cd2java.Annotations", ast,
        new StringHookPoint("@Configuration\n@EnableWebSecurity"));

    String managerName = name + ManagerDecorator.SUFFIX;
    String userclassName = "User";

    var m1 = getCD4C().addMethod(ast, "login_module.backend.security_config.CustomerUserDetailsService",
        managerName, userclassName);
    glex.replaceTemplate("cd2java.Annotations", m1, new StringHookPoint("@Bean"));

    var m2 = getCD4C().addMethod(ast, "login_module.backend.security_config.PasswordEncoder");
    glex.replaceTemplate("cd2java.Annotations", m2, new StringHookPoint("@Bean"));

    getCD4C().addMethod(ast, "login_module.backend.security_config.SendJsonResponse");

    addImports(ast);

    return ast;
  }

  protected void addImports(ASTCDClass ast) {
    getCD4C().addImport(ast, "java.util.Map");

    getCD4C().addImport(ast, "com.fasterxml.jackson.databind.ObjectMapper");
    getCD4C().addImport(ast, "org.springframework.context.annotation.Bean");
    getCD4C().addImport(ast, "org.springframework.context.annotation.Configuration");
    getCD4C().addImport(ast, "org.springframework.http.MediaType");
    getCD4C().addImport(ast, "org.springframework.security.config.annotation.web.configuration.EnableWebSecurity");
    getCD4C().addImport(ast, "org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter");
    getCD4C().addImport(ast, "org.springframework.security.core.authority.SimpleGrantedAuthority");
    getCD4C().addImport(ast, "org.springframework.security.core.userdetails.UserDetailsService");
    getCD4C().addImport(ast, "org.springframework.security.crypto.factory.PasswordEncoderFactories");
    getCD4C().addImport(ast, "org.springframework.security.crypto.password.PasswordEncoder");
    getCD4C().addImport(ast, "javax.servlet.http.HttpServletResponse");
    getCD4C().addImport(ast, "java.nio.charset.StandardCharsets");

    getCD4C().addImport(ast, "java.io.PrintWriter");
    getCD4C().addImport(ast, "java.io.IOException");

    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("GemList"));
  }
}
