/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.CommandObserverDecorator;
import umlp.generator.domain.decorator.CommandSendingObserverDecorator;

public class TSCommandObserverDecorator extends CommandObserverDecorator {

  public TSCommandObserverDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected void setOverrideAnnotation(ASTCDMethodSignature m) {
    // don't
  }

}
