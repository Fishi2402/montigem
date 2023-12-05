/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.util;

import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cd.codegen.methods.accessor.ListAccessorDecorator;
import de.monticore.cd.codegen.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.cd.codegen.methods.accessor.OptionalAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.Arrays;
import java.util.List;

public class DecoratorUtil {

  public static AccessorDecorator streamlessListAcessor(final GlobalExtensionManagement glex) {
    return new AccessorDecorator(glex, new MandatoryAccessorDecorator(glex),
        new OptionalAccessorDecorator(glex), new ListAccessorDecorator(glex) {
      @Override
      protected List<String> getMethodSignatures() {
        return Arrays.asList(
            String.format(ListAccessorDecorator.CONTAINS, capitalizedAttributeNameWithOutS),
            String.format(ListAccessorDecorator.CONTAINS_ALL, capitalizedAttributeNameWithS),
            String.format(ListAccessorDecorator.IS_EMPTY, capitalizedAttributeNameWithS),
            String.format(ListAccessorDecorator.SIZE, capitalizedAttributeNameWithS),
            String.format(ListAccessorDecorator.GET, attributeType, capitalizedAttributeNameWithOutS),
            String.format(ListAccessorDecorator.INDEX_OF, capitalizedAttributeNameWithOutS),
            String.format(ListAccessorDecorator.LAST_INDEX_OF, capitalizedAttributeNameWithOutS),
            String.format(ListAccessorDecorator.SUBLIST, attributeType, capitalizedAttributeNameWithS));
      }
    });
  }
}
