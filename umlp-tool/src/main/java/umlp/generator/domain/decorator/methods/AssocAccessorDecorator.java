/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.methods;

import de.monticore.cd.codegen.methods.AbstractMethodDecorator;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cd.codegen.methods.accessor.OptionalAccessorDecorator;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.GenService;

public class AssocAccessorDecorator extends AccessorDecorator {
  public AssocAccessorDecorator(GlobalExtensionManagement glex, ASTCDType ast) {
    super(glex, new AssocMandatoryAccessorDecorator(glex),
            new OptionalAccessorDecorator(glex),
            new AssocListAccessorDecorator(glex, ast));
  }

  @Override
  protected AbstractMethodDecorator determineMethodDecoratorStrategy(ASTCDAttribute ast) {
    if (GenService.isGemList(ast)) {
      return listMethodDecorator;
    }
    return mandatoryMethodDecorator;
  }
}
