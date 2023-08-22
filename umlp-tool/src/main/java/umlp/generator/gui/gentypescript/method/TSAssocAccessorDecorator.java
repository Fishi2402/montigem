/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.method;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.AbstractMethodDecorator;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.methods.AssocMandatoryAccessorDecorator;

public class TSAssocAccessorDecorator extends AccessorDecorator {
  public TSAssocAccessorDecorator(
      GlobalExtensionManagement glex, CDGenService service, ASTCDType ast) {
    super(glex,
        new TSAssocMandatoryAccessorDecorator(glex, service),
        new TSOptionalAccessorDecorator(glex, service),
        new TSAssocListAccessorDecorator(glex, service, ast));
  }

  @Override
  protected AbstractMethodDecorator determineMethodDecoratorStrategy(ASTCDAttribute ast) {
    if (GenService.isGemList(ast)) {
      return listMethodDecorator;
    }
    return mandatoryMethodDecorator;
  }
}
