/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.method;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.methods.AssocMandatoryMutatorDecorator;

public class TSAssocMandatoryMutatorDecorator extends AssocMandatoryMutatorDecorator {

  public TSAssocMandatoryMutatorDecorator(
      GlobalExtensionManagement glex, CDGenService service, ASTCDType domainAst) {
    super(glex, service, domainAst);
  }

  @Override
  protected String getGemNotificationSetterTemplate() {
    return "umlp.gui2ts.methods.GemNotificationSetter";
  }

  @Override
  protected String getInternalSetOneTemplate() {
    return "umlp.gui2ts.methods.assocs.InternalSetOne";
  }

  @Override
  protected String getNotificationSetterTemplate() {
    return "umlp.gui2ts.methods.NotificationSetter";
  }

}
