package umlp.generator.domain.decorator.methods;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.se_rwth.commons.StringTransformations;

import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class AssocListAccessorDecorator extends MandatoryAccessorDecorator {

  protected final ASTCDType ast;

  protected final CD4C cd4c = CD4C.getInstance();

  public AssocListAccessorDecorator(GlobalExtensionManagement glex, ASTCDType ast) {
    super(glex);
    this.ast = ast;
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methodList = Lists.newArrayList();

    String targetName = (new CdUtilsPrinter()).printType(((ASTMCGenericType) ast.getMCType()).getMCTypeArgument(0).getMCTypeOpt().get());

    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.methods.assocs.Contains", ast.getName(), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.methods.assocs.ContainsAll", ast.getName(), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.methods.assocs.Iterator", ast.getName(), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.methods.assocs.Size", ast.getName()).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.methods.assocs.SubList", ast.getName(), targetName).get());

    // generate getX-Method
    methodList.add(createGetMethod(ast));

    // generate getXGem-Method
    methodList.add(createGetGemList(ast));

    return methodList;
  }

  protected ASTCDMethod createGetMethod(ASTCDAttribute ast) {
    String name = String.format(GET, StringTransformations.capitalize(service.getNativeAttributeName(ast.getName())));
    String type = (new CdUtilsPrinter()).printType(((ASTMCGenericType) ast.getMCType()).getMCTypeArgument(0).getMCTypeOpt().get());
    ASTCDParameter param = getCDParameterFacade().createParameter("int", "index");
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), type, name, param);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("umlp.methods.AssocGet", ast));
    return method;
  }

  protected ASTCDMethod createGetGemList(ASTCDAttribute ast) {
    ASTCDMethod method = createGetter(ast);
    method.setName(method.getName() + "GemList");
    return method;
  }

}
