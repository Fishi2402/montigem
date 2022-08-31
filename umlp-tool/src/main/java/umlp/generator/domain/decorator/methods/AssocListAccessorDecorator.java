package umlp.generator.domain.decorator.methods;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.prettyprint.MCCollectionTypesFullPrettyPrinter;
import de.se_rwth.commons.StringTransformations;

import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class AssocListAccessorDecorator extends MandatoryAccessorDecorator {

  public AssocListAccessorDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methodList = Lists.newArrayList();

    // generate getX-Method
    String name = String.format(GET, StringTransformations.capitalize(service.getNativeAttributeName(ast.getName())));
    if (ast.getMCType() instanceof ASTMCGenericType) {
      String type = (new CdUtilsPrinter()).printType(((ASTMCGenericType) ast.getMCType()).getMCTypeArgument(0).getMCTypeOpt().get());
      ASTCDParameter param = getCDParameterFacade().createParameter("int", "index");
      ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), type, name, param);
      this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("umlp.methods.AssocGet", ast));
      methodList.add(method);
    }

    // generate getXGem-Method
    ASTCDMethod method2 = createGetter(ast);
    method2.setName(method2.getName() + "GemList");
    methodList.add(method2);

    return methodList;
  }

}
