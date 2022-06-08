package umlp.generator.decorator;

import de.monticore.cd.codegen.methods.mutator.MandatoryMutatorDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

public class SimpleMethodMutatorDecorator extends MandatoryMutatorDecorator {

  public SimpleMethodMutatorDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected ASTCDMethod createSetter(ASTCDAttribute ast) {
    ASTCDMethod method = super.createSetter(ast);
    glex.replaceTemplate( EMPTY_BODY, method, new StringHookPoint("this." + method.getName() + "(o, NotificationScope.ALL);"));
    return method;
  }
}
