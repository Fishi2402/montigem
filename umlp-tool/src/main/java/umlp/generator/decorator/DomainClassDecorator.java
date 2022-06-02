/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.decorator;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlmodifier._ast.ASTModifier;
import umlp.UMLPMill;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.*;
import static umlp.generator.decorator.GenService.createInitialValue;

public class DomainClassDecorator extends AbstractDecorator {


  public DomainClassDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    String name = origCD.getCDDefinition().getName();
    origCD.getCDDefinition().getCDClassesList().forEach(c -> astcdPackage.addCDElement(createClass(c, name)));
  }

  protected ASTCDClass createClass(ASTCDClass domainClass, String name) {
    ASTCDExtendUsage extendUsage;
    if (domainClass.isPresentCDExtendUsage()) {
      extendUsage = getExtendUsageFacade().createCDExtendUsage(domainClass.getCDExtendUsage().getSuperclass(0).deepClone());
    } else {
      extendUsage = getExtendUsageFacade().createCDExtendUsage("Observable");
    }
    ASTModifier modifier = domainClass.getModifier().isAbstract() ? PUBLIC_ABSTRACT.build() : PUBLIC.build();
    ASTCDClass clazz = UMLPMill.cDClassBuilder()
            .setModifier(modifier)
            .setCDExtendUsage(extendUsage)
            .setName(domainClass.getName()).build();
    glex.replaceTemplate("cd2java.Annotations", clazz,
            new TemplateHookPoint("umlp.domain.ClassAnnotations", clazz.getName()));

    // Constructors
    getCD4C().addConstructor(clazz, "umlp.domain.Constructor", domainClass.getName());

    // Attributes
    List<ASTCDAttribute> attrList = Lists.newArrayList();
    attrList.addAll(getAttributesForFields(domainClass));
    attrList.addAll(getAttributesForAssociations(domainClass));
    clazz.addAllCDMembers(attrList);

    // Attribute gemId
    ASTCDAttribute attr = getCDAttributeFacade().createAttribute(PROTECTED.build(), "long", "gemId");
    glex.replaceTemplate("cd2java.Value", attr, new StringHookPoint("-32"));
    glex.replaceTemplate("cd2java.Annotations", attr,
            new TemplateHookPoint("umlp.domain.GemAnnotations"));
    attr.getModifier().setDerived(true);
    clazz.addCDMember(attr);

    // Methods
    createGetterSetterMethods(domainClass, clazz, clazz.getCDAttributeList());
    getCD4C().addMethod(clazz, "umlp.domain.Init");
    getCD4C().addMethod(clazz, "umlp.domain.Build", domainClass.getName());

    // Imports
    addImports(clazz);

    return clazz;
  }

  protected List<ASTCDAttribute> getAttributesForFields(ASTCDClass domainClass) {
    List<ASTCDAttribute> attributesList = Lists.newLinkedList();
    List<ASTCDAttribute> visibleAttributes = domainClass.getCDAttributeList().stream()
            .filter(a -> GenService.isVisible(a)).collect(Collectors.toList());

    // get all visible parameters
    for (ASTCDAttribute visibleAttribute : visibleAttributes) {
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED.build(),
              visibleAttribute.getMCType().deepClone(), visibleAttribute.getName());
      glex.replaceTemplate("cd2java.Annotations", attribute,
              new TemplateHookPoint("umlp.domain.AttributeAnnotations", domainClass.getName()));
      attributesList.add(attribute);
    }
    return attributesList;
  }

  protected List<ASTCDAttribute> getAttributesForAssociations(ASTCDClass domainClass) {
    List<ASTCDAttribute> attrList = Lists.newLinkedList();
    List<ASTCDAssocSide> associations = CDSymbolTables.getAssociationsInHierarchy(domainClass).stream()
            .filter(a -> GenService.isVisible(a)).collect(Collectors.toList());

    // handle associations
    for (ASTCDAssocSide association : associations) {
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED.build(),
              association.getSymbol().getType().print(), association.getName());
      attribute.setInitial(createInitialValue(association.getSymbol().getType()));
      attrList.add(attribute);
    }
    return attrList;
  }

  protected void createGetterSetterMethods(ASTCDClass domainClass, ASTCDClass clazz,
                                           List<ASTCDAttribute> attrList) {
    MethodDecorator methodDecoarator = new MethodDecorator(glex);
    List<ASTCDMethod> methodList = Lists.newArrayList();
    for (ASTCDAttribute attribute : attrList) {
      methodList.addAll(methodDecoarator.decorate(attribute));
    }
    clazz.addAllCDMembers(methodList);
  }

  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, "java.util.List");
    getCD4C().addImport(clazz, "java.util.Optional");
    getCD4C().addImport(clazz, "java.util.Observable");
    getCD4C().addImport(clazz, "umlp.rte.GemIdentifiable");
    getCD4C().addImport(clazz, "org.hibernate.annotations.GenericGenerator");
    getCD4C().addImport(clazz, "javax.persistence.*");
  }

}
