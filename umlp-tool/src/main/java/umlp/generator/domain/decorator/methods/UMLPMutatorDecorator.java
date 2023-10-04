/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.methods;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.AbstractMethodDecorator;
import de.monticore.cd.codegen.methods.MutatorDecorator;
import de.monticore.cd.codegen.methods.mutator.ListMutatorDecorator;
import de.monticore.cd.codegen.methods.mutator.MandatoryMutatorDecorator;
import de.monticore.cd.codegen.methods.mutator.OptionalMutatorDecorator;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.GenService;

public class UMLPMutatorDecorator extends MutatorDecorator {

  public UMLPMutatorDecorator(
      final GlobalExtensionManagement glex,
      final AbstractMethodDecorator mandatoryMethodDecorator,
      final AbstractMethodDecorator optionalMethodDecorator,
      final AbstractMethodDecorator listMethodDecorator) {
    super(glex, mandatoryMethodDecorator, optionalMethodDecorator, listMethodDecorator);
  }

  @Override
  protected AbstractMethodDecorator determineMethodDecoratorStrategy(ASTCDAttribute ast) {
    if (GenService.isList(ast)) {
      return listMethodDecorator;
    }
    return mandatoryMethodDecorator;
  }

}
