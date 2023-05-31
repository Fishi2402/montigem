/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.domain.decorator.commands.AssocCommandDecorator;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.util.PathUtil;

public class TSAssocCommandDecorator extends AssocCommandDecorator {

  public TSAssocCommandDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  protected void addConstructor(
      ASTCDClass ast, String template, String classname, String sourceName, String targetName
  ) {
    glex.replaceTemplate(GUI2ComponentTemplates.CONSTRUCTOR, ast,
        new TemplateHookPoint(template, classname, COMMAND_ASSOC_PACKAGE,
            sourceName, targetName));
  }

  @Override
  protected String extendedClass() {
    return "Command<void>";
  }

  @Override
  protected String getAdd_DoRunTemplate() {
    return "umlp.gui2ts.commands.assocs.TSAdd_DoRun";
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.assocs.TSConstructor";
  }

  @Override
  protected String getRemove_DoRunTemplate() {
    return "umlp.gui2ts.commands.assocs.TSRemove_DoRun";
  }

  @Override
  protected String getSet_ConstructorTemplate() {
    return "umlp.gui2ts.commands.assocs.TSSet_Constructor";
  }

  @Override
  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.gui2ts.commands.TSGetEffectedGemIds";
  }

  @Override
  protected String getSet_Source_ExchangeTemplate() {
    return "umlp.gui2ts.commands.assocs.TSSet_Source_Exchange";
  }

  @Override
  protected String getSet_DoRunTemplate() {
    return "umlp.gui2ts.commands.assocs.TSSet_DoRun";
  }

  @Override
  protected String getBuildJsonTemplate() {
    return "umlp.gui2ts.commands.assocs.TSBuildJson";
  }

  @Override
  protected String getFromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.assocs.TSFromJsonObject";
  }

  @Override
  protected void addImports(ASTCDCompilationUnit origCD, ASTCDClass ast, String sourceType, String targetType) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("StatusResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandManager"));
    getCD4C().addImport(ast, PathUtil.getBackendRTEUtilPath("LogUtil"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("GeneralErrorCollector"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + origCD.getCDDefinition().getName() + ManagerDecorator.SUFFIX);
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + sourceType);
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + targetType);
  }

  @Override
  protected void addDeSerImports(ASTCDCompilationUnit origCD, ASTCDClass ast, String cmdName) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    getCD4C().addImport(ast, getPackageName(origCD, COMMAND_ASSOC_PACKAGE) + "." + cmdName);
  }

}
