/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.method;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.AbstractMethodDecorator;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cd.codegen.methods.accessor.ListAccessorDecorator;
import de.monticore.cd.codegen.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

public class TSAccessorDecorator extends AccessorDecorator {

  public TSAccessorDecorator(final GlobalExtensionManagement glex) {
    super(
        glex,
        new MandatoryAccessorDecorator(glex),
        new TSOptionalAccessorDecorator(glex),
        new ListAccessorDecorator(glex));
  }

  public TSAccessorDecorator(final GlobalExtensionManagement glex, final CDGenService service) {
    super(
        glex,
        new MandatoryAccessorDecorator(glex, service),
        new TSOptionalAccessorDecorator(glex, service),
        new ListAccessorDecorator(glex, service));
  }

  public TSAccessorDecorator(
      final GlobalExtensionManagement glex,
      final AbstractMethodDecorator mandatoryMethodDecorator,
      final AbstractMethodDecorator optionalMethodDecorator,
      final AbstractMethodDecorator listMethodDecorator) {
    super(glex, mandatoryMethodDecorator, optionalMethodDecorator, listMethodDecorator);
  }
}
