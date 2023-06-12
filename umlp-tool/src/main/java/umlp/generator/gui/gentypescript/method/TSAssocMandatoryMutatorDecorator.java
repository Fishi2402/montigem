/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.method;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.domain.decorator.methods.AssocMandatoryMutatorDecorator;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class TSAssocMandatoryMutatorDecorator extends AssocMandatoryMutatorDecorator {

  public TSAssocMandatoryMutatorDecorator(
      GlobalExtensionManagement glex, CDGenService service, ASTCDType domainAst) {
    super(glex, service, domainAst);
  }

  @Override
  protected ASTCDMethod createSetter(final ASTCDAttribute ast) {
    String name =
        String.format(SET, StringTransformations.capitalize(
            service.getNativeAttributeName(ast.getName())
        ));
    ASTCDMethod method =
        this.getCDMethodFacade()
            .createMethod(PUBLIC.build(), name + "1", this.getCDParameterFacade().createParameters(ast));

    glex.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("this." + name + "("
        + StringTransformations.uncapitalize(ast.getName()) + ", NotificationScope.ALL);"));
    return method;
  }

  @Override
  protected ASTCDMethod createGemSetter(ASTCDAttribute ast) {
    String name = String.format(SET, StringTransformations.capitalize(
        service.getNativeAttributeName(ast.getName())+"Gem"));
    ASTCDMethod method3 = this.getCDMethodFacade().createMethod(PUBLIC.build(), name + "1", this.getCDParameterFacade().createParameters(ast));
    glex.replaceTemplate(EMPTY_BODY, method3, new StringHookPoint("this." + name + "("
        + StringTransformations.uncapitalize(ast.getName()) + ", NotificationScope.ALL);"));
    return method3;
  }

  @Override
  protected String getGemNotificationSetterTemplate() {
    return "umlp.gui2ts.methods.TSGemNotificationSetter";
  }

  @Override
  protected String getInternalSetOneTemplate() {
    return "umlp.gui2ts.methods.assocs.TSInternalSetOne";
  }

  @Override
  protected String getNotificationSetterTemplate() {
    return "umlp.gui2ts.methods.TSNotificationSetter";
  }

}
