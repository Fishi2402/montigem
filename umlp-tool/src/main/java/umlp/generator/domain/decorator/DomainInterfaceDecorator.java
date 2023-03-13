/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cd.codegen.methods.MutatorDecorator;
import de.monticore.cd.codegen.methods.mutator.ListMutatorDecorator;
import de.monticore.cd.codegen.methods.mutator.OptionalMutatorDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterfaceBuilder;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.umlmodifier._ast.ASTModifier;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.methods.AssocAccessorDecorator;
import umlp.generator.domain.decorator.methods.AssocMutatorDecorator;
import umlp.generator.domain.decorator.methods.MethodGenService;
import umlp.generator.domain.decorator.methods.SimpleMethodMutatorDecorator;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.*;
import static umlp.generator.domain.decorator.GenService.createType;

public class DomainInterfaceDecorator extends AbstractDecorator {

  protected final MCPath hwPath;

  public DomainInterfaceDecorator(GlobalExtensionManagement glex, MCPath hwPath) {
    super(glex);
    this.hwPath = hwPath;
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage cdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    origCD.getCDDefinition().getCDInterfacesList().stream().filter(i -> !i.getSymbol().isIsDerived())
            .forEach(i -> cdPackage.addCDElement(createInterface(i)));
  }

  protected ASTCDInterface createInterface(ASTCDInterface domainInterface) {
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
    builder.setCDExtendUsage(getExtendUsageFacade()
            .createCDExtendUsage(superInterfaces));

    ASTCDInterface cdInterface = builder.build();
    AttributesTuple attributes = getAllAttributes(domainInterface);

    // Add method signatures for Getters and Setter to be implemented by Subclasses
    attributeGettersAndSetters(cdInterface, domainInterface, attributes.attrFields);
    assocGettersAndSetters(cdInterface, attributes.attrAssocs);

    // Add method signatures for all Methods in Interface
    addMethodSignatures(domainInterface, cdInterface);

    // Imports
    addImports(cdInterface);

    return cdInterface;
  }

  protected void addMethodSignatures(ASTCDInterface domainInterface, ASTCDInterface cdInterface) {
    List<ASTCDMethod> methods = domainInterface.getCDMethodList();
    methods.forEach(m -> m.getModifier().setAbstract(true));
    cdInterface.addAllCDMembers(methods);
  }

  protected void attributeGettersAndSetters(ASTCDInterface cdInterface,
                                            ASTCDInterface domainInterface,
                                            List<ASTCDAttribute> attributes) {
    CDGenService service = new MethodGenService();
    MethodDecorator decorator = new MethodDecorator(glex,
            new AccessorDecorator(glex, service),
            new MutatorDecorator(glex, new SimpleMethodMutatorDecorator(glex, service, domainInterface),
                    new OptionalMutatorDecorator(glex, service),
                    new ListMutatorDecorator(glex, service)), service);
    for(ASTCDAttribute attribute: attributes) {
      List<ASTCDMethod> methods = decorator.decorate(attribute);
      methods.forEach(m -> m.getModifier().setAbstract(true));
      cdInterface.addAllCDMembers(methods);
    }
  }

  protected void assocGettersAndSetters(ASTCDInterface cdInterface,
                                        List<ASTCDAttribute> attributes) {
    CDGenService service = new MethodGenService();
    MethodDecorator decorator = new MethodDecorator(glex,
            new AssocAccessorDecorator(glex, service, cdInterface),
            new AssocMutatorDecorator(glex, service, cdInterface), service);
    for(ASTCDAttribute attribute: attributes) {
      List<ASTCDMethod> methods = decorator.decorate(attribute);
      methods.forEach(m -> m.getModifier().setAbstract(true));
      cdInterface.addAllCDMembers(methods);
    }
  }

  protected AttributesTuple getAllAttributes(ASTCDInterface domainInterface) {
    return new AttributesTuple(
            getAttributesForFields(domainInterface),
            getAttributesForAssociations(domainInterface));
  }

  protected List<ASTCDAttribute> getAttributesForFields(ASTCDInterface domainInterface) {
    List<ASTCDAttribute> attributesList = Lists.newLinkedList();
    List<ASTCDAttribute> visibleAttributes = domainInterface.getCDAttributeList().stream()
            .filter(GenService::isVisible).collect(Collectors.toList());
    for(ASTCDAttribute visibleAttribute: visibleAttributes) {
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED.build(),
              createType(visibleAttribute), GenService.createName(visibleAttribute.getName()));
      attribute.getModifier().setDerived(visibleAttribute.getModifier().isDerived());
      attributesList.add(attribute);
    }
    return attributesList;
  }

  protected List<ASTCDAttribute> getAttributesForAssociations(ASTCDInterface domainInterface) {
    List<ASTCDAttribute> attrList = Lists.newLinkedList();
    List<ASTCDAssocSide> associations = CDSymbolTables.getAssociations(domainInterface).stream()
            .filter(GenService::isVisible).collect(Collectors.toList());

    // handle associations
    for(ASTCDAssocSide assocSide: associations) {
      String type = assocSide.getSymbol().getType().print();
      ASTCDAttribute attribute;
      if(GenService.isList(assocSide)) {
        attribute = getCDAttributeFacade().createAttribute(PROTECTED.build(),
                getMCTypeFacade().createBasicGenericTypeOf("GemList", type), GenService.createName(assocSide.getName()));
      } else {
        attribute = getCDAttributeFacade().createAttribute(PROTECTED.build(),
                getMCTypeFacade().createBasicGenericTypeOf("Gem", type), GenService.createName(assocSide.getName()));
      }
      attrList.add(attribute);
    }
    return attrList;
  }

  protected void addImports(ASTCDInterface cdInterface) {
    getCD4C().addImport(cdInterface, PathUtil.getBackendRTECommonPath("GemIdentifiable"));
    getCD4C().addImport(cdInterface, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(cdInterface, PathUtil.getBackendRTECommonPath("Gem"));
    getCD4C().addImport(cdInterface, PathUtil.getBackendRTECommonPath("GemList"));
    getCD4C().addImport(cdInterface, PathUtil.getBackendRTECommonPath("GemBuildable"));
  }

  protected static class AttributesTuple {
    final List<ASTCDAttribute> attrFields;
    final List<ASTCDAttribute> attrAssocs;
    final List<ASTCDAttribute> attrList;

    protected AttributesTuple(List<ASTCDAttribute> attrFields, List<ASTCDAttribute> attrAssocs) {
      this.attrFields = attrFields;
      this.attrAssocs = attrAssocs;
      attrList = new ArrayList<>(attrAssocs);
      attrList.addAll(attrFields);
    }
  }
}
