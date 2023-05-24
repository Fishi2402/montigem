/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.method;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.AbstractMethodDecorator;
import de.monticore.cd.codegen.methods.MutatorDecorator;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.GenService;

public class TSAssocMutatorDecorator extends MutatorDecorator {
  public TSAssocMutatorDecorator(
      GlobalExtensionManagement glex, CDGenService service, ASTCDType ast) {
    super(glex,
        new TSAssocMandatoryMutatorDecorator(glex, service, ast),
        new TSOptionalMutatorDecorator(glex, service),
        new TSAssocListMutatorDecorator(glex, service, ast));
  }

  @Override
  protected AbstractMethodDecorator determineMethodDecoratorStrategy(ASTCDAttribute ast) {
    if (GenService.isGemList(ast)) {
      return listMethodDecorator;
    }
    return mandatoryMethodDecorator;
  }
}
