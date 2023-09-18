package umlp.generator.gui.gentypescript.commands;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import umlp.generator.domain.decorator.DeSerDecorator;
import umlp.generator.domain.decorator.commands.ManagerSubscriptionDecorator;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.util.PathUtil;

public class TSManagerSubscriptionDecorator extends ManagerSubscriptionDecorator {
  public TSManagerSubscriptionDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected void addImports(ASTCDCompilationUnit origCD, ASTCDClass domainClass, ASTCDClass ast, String classname) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("StatusResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandManager"));
  }

  @Override
  protected void addConstructor(ASTCDClass ast, String classname, String packageName) {
    glex.replaceTemplate(GUI2ComponentTemplates.CONSTRUCTOR, ast,
        new TemplateHookPoint(getConstructorTemplate(), classname, packageName));
  }

  @Override
  protected String getDoRunTemplate() {
    return "umlp.gui2ts.commands.subscribeManagerEvents.TSDoRun";
  }

  @Override
  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.gui2ts.commands.TSGetEffectedGemIds";
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.subscribeManagerEvents.TSConstructor";
  }

  @Override
  protected String extendedClass() {
    return "Command<void>";
  }

  @Override
  protected String getSubscribeManagerEvent_BuildJsonTemplate() {
    return "umlp.gui2ts.commands.deser.TSSubscribeManagerEvent_BuildJson";
  }

  @Override
  protected String getSubscribeManagerEvent_FromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.deser.TSSubscribeManagerEvent_FromJsonObject";
  }

  @Override
  protected void addDeSerImport(ASTCDClass ast, ASTCDCompilationUnit origCD, ASTCDClass domainClass, String cmdName) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + domainClass.getName());
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + domainClass.getName() + DeSerDecorator.SUFFIX);
    getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE) + "." + cmdName);
  }
}
