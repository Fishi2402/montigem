/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.method;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.domain.decorator.methods.AssocListAccessorDecorator;

import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class TSAssocListAccessorDecorator extends AssocListAccessorDecorator {

  public TSAssocListAccessorDecorator(GlobalExtensionManagement glex, CDGenService service,
                                      ASTCDType ast) {
    super(glex, service, ast);
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methodList = Lists.newArrayList();

    String targetName = (new CdUtilsPrinter()).printType(((ASTMCGenericType) ast.getMCType()).getMCTypeArgument(0).getMCTypeOpt().get());

    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSContains", service.getNativeAttributeName(ast.getName()), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSContainsAll", service.getNativeAttributeName(ast.getName()), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSIterator", service.getNativeAttributeName(ast.getName()), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSSize", service.getNativeAttributeName(ast.getName())).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSGetList", service.getNativeAttributeName(ast.getName()), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSSubList", service.getNativeAttributeName(ast.getName()), targetName).get());

    // generate getX-Method
    methodList.add(createGetMethod(ast));

    // generate getXGem-Method
    methodList.add(createGetGemList(ast));

    return methodList;
  }

  protected ASTCDMethod createGetMethod(ASTCDAttribute ast) {
    String name = String.format(GET, StringTransformations.capitalize(service.getNativeAttributeName(ast.getName())) + "1");
    String type = (new CdUtilsPrinter()).printType(((ASTMCGenericType) ast.getMCType()).getMCTypeArgument(0).getMCTypeOpt().get());
    ASTCDParameter param = getCDParameterFacade().createParameter("int", "index");
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), type, name, param);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(getAssocGetTemplate(), ast.getName()));
    return method;
  }

  @Override
  protected String getAssocGetTemplate() {
    return "umlp.gui2ts.methods.TSAssocGet";
  }

}
