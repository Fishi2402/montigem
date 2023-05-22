/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.method;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.accessor.OptionalAccessorDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class TSOptionalAccessorDecorator extends OptionalAccessorDecorator {

  public TSOptionalAccessorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public TSOptionalAccessorDecorator(
      final GlobalExtensionManagement glex, final CDGenService service) {
    super(glex, service);
  }

  protected ASTCDMethod createGetMethod(final ASTCDAttribute ast) {
    String name = String.format(GET, nativeAttributeName);
    ASTMCType type = service.getFirstTypeArgument(ast.getMCType()).deepClone();
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), type, name);
    String generatedErrorCode = service.getGeneratedErrorCode(ast.getName() + ast.printType());
    this.replaceTemplate(
        EMPTY_BODY,
        method,
        new TemplateHookPoint("umlp.gui2ts.methods.TSGet4Opt", ast, nativeAttributeName, generatedErrorCode));
    method.getModifier().setAbstract(ast.getModifier().isDerived());
    return method;
  }

  protected ASTCDMethod createIsPresentMethod(final ASTCDAttribute ast) {
    String name = String.format(IS_PRESENT, nativeAttributeName);
    ASTCDMethod method =
        this.getCDMethodFacade()
            .createMethod(PUBLIC.build(), getMCTypeFacade().createBooleanType(), name);
    this.replaceTemplate(
        EMPTY_BODY, method, new TemplateHookPoint("umlp.gui2ts.methods.TSIsPresent4Opt", ast));
    return method;
  }
}
