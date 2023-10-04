/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.method;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.methods.AssocListAccessorDecorator;

import java.util.List;

public class TSAssocListAccessorDecorator extends AssocListAccessorDecorator {

  public TSAssocListAccessorDecorator(GlobalExtensionManagement glex, CDGenService service, ASTCDType ast) {
    super(glex, service, ast);
  }

  @Override
  protected String getAssocGetListTemplate() {
    return "umlp.gui2ts.methods.TSAssocGetList";
  }

  @Override
  protected String getAssocGetListNameTemplate() {
    return "%s1";
  }

  @Override
  protected List<String> getMethodTemplates() {
    return List.of(
        "umlp.gui2ts.methods.assocs.TSContains",
        "umlp.gui2ts.methods.assocs.TSContainsAll",
        "umlp.gui2ts.methods.assocs.TSIterator",
        "umlp.gui2ts.methods.assocs.TSSize",
        "umlp.gui2ts.methods.assocs.TSGetList",
        "umlp.gui2ts.methods.assocs.TSSubList"
    );
  }
}
