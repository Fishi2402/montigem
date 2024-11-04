/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.method;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.accessor.ListAccessorDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.List;
import java.util.stream.Collectors;

public class TSListAccessorDecorator extends ListAccessorDecorator {

  public TSListAccessorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public TSListAccessorDecorator(
      final GlobalExtensionManagement glex, final CDGenService service) {
    super(glex, service);
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    initAttr(ast);
    List<ASTCDMethod> methods = Lists.newArrayList();
    methods.add(createGetListMethod(ast));
    if (ast.getModifier().isDerived()) {
      methods.get(0).getModifier().setAbstract(true);
    }
    return methods;
  }

  // TODO: Implement this in cd4analysis
  protected void initAttr(ASTCDAttribute ast) {
    this.capitalizedAttributeNameWithS = getCapitalizedAttributeNameWithS(ast);
    // if the attributeName is set by itself then the s is not removed
    // this means capitalizedAttributeNameWithS == capitalizedAttributeNameWithOutS
    this.capitalizedAttributeNameWithOutS = capitalizedAttributeNameWithS;
    // but if the attributeName is derived then the s is removed
    if (capitalizedAttributeNameWithS.endsWith("s") && service.hasDerivedAttributeName(ast)) {
      this.capitalizedAttributeNameWithOutS =
          capitalizedAttributeNameWithS.substring(0, capitalizedAttributeNameWithS.length() - 1);
    }
    this.attributeType = getAttributeType(ast);
  }
}
