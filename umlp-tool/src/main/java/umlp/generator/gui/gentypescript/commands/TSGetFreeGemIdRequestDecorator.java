/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import umlp.generator.domain.decorator.commands.GetFreeGemIdRequestDecorator;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.util.PathUtil;

public class TSGetFreeGemIdRequestDecorator extends GetFreeGemIdRequestDecorator {

  public TSGetFreeGemIdRequestDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected String getDoRunTemplate() {
    return "umlp.gui2ts.commands.getfreegemid.TSDoRun";
  }

  @Override
  protected String extendedClass() {
    return "Request<number>";
  }

  @Override
  protected void addConstructor(ASTCDClass ast, ASTCDCompilationUnit origCD) {
    glex.replaceTemplate(GUI2ComponentTemplates.CONSTRUCTOR, ast,
        new TemplateHookPoint(getConstructorTemplate(), CLASS_NAME,
            getPackageName(origCD, DEFAULT_PACKAGE)));
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.getfreegemid.TSConstructor";
  }

  @Override
  protected String getFromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.getfreegemid.TSFromJsonObject";
  }

  @Override
  protected void addDeSerImport(ASTCDClass ast, ASTCDCompilationUnit origCD) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE) + "." + CLASS_NAME);
  }

}
