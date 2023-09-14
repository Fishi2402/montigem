/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.domain.decorator.DeSerDecorator;
import umlp.generator.domain.decorator.commands.CreateDecorator;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.util.PathUtil;

public class TSCreateDecorator extends CreateDecorator {

  public TSCreateDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected String extendedClass() {
    return "Command<void>";
  }

  @Override
  protected ASTCDAttribute addDomainAttr(ASTCDClass ast, String classname) {
    return getCD4C().addAttribute(ast, "protected "
        + classname + " " + StringTransformations.uncapitalize(classname));
  }

  @Override
  protected void addConstructor(ASTCDClass ast, String classname, String packageName) {
    glex.replaceTemplate(GUI2ComponentTemplates.CONSTRUCTOR, ast,
        new TemplateHookPoint(getConstructorTemplate(), classname, packageName, SUFFIX));
  }

  @Override
  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.gui2ts.commands.TSGetEffectedGemIds";
  }

  @Override
  protected String getDoRunTemplate() {
    return "umlp.gui2ts.commands.create.TSDoRun";
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.TSConstructor";
  }

  @Override
  protected String getBuildJsonTemplate() {
    return "umlp.gui2ts.commands.deser.TSBuildJson";
  }

  @Override
  protected String getFromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.deser.TSFromJsonObject";
  }

  @Override
  protected void addDeSerImport(ASTCDClass ast, ASTCDCompilationUnit origCD,
      ASTCDClass domainClass, String cmdName) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + domainClass.getName());
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + domainClass.getName() + DeSerDecorator.SUFFIX);
    getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE) + "." + cmdName);
  }

  @Override
  protected void addImports(ASTCDCompilationUnit origCD, ASTCDClass domainClass, ASTCDClass ast,
      String classname) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+ classname);
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+ getCorrectManager(domainClass));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("StatusResult"));
  }
}
