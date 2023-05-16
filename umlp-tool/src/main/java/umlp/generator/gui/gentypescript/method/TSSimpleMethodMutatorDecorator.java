/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.method;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.methods.SimpleMethodMutatorDecorator;

public class TSSimpleMethodMutatorDecorator extends SimpleMethodMutatorDecorator {

  public TSSimpleMethodMutatorDecorator(
      GlobalExtensionManagement glex, CDGenService service, ASTCDType ast) {
    super(glex, service, ast);
  }

  @Override
  protected String getNotificationSetterTemplate() {
    return "umlp.gui2ts.methods.TSNotificationSetter";
  }

}
