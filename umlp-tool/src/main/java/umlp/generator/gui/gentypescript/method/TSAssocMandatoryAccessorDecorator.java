/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.method;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.methods.AssocMandatoryAccessorDecorator;

import java.util.List;

public class TSAssocMandatoryAccessorDecorator extends AssocMandatoryAccessorDecorator {

  public TSAssocMandatoryAccessorDecorator(
      GlobalExtensionManagement glex, CDGenService service) {
    super(glex, service);
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methodList = Lists.newArrayList();
    // TODO: remove this temporary fix
    if (!ast.getModifier().isDerived()) {
      // generate getX-Method
      methodList.add(createGetMethod(ast));
      // generate getXGem-Method
      ASTCDMethod method = createGetGem(ast);
      //replace param with
      methodList.add(method);
    }
    return methodList;
  }

  @Override
  protected String getGetTemplate() {
    return "umlp.gui2ts.methods.TSGet";
  }

}
