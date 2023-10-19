/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.commands.AddDecorator;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.util.PathUtil;

import java.util.Set;

public class TSAddDecorator extends AddDecorator {

  public TSAddDecorator(GlobalExtensionManagement glex, Set<String> supportedDeSers) {
    super(glex, supportedDeSers);
  }

  @Override
  protected void addConstructor(ASTCDClass ast, String classname, String packageName,
      String attrName, String type) {
    glex.replaceTemplate(GUI2ComponentTemplates.CONSTRUCTOR, ast,
        new TemplateHookPoint(getConstructorTemplate(), classname, packageName, attrName, type));
  }

  @Override
  protected void addJavaImport(ASTCDClass ast, SymTypeExpression type) {
    // don't
  }

  @Override
  protected String extendedClass() {
    return "Command<void>";
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.add.TSConstructor";
  }

  @Override
  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.gui2ts.commands.TSGetEffectedGemIds";
  }

  @Override
  protected String getDoRunTemplate() {
    return "umlp.gui2ts.commands.add.TSDoRun";
  }

  @Override
  protected String getAdd_BuildJsonTemplate() {
    return "umlp.gui2ts.commands.deser.TSAdd_BuildJson";
  }

  @Override
  protected String getAdd_FromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.deser.TSAdd_FromJsonObject";
  }

  @Override
  protected String getGeneralCheckAccessPolicyTemplate() {
    return "umlp.gui2ts.accesspolicy.TSGeneralCheckAccessPolicy";
  }

  @Override
  protected void addDeSerImport(ASTCDClass ast, ASTCDCompilationUnit origCD,
      ASTCDType domainClass, SymTypeExpression type, SymTypeExpression innerType, String cmdName) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + domainClass.getName());
    getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE) + "." + cmdName);
    if (GenService.isCDEnum(innerType)) {
      getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + ((SymTypeOfGenerics)type).getArgument(0).print());
    }
  }

  @Override
  protected void addImports(ASTCDCompilationUnit origCD, ASTCDType domainClass, ASTCDClass ast) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("StatusResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(ast, PathUtil.getBackendRTEUtilPath("LogUtil"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("GeneralErrorCollector"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + domainClass.getName());
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + getCorrectManager(domainClass));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+ "." + domainClass.getName() + "AccessPolicy");
  }
}
