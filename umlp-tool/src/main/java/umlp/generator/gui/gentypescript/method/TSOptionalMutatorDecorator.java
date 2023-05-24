/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.method;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.mutator.OptionalMutatorDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class TSOptionalMutatorDecorator extends OptionalMutatorDecorator {

  public TSOptionalMutatorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public TSOptionalMutatorDecorator(
      final GlobalExtensionManagement glex, final CDGenService service) {
    super(glex, service);
  }

  @Override
  protected ASTCDMethod createSetMethod(final ASTCDAttribute ast) {
    String name = String.format(SET, naiveAttributeName);
    ASTMCType parameterType = service.getFirstTypeArgument(ast.getMCType()).deepClone();
    ASTCDParameter parameter =
        this.getCDParameterFacade().createParameter(parameterType, ast.getName());
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), name, parameter);
    this.replaceTemplate(
        EMPTY_BODY, method, new TemplateHookPoint("umlp.gui2ts.methods.TSSet4Opt", ast, naiveAttributeName));
    return method;
  }

  @Override
  protected ASTCDMethod createSetAbsentMethod(final ASTCDAttribute ast) {
    String name = String.format(SET_ABSENT, naiveAttributeName);
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), name);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("umlp.gui2ts.methods.TSSetAbsent", ast));
    return method;
  }
}
