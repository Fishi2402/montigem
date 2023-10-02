/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.methods;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.AbstractMethodDecorator;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cd.codegen.methods.accessor.ListAccessorDecorator;
import de.monticore.cd.codegen.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.cd.codegen.methods.accessor.OptionalAccessorDecorator;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.GenService;

public class UMLPAccessorDecorator extends AccessorDecorator {

  public UMLPAccessorDecorator(final GlobalExtensionManagement glex, final CDGenService service) {
    super(
        glex,
        new MandatoryAccessorDecorator(glex, service),
        new OptionalAccessorDecorator(glex, service),
        new ListAccessorDecorator(glex, service));
  }

  @Override
  protected AbstractMethodDecorator determineMethodDecoratorStrategy(ASTCDAttribute ast) {
    if (GenService.isList(ast)) {
      return listMethodDecorator;
    }
    return mandatoryMethodDecorator;
  }

}
