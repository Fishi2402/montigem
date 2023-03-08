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
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.methods.AssocAccessorDecorator;
import umlp.generator.domain.decorator.methods.AssocMutatorDecorator;
import umlp.generator.domain.decorator.methods.MethodGenService;
import umlp.generator.domain.decorator.methods.SimpleMethodMutatorDecorator;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.*;
import static umlp.generator.domain.decorator.GenService.createType;

public class DomainClassDecorator extends AbstractDecorator {

  protected final MCPath hwPath;

  public DomainClassDecorator(final GlobalExtensionManagement glex,
                              MCPath hwPath) {
    super(glex);
    this.hwPath = hwPath;
  }

  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    String name = origCD.getCDDefinition().getName();
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getSymbol().isIsDerived())
      .forEach(c -> astcdPackage.addCDElement(createClass(origCD, c, name)));

    origCD.getCDDefinition().getCDEnumsList().stream().filter(c -> !c.getSymbol().isIsDerived())
            .forEach(c -> astcdPackage.addCDElement(c.deepClone()));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, ASTCDClass domainClass, String name) {
    ASTCDInterfaceUsage implementsUsage = getInterfaceUsageFacade()
        .createCDInterfaceUsage("GemIdentifiable", "GemBuildable");
    ASTModifier modifier = domainClass.getModifier().isAbstract() ? PUBLIC_ABSTRACT.build() : PUBLIC.build();
    ASTCDClassBuilder builder = UMLPMill.cDClassBuilder()
            .setModifier(modifier)
            .setCDInterfaceUsage(implementsUsage)
            .setName(domainClass.getName());
    if (domainClass.isPresentCDExtendUsage()) {
      builder.setCDExtendUsage(getExtendUsageFacade()
              .createCDExtendUsage(domainClass.getCDExtendUsage().getSuperclass(0).deepClone()));
    }
    ASTCDClass clazz = builder.build();

    // Constructors
    getCD4C().addConstructor(clazz, "umlp.domain.Constructor", domainClass.getName());

    if (!domainClass.isPresentCDExtendUsage()) {
      // Attribute gemId and getter and setter
      ASTCDAttribute gemAttr = getCD4C().addAttribute(clazz, "protected readonly long gemId = -32;");
      clazz.addAllCDMembers(new MethodDecorator(glex).decorate(gemAttr));
    }

    // Attribute observerList
    ASTCDAttribute attrObserver = getCD4C().addAttribute(clazz,
            " protected List<"+domainClass.getName()+"Observer> " + StringTransformations.uncapitalize(domainClass.getName()) +"Observers");
    glex.replaceTemplate("cd2java.Value", attrObserver, new StringHookPoint("= new ArrayList<>()"));
    glex.replaceTemplate("cd2java.Annotations", attrObserver,
            new StringHookPoint("transient"));

    // Attributes
    AttributesTuple attributes = getAllAttributes(domainClass);
    clazz.addAllCDMembers(attributes.attrList);

    // General methods
    createObserverMethods(domainClass, clazz);
    getCD4C().addMethod(clazz, "umlp.domain.Build", domainClass.getName());

    // Methods for class attributes
    createGetterSetterForAttrs(domainClass, clazz, attributes.attrFields);
    createCheckMethods(domainClass, clazz);

    // Methods for class assocs
    createGetterSetterForAssocs(domainClass, clazz, attributes.attrAssocs);

     // Imports
    addImports(origCD, clazz);

    return clazz;
  }

  protected AttributesTuple getAllAttributes(ASTCDClass domainClass) {
    return new AttributesTuple(
        getAttributesForFields(domainClass),
        getAttributesForAssociations(domainClass)
    );
  }

  protected void createCheckMethods(ASTCDClass domainClass, ASTCDClass clazz) {
    for (ASTCDAttribute attr: CDSymbolTables.getAttributesInHierarchy(domainClass)
            .stream().filter(a -> GenService.isVisibleNotDerived(a)).collect(Collectors.toList())) {
      getCD4C().addMethod(clazz, "umlp.domain.Check",
              domainClass.getName(), attr.getName(),
              !GenService.isList(attr) && !GenService.isOptional(attr));
    }
    for (ASTCDAssocSide assoc: CDSymbolTables.getAssociationsInHierarchy(domainClass)) {
      getCD4C().addMethod(clazz, "umlp.domain.Check",
              domainClass.getName(), assoc.getName(),
              !GenService.isOptional(assoc));
    }
  }

protected void createObserverMethods(ASTCDClass domainClass, ASTCDClass clazz) {
    getCD4C().addMethod(clazz, "umlp.domain.AddObserver", domainClass.getName(), clazz.isPresentCDExtendUsage());
    getCD4C().addMethod(clazz, "umlp.domain.RemoveObserver", domainClass.getName());
    getCD4C().addMethod(clazz, "umlp.domain.ContainsObserver", domainClass.getName());
  }

  protected List<ASTCDAttribute> getAttributesForFields(ASTCDClass domainClass) {
    List<ASTCDAttribute> attributesList = Lists.newLinkedList();
    List<ASTCDAttribute> visibleAttributes = domainClass.getCDAttributeList().stream()
            .filter(a -> GenService.isVisible(a)).collect(Collectors.toList());
    for (ASTCDAttribute visibleAttribute : visibleAttributes) {
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED.build(),
              createType(visibleAttribute), GenService.createName(visibleAttribute.getName()));
      attribute.getModifier().setDerived(visibleAttribute.getModifier().isDerived());
      attributesList.add(attribute);
    }
    return attributesList;
  }

  protected List<ASTCDAttribute> getAttributesForAssociations(ASTCDClass domainClass) {
    List<ASTCDAttribute> attrList = Lists.newLinkedList();
    List<ASTCDAssocSide> associations = CDSymbolTables.getAssociations(domainClass).stream()
            .filter(a -> GenService.isVisible(a)).collect(Collectors.toList());

    // handle associations
    for (ASTCDAssocSide assocSide : associations) {
      String type = assocSide.getSymbol().getType().print();
      ASTCDAttribute attribute;
      if (GenService.isList(assocSide)) {
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

  protected void createGetterSetterForAttrs(ASTCDClass domainClass, ASTCDClass clazz,
                                            List<ASTCDAttribute> attrList) {
    CDGenService service = new MethodGenService();
    MethodDecorator methodDecorator = new MethodDecorator(glex,
            new AccessorDecorator(glex, service),
            new MutatorDecorator(glex, new SimpleMethodMutatorDecorator(glex, service, domainClass),
                    new OptionalMutatorDecorator(glex, service),
                    new ListMutatorDecorator(glex, service)), service);
    for (ASTCDAttribute attribute : attrList) {
      clazz.addAllCDMembers(methodDecorator.decorate(attribute));
    }
  }

  protected void createGetterSetterForAssocs(ASTCDClass domainClass, ASTCDClass clazz,
                                             List<ASTCDAttribute> attrList) {
    CDGenService service = new MethodGenService();
    MethodDecorator methodDecorator = new MethodDecorator(glex,
            new AssocAccessorDecorator(glex, service, clazz),
            new AssocMutatorDecorator(glex, service, clazz), service);
    for (ASTCDAttribute attribute : attrList) {
      clazz.addAllCDMembers(methodDecorator.decorate(attribute));
    }
  }

  protected void addImports(ASTCDCompilationUnit origCD, ASTCDType clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("GemIdentifiable"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("Gem"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("GemList"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("GemBuildable"));
    addDBAnnotationInfo(clazz);
  }

  protected void addDBAnnotationInfo(ASTCDType clazz) {
    // empty
  }

  protected static class AttributesTuple{
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
