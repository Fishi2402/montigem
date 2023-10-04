/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.method;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import umlp.generator.domain.decorator.methods.UMLPMandatoryMutatorDecorator;

import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

public class TSSimpleMethodMutatorDecorator extends UMLPMandatoryMutatorDecorator {

  public TSSimpleMethodMutatorDecorator(
      GlobalExtensionManagement glex, CDGenService service, ASTCDType ast) {
    super(glex, service, ast);
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methodList = Lists.newArrayList();
    if (!ast.getModifier().isDerived() && !ast.getModifier().isReadonly()) {
      // No default setter
      // methodList.add(createSetter(ast));
      methodList.add(createNotificationSetter(ast, false));
    }
    return methodList;
  }

  @Override
  protected ASTCDMethod createNotificationSetter(ASTCDAttribute ast, boolean isGem) {
    ASTCDMethod method = super.createSetter(ast);
    // TODO AGe: does "scope?" work?
    method.addCDParameter(getCDParameterFacade().createParameter(
        MCTypeFacade.getInstance().createOptionalTypeOf("NotificationScope"), "scope?"));
    glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(getNotificationSetterTemplate(),
        this.ast.getName(), service.getNativeAttributeName(ast.getName()), new CdUtilsPrinter().printType(ast.getMCType()), isGem));
    return method;
  }

  @Override
  protected String getNotificationSetterTemplate() {
    return "umlp.gui2ts.methods.TSNotificationSetter";
  }

}
