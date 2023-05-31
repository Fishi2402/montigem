/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.CommandSendingObserverDecorator;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class TSCommandSendingObserverDecorator extends CommandSendingObserverDecorator {

  public TSCommandSendingObserverDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected ASTCDClass createEmptyClass(ASTCDClass domainClass, String classname) {
    return UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(domainClass.getName() + "Observer"))
        .setName(classname)
        .build();
  }

  @Override
  protected void setOverrideAnnotation(ASTCDMethodSignature m) {
    // don't
  }

}
