/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterfaceBuilder;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.umlmodifier._ast.ASTModifier;
import umlp.UMLPMill;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class DomainInterfaceDecorator extends DomainTypeDecorator {

  public DomainInterfaceDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage cdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    origCD.getCDDefinition().getCDInterfacesList().stream().filter(i -> !i.getSymbol().isIsDerived())
            .forEach(i -> cdPackage.addCDElement(createInterface(i, origCD)));
  }

  protected ASTCDInterface createInterface(ASTCDInterface domainInterface, ASTCDCompilationUnit origCD) {
    String name = domainInterface.getName();
    ASTModifier modifier = PUBLIC.build();

    ASTCDInterfaceBuilder builder = UMLPMill.cDInterfaceBuilder()
            .setModifier(modifier)
            .setName(name);

    List<ASTMCObjectType> superInterfaces = new ArrayList<>();
    superInterfaces.add(getMCTypeFacade().createQualifiedType("GemIdentifiable"));
    superInterfaces.add(getMCTypeFacade().createQualifiedType("GemBuildable"));
    if(domainInterface.isPresentCDExtendUsage()) {
      superInterfaces.addAll(domainInterface.getSuperclassList());
    }
    superInterfaces.addAll(domainInterface.getInterfaceList());

    builder.setCDExtendUsage(getExtendUsageFacade()
            .createCDExtendUsage(superInterfaces));

    ASTCDInterface cdInterface = builder.build();
    AttributesTuple attributes = getAllAttributes(domainInterface, true, origCD);

    // Add method signatures for Getters and Setter to be implemented by Subclasses
    createGetterSetterForAttrs(cdInterface, domainInterface, attributes.attrFields, true);
    createGetterSetterForAssocs(cdInterface, attributes.attrAssocs, true);

    // Add method signatures for all Methods in Interface
    addMethodSignatures(domainInterface, cdInterface);

    // Imports
    addImports(domainInterface, cdInterface);

    return cdInterface;
  }

  protected void addMethodSignatures(ASTCDInterface domainInterface, ASTCDInterface cdInterface) {
    List<ASTCDMethod> methods = domainInterface.getCDMethodList();
    methods.forEach(m -> m.getModifier().setAbstract(true));
    cdInterface.addAllCDMembers(methods);
  }
}
