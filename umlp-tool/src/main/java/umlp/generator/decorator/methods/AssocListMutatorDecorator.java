package umlp.generator.decorator.methods;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.se_rwth.commons.StringTransformations;

import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class AssocListMutatorDecorator extends SimpleMethodMutatorDecorator {

  public AssocListMutatorDecorator(GlobalExtensionManagement glex, ASTCDType domainAst) {
    super(glex, domainAst);
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methodList = Lists.newArrayList();

    // generate getX-Method
    String name = String.format(SET, StringTransformations.capitalize(service.getNativeAttributeName(ast.getName())));
    if (ast.getMCType() instanceof ASTMCGenericType) {
      System.out.println();
      String type = (new CdUtilsPrinter()).printType(((ASTMCGenericType) ast.getMCType()).getMCTypeArgument(0).getMCTypeOpt().get());
      ASTCDParameter param1 = getCDParameterFacade().createParameter("int", "idx");
      ASTCDParameter param2 = getCDParameterFacade().createParameter(type, "o");
      ASTCDParameter param3 = getCDParameterFacade().createParameter("NotificationScope", "scope");
      ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), type, name, param1, param2, param3);
      this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("umlp.methods.AssocSet", ast.getName(), domainAst.getName()));
      methodList.add(method);
    }


    return methodList;
  }

}
