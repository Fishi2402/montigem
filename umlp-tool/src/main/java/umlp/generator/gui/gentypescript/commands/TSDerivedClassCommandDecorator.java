/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.domain.decorator.commands.AssocCommandDecorator;
import umlp.generator.domain.decorator.commands.DerivedClassCommandDecorator;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.util.PathUtil;

public class TSDerivedClassCommandDecorator extends DerivedClassCommandDecorator {

  public TSDerivedClassCommandDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }


  protected void addConstructor(
      ASTCDClass ast, String template, String classname, String packageName, String sourceName, String targetName
                               ) {
    glex.replaceTemplate(GUI2ComponentTemplates.CONSTRUCTOR, ast,
        new TemplateHookPoint(template, classname, packageName,
            sourceName, targetName));
  }

  protected String cleanupType(ASTCDCompilationUnit origCD, String type) {
    // Remove the "[pck.]DtCockpit." prefix
    String pckName = getPackageName(origCD, DEFAULT_PACKAGE);
    if (type.toLowerCase().startsWith(pckName))
      return type.substring(pckName.length()+1);

    return super.cleanupType(origCD,type);
  }

  protected ASTCDClass createGetMethod(ASTCDCompilationUnit origCD, ASTCDClass domainClass,
      String attrName,
      String type,
      String packageName,
      boolean isList) {

    // Remove the "DtCockpit." prefix
//    if (type.startsWith(origCD.getCDDefinition().getName()))
//      cleanedUpType = cleanedUpType.substring(origCD.getCDDefinition().getName().length()+1);

    ASTCDClass ast = super.createGetMethod(origCD, domainClass, attrName,type,
        packageName, isList);
    // Ugly here
    if (type.contains(".") && !type.startsWith("java.lang"))
      getCD4C().addImport(ast, type);


    return ast;
  }

  @Override
  protected String boxType(String type) {
    if (type.equals("int") || type.equals("long") || type.equals("java.lang.int") || type.equals("java.lang.long"))
      return "number";
    if (type.equals("String") || type.equals("java.lang.String"))
      return "string";
    return type;
  }

  @Override
  protected String getAssocsCheckAccessPolicyTemplate() {
    return "umlp.gui2ts.commands.derivedclass.TSAssocsCheckAccessPolicy";
  }

  @Override
  protected String getAdd_DoRunTemplate() {
    return "umlp.gui2ts.commands.derivedclass.TSGet_DoAction";
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.derivedclass.TSConstructor";
  }

  protected String getFromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.assocs.TSFromJsonObject";
  }

  @Override
  protected String getBuildJsonTemplate() {
    return "umlp.gui2ts.commands.assocs.TSBuildJson";
  }

  @Override
  protected void addImports(ASTCDCompilationUnit origCD, ASTCDClass ast) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("SimpleResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("ListResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("Request"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandManager"));
    getCD4C().addImport(ast, PathUtil.getBackendRTEUtilPath("LogUtil"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("GeneralErrorCollector"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + origCD.getCDDefinition().getName() + ManagerDecorator.SUFFIX);
  }

  @Override
  protected void addDeSerImports(ASTCDCompilationUnit origCD, ASTCDClass ast, String cmdName) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    getCD4C().addImport(ast, getPackageName(origCD, COMMAND_DERIVED_CLASS_PACKAGE) + "." + cmdName);
  }
}
