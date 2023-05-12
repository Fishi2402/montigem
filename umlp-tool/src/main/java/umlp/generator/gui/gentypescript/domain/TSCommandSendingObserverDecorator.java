/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.CommandSendingObserverDecorator;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

public class TSCommandSendingObserverDecorator extends CommandSendingObserverDecorator {

  public TSCommandSendingObserverDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected void setOverrideAnnotation(ASTCDMethodSignature m) {
    // don't
  }

}
