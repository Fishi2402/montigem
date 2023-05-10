/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.method;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CDGenService;
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

    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.Contains", service.getNativeAttributeName(ast.getName()), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.ContainsAll", service.getNativeAttributeName(ast.getName()), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.Iterator", service.getNativeAttributeName(ast.getName()), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.Size", service.getNativeAttributeName(ast.getName())).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.GetList", service.getNativeAttributeName(ast.getName()), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.SubList", service.getNativeAttributeName(ast.getName()), targetName).get());

    // generate getX-Method
    methodList.add(createGetMethod(ast));

    // generate getXGem-Method
    methodList.add(createGetGemList(ast));

    return methodList;
  }

  @Override
  protected String getAssocGetTemplate() {
    return "umlp.gui2ts.methods.AssocGet";
  }

}
