/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cd.codegen.methods.MutatorDecorator;
import de.monticore.cd.codegen.methods.mutator.ListMutatorDecorator;
import de.monticore.cd.codegen.methods.mutator.OptionalMutatorDecorator;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.methods.AssocAccessorDecorator;
import umlp.generator.domain.decorator.methods.AssocMutatorDecorator;
import umlp.generator.domain.decorator.methods.SimpleMethodMutatorDecorator;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.*;

public class DomainClassDecorator extends AbstractDecorator {


  public DomainClassDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    String name = origCD.getCDDefinition().getName();
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getSymbol().isIsDerived())
      .forEach(c -> astcdPackage.addCDElement(createClass(c, name)));
  }

  protected ASTCDClass createClass(ASTCDClass domainClass, String name) {
    ASTCDInterfaceUsage implementsUsage = getInterfaceUsageFacade().createCDInterfaceUsage("GemIdentifiable");
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
    glex.replaceTemplate("cd2java.Annotations", clazz,
            new TemplateHookPoint("umlp.domain.ClassAnnotations", clazz.getName()));

    // Constructors
    getCD4C().addConstructor(clazz, "umlp.domain.Constructor", domainClass.getName());

    // Attribute gemId
    ASTCDAttribute gemAttr = getCD4C().addAttribute(clazz,"protected derived long gemId = -32;");
    glex.replaceTemplate("cd2java.Annotations", gemAttr,
            new TemplateHookPoint("umlp.domain.GemAnnotations"));

    // Attribute observerList
    ASTCDAttribute attrObserver = getCD4C().addAttribute(clazz,
            " protected List<"+domainClass.getName()+"Observer> " + StringTransformations.uncapitalize(domainClass.getName()) +"Observers");
    glex.replaceTemplate("cd2java.Value", attrObserver, new StringHookPoint("= new ArrayList<>()"));
    glex.replaceTemplate("cd2java.Annotations", attrObserver,
            new StringHookPoint("transient"));

    // Attributes
    List<ASTCDAttribute> attrFields = getAttributesForFields(domainClass);
    List<ASTCDAttribute> attrAssocs = getAttributesForAssociations(domainClass);
    List<ASTCDAttribute> attrList = Lists.newArrayList(attrFields);
    attrList.addAll(attrAssocs);
    clazz.addAllCDMembers(attrList);

    // General methods
    clazz.addAllCDMembers(new MethodDecorator(glex).decorate(gemAttr));
    createObserverMethods(domainClass, clazz);
    getCD4C().addMethod(clazz, "umlp.domain.Build", domainClass.getName());

    // Methods for class attributes
    createGetterSetterForAttrs(domainClass, clazz, attrFields);
    createCheckMethods(domainClass, clazz);

    // Methods for class assocs
    createGetterSetterForAssocs(domainClass, clazz, attrAssocs);

     // Imports
    addImports(clazz);

    return clazz;
  }

  protected void createCheckMethods(ASTCDClass domainClass, ASTCDClass clazz) {
    for (ASTCDAttribute attr: CDSymbolTables.getAttributesInHierarchy(domainClass)) {
      getCD4C().addMethod(clazz, "umlp.domain.Check",
              domainClass.getName(), attr.getName());
    }
    for (ASTCDAssocSide assoc: CDSymbolTables.getAssociationsInHierarchy(domainClass)) {
      getCD4C().addMethod(clazz, "umlp.domain.Check",
              domainClass.getName(), assoc.getName());
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
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter();
    for (ASTCDAttribute visibleAttribute : visibleAttributes) {
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED.build(),
              GenService.createType(visibleAttribute), visibleAttribute.getName());
      glex.replaceTemplate("cd2java.Annotations", attribute,
              new TemplateHookPoint("umlp.domain.AttributeAnnotations", attribute.getName()));
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
                getMCTypeFacade().createBasicGenericTypeOf("GemList", type), assocSide.getName());
        glex.replaceTemplate("cd2java.Annotations", attribute,
                new TemplateHookPoint("umlp.domain.AssocAnnotations",
                        domainClass.getName(), attribute.getName()));

      } else {
        attribute = getCDAttributeFacade().createAttribute(PROTECTED.build(),
                getMCTypeFacade().createBasicGenericTypeOf("Gem", type), assocSide.getName());
      }
      attrList.add(attribute);
    }
    return attrList;
  }

  protected void createGetterSetterForAttrs(ASTCDClass domainClass, ASTCDClass clazz,
                                            List<ASTCDAttribute> attrList) {
    MethodDecorator methodDecorator = new MethodDecorator(glex, new AccessorDecorator(glex),
            new MutatorDecorator(glex, new SimpleMethodMutatorDecorator(glex, domainClass), new OptionalMutatorDecorator(glex), new ListMutatorDecorator(glex)));
    for (ASTCDAttribute attribute : attrList) {
      clazz.addAllCDMembers(methodDecorator.decorate(attribute));
    }
  }

  protected void createGetterSetterForAssocs(ASTCDClass domainClass, ASTCDClass clazz,
                                             List<ASTCDAttribute> attrList) {
    MethodDecorator methodDecorator = new MethodDecorator(glex,
            new AssocAccessorDecorator(glex),
            new AssocMutatorDecorator(glex, domainClass));
    for (ASTCDAttribute attribute : attrList) {
      clazz.addAllCDMembers(methodDecorator.decorate(attribute));
    }
  }

  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, "java.util.Observable");
    getCD4C().addImport(clazz, "umlp.rte.GemIdentifiable");
    getCD4C().addImport(clazz, "umlp.rte.NotificationScope");
    getCD4C().addImport(clazz, "umlp.rte.Gem");
    getCD4C().addImport(clazz, "umlp.rte.GemList");
    getCD4C().addImport(clazz, "com.google.gson.annotations.JsonAdapter");
    getCD4C().addImport(clazz, "org.hibernate.annotations.GenericGenerator");
    getCD4C().addImport(clazz, "javax.persistence.*");
  }

}
