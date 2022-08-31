package umlp.generator.domain.decorator.methods;

import de.monticore.cd.codegen.methods.AbstractMethodDecorator;
import de.monticore.cd.codegen.methods.MutatorDecorator;
import de.monticore.cd.codegen.methods.mutator.OptionalMutatorDecorator;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.GenService;

public class AssocMutatorDecorator extends MutatorDecorator {
  public AssocMutatorDecorator(GlobalExtensionManagement glex, ASTCDType ast) {
    super(glex, new AssocMandatoryMutatorDecorator(glex, ast), new OptionalMutatorDecorator(glex), new AssocListMutatorDecorator(glex, ast));
  }

  @Override
  protected AbstractMethodDecorator determineMethodDecoratorStrategy(ASTCDAttribute ast) {
    if (GenService.isGemList(ast)) {
      return listMethodDecorator;
    }
     return mandatoryMethodDecorator;
  }
}
