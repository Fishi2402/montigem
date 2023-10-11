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

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class ServerApplicationDecorator extends AbstractDecorator {

  public static final String SUFFIX = "ServerApplication";
  private boolean isNeo4jGen;

  public ServerApplicationDecorator(GlobalExtensionManagement glex, boolean isNeo4jGen) {
    super(glex);
    this.isNeo4jGen = isNeo4jGen;
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(withOrigin(createClass(origCD)));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();
    ASTCDClass serverApplicationClass = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(name + SUFFIX)
        .build();

    // methods
    getCD4C().addMethod(serverApplicationClass, "umlp.serverapplication.main", name);
    glex.replaceTemplate("cd2java.Annotations", serverApplicationClass, new StringHookPoint("@SpringBootApplication(\n" +
        "    // We have our own data management\n" +
        "    exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},\n" +
        "    scanBasePackages={\"umlp.backendrte.service.websocket\", \"umlp.backendrte.service.rest\", \"" +origCD.getCDDefinition().getName().toLowerCase() +
        ".service\"}\n" +
        ")"));

    ASTCDMethodSignature initMethod = isNeo4jGen? getCD4C().addMethod(serverApplicationClass, "umlp.serverapplication.initNeo4j", name)
        : getCD4C().addMethod(serverApplicationClass, "umlp.serverapplication.init", name);
    glex.replaceTemplate("cd2java.Annotations", initMethod, new StringHookPoint("@javax.annotation.PostConstruct "));

    // services
    var sigNoAuth = getCD4C().addMethod(serverApplicationClass, "umlp.serverapplication.NoAuthWebsocketService");
    var sigAuth = getCD4C().addMethod(serverApplicationClass, "umlp.serverapplication.AuthWebsocketService");

    glex.replaceTemplate("cd2java.Annotations", sigNoAuth, new StringHookPoint(
      "@Bean\n@ConditionalOnProperty(name = \"umlp.service.websocket.impl\", havingValue = \"noAuth\", matchIfMissing = true)"));
    glex.replaceTemplate("cd2java.Annotations", sigAuth, new StringHookPoint(
      "@Bean\n@ConditionalOnProperty(name = \"umlp.service.websocket.impl\", havingValue = \"auth\")"));

    // Imports
    addImports(serverApplicationClass);
    return serverApplicationClass;
  }

  protected void addImports(ASTCDClass clazz) {
    getCD4C().addImport(clazz, "org.springframework.context.annotation.Bean");
    getCD4C().addImport(clazz, "org.springframework.boot.autoconfigure.condition.ConditionalOnProperty");
    getCD4C().addImport(clazz, PathUtil.getBackendRTEPath("service", "websocket", "CommandWebSocketService"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTEPath("service", "websocket", "CommandWebSocketNoAuthService"));

    getCD4C().addImport(clazz, "de.monticore.io.FileReaderWriter");
    getCD4C().addImport(clazz, "de.se_rwth.commons.logging.Log");
    getCD4C().addImport(clazz, "org.h2.tools.Server");
    getCD4C().addImport(clazz, "org.springframework.boot.SpringApplication");
    getCD4C().addImport(clazz, "org.springframework.boot.autoconfigure.SpringBootApplication");
    getCD4C().addImport(clazz, "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration");
    getCD4C().addImport(clazz, "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration");
    getCD4C().addImport(clazz, "java.io.IOException");
    getCD4C().addImport(clazz, "java.net.URL");
    getCD4C().addImport(clazz, "java.sql.SQLException");
    getCD4C().addImport(clazz, "java.util.Optional");

    if(isNeo4jGen) {
      getCD4C().addImport(clazz, "umlp.backendrte.common.DatabaseAccessManager4Neo4j");
    } else {
      getCD4C().addImport(clazz, "umlp.backendrte.common.DatabaseAccessManager4SQL");
    }
  }

}
