/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.domain.decorator.commands.AggregatedCommandDeSerDecorator;
import umlp.generator.domain.decorator.requests.GetFreeGemIdRequestDecorator;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;

import java.util.List;

public class TSAggregatedCommandDeSerDecorator extends AggregatedCommandDeSerDecorator {

  protected String astcdPackage;

  public TSAggregatedCommandDeSerDecorator(GlobalExtensionManagement glex, List<ASTCDClass> deSers) {
    super(glex, deSers);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE).getName();
    super.transform(origCD, decoratedCD);
  }

  @Override
  protected void addConstructor(ASTCDClass ast, String classname, String packageName,
      List<String> commandList) {
    addCommandImports(ast, commandList);
    glex.replaceTemplate(GUI2ComponentTemplates.CONSTRUCTOR, ast,
        new TemplateHookPoint(getConstructorTemplate(), commandList, classname, packageName));
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.aggregatedcommanddeser.TSConstructor";
  }

  @Override
  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
  }

  protected void addCommandImports(ASTCDClass ast, List<String> commandList) {
    getCD4C().addImport(ast, this.astcdPackage + "."
        + REQUESTS_PACKAGE + "." + GetFreeGemIdRequestDecorator.CLASS_NAME + "DeSer");
    for (String command : commandList) {
      getCD4C().addImport(ast, command + "DeSer");
    }
  }

}
