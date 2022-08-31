package umlp.generator.domain.decorator.methods;

import de.monticore.cd.codegen.methods.AbstractMethodDecorator;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cd.codegen.methods.accessor.OptionalAccessorDecorator;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.GenService;

public class AssocAccessorDecorator extends AccessorDecorator {
  public AssocAccessorDecorator(GlobalExtensionManagement glex) {
    super(glex, new AssocMandatoryAccessorDecorator(glex), new OptionalAccessorDecorator(glex), new AssocListAccessorDecorator(glex));
  }

  @Override
  protected AbstractMethodDecorator determineMethodDecoratorStrategy(ASTCDAttribute ast) {
    if (GenService.isGemList(ast)) {
      return listMethodDecorator;
    }
    return mandatoryMethodDecorator;
  }
}
