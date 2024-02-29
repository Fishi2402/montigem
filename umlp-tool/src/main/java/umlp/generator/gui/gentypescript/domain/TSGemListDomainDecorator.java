/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.GemListDomainDecorator;

public class TSGemListDomainDecorator extends GemListDomainDecorator {

  public TSGemListDomainDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.gemlist.assocs.TSConstructor";
  }

  @Override
  protected String getLoadObjectsTemplate() {
    return "umlp.gui2ts.gemlist.TSLoadObjects";
  }

  @Override
  protected String getDeepEqualsTemplate() {
    return "umlp.gui2ts.gemlist.TSDeepEquals";
  }

  @Override
  protected void markDeepEqualsBackendOnly(ASTCDType targetType, ASTCDMethodSignature m) {
    // Ignore
  }
}
