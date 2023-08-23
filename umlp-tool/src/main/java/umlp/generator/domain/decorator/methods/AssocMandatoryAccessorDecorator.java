/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.methods;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.se_rwth.commons.StringTransformations;

import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class AssocMandatoryAccessorDecorator extends MandatoryAccessorDecorator {

  public AssocMandatoryAccessorDecorator(GlobalExtensionManagement glex,
                                         CDGenService service) {
    super(glex, service);
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methodList = Lists.newArrayList();

    // generate getX-Method
    String name = String.format(GET, StringTransformations.capitalize(service.getNativeAttributeName(ast.getName())));
    ASTMCType type = ast.getMCType();
    if (ast.getMCType() instanceof ASTMCGenericType) {
      type = ((ASTMCGenericType) ast.getMCType()).getMCTypeArgument(0).getMCTypeOpt().get().deepClone();
    }
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), type, name);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(getGetTemplate(), ast));
    methodList.add(method);

    // generate getXGem-Method
    ASTCDMethod method2 = createGetter(ast);
    method2.setName(method2.getName() + "Gem");
    methodList.add(method2);

    return methodList;
  }

  protected String getGetTemplate() {
    return "umlp.methods.Get";
  }

}
