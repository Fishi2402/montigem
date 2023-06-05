/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import umlp.generator.domain.decorator.commands.GetFilteredDecorator;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.util.PathUtil;

public class TSGetFilteredDecorator extends GetFilteredDecorator {

  public TSGetFilteredDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected String extendedClass() {
    return "Request<number>";
  }

  @Override
  protected void addConstructor(ASTCDClass ast, String classname, String packageName) {
    glex.replaceTemplate(GUI2ComponentTemplates.CONSTRUCTOR, ast,
        new TemplateHookPoint(getConstructorTemplate(), classname, "", SUFFIX, packageName));
  }

  @Override
  protected String getDoRunTemplate() {
    return "umlp.gui2ts.commands.get_filtered.TSDoRun";
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.get_filtered.TSConstructor";
  }

  @Override
  protected String getGetFiltered_BuildJsonTemplate() {
    return "umlp.gui2ts.commands.deser.TSGetFiltered_BuildJson";
  }

  @Override
  protected String getGetFiltered_FromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.deser.TSGetFiltered_FromJsonObject";
  }

  @Override
  protected void addDeSerImport(ASTCDCompilationUnit origCD, ASTCDClass ast, String cmdName) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE) + "." + cmdName);
  }
}
