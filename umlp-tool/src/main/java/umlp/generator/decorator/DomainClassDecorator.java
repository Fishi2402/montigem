/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.decorator;

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cd.codegen.methods.MutatorDecorator;
import de.monticore.cd.codegen.methods.mutator.ListMutatorDecorator;
import de.monticore.cd.codegen.methods.mutator.OptionalMutatorDecorator;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.types.check.SymTypePrimitive.box;

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

    // Attributes
    List<ASTCDAttribute> attrFields = getAttributesForFields(domainClass);
    List<ASTCDAttribute> attrAssocs = getAttributesForAssociations(domainClass);
    List<ASTCDAttribute> attrList = Lists.newArrayList(attrFields);
    attrList.addAll(attrAssocs);
    clazz.addAllCDMembers(attrList);

    // Attribute gemId
    ASTCDAttribute gemAttr = getCD4C().addAttribute(clazz,"protected derived long gemId = -32;");
    glex.replaceTemplate("cd2java.Annotations", gemAttr,
            new TemplateHookPoint("umlp.domain.GemAnnotations"));

    // Attribute observerList
    ASTCDAttribute attrObserver = getCDAttributeFacade().createAttribute(PROTECTED.build(),
            "List<"+domainClass.getName()+"Observer>", StringTransformations.uncapitalize(domainClass.getName()) +"Observers");
    glex.replaceTemplate("cd2java.Value", attrObserver, new StringHookPoint("= new ArrayList<>()"));
    clazz.addCDMember(attrObserver);


    // Methods
    createGetterSetterMethods(domainClass, clazz, attrList);
    createNofificationSetter(domainClass, clazz, attrFields);
    createCheckMethods(domainClass, clazz, attrFields);
    createGetterSetterMethods(domainClass, clazz, Arrays.asList(gemAttr));
    createObserverMethods(domainClass, clazz);

    getCD4C().addMethod(clazz, "umlp.domain.Build", domainClass.getName());

    // Imports
    addImports(clazz);

    return clazz;
  }

  protected void createNofificationSetter(ASTCDClass domainClass, ASTCDClass clazz, List<ASTCDAttribute> attrFields) {
    for (ASTCDAttribute attr: attrFields) {
      getCD4C().addMethod(clazz, "umlp.domain.NotificationSetter",
              domainClass.getName(), (new CdUtilsPrinter()).printType(attr.getMCType()), attr.getName());
    }
  }

  protected void createCheckMethods(ASTCDClass domainClass, ASTCDClass clazz, List<ASTCDAttribute> attrFields) {
    for (ASTCDAttribute attr: attrFields) {
      getCD4C().addMethod(clazz, "umlp.domain.Check",
              domainClass.getName(), attr.getName());
    }
  }

  protected void createObserverMethods(ASTCDClass domainClass, ASTCDClass clazz) {
    getCD4C().addMethod(clazz, "umlp.domain.AddObserver", domainClass.getName());
    getCD4C().addMethod(clazz, "umlp.domain.RemoveObserver", domainClass.getName());
    getCD4C().addMethod(clazz, "umlp.domain.ContainsObserver", domainClass.getName());
  }

  protected List<ASTCDAttribute> getAttributesForFields(ASTCDClass domainClass) {
    List<ASTCDAttribute> attributesList = Lists.newLinkedList();
    List<ASTCDAttribute> visibleAttributes = domainClass.getCDAttributeList().stream()
            .filter(a -> GenService.isVisible(a)).collect(Collectors.toList());
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter();
    for (ASTCDAttribute visibleAttribute : visibleAttributes) {
      ASTMCQualifiedType attrType = getMCTypeFacade().createQualifiedType(box(pp.prettyprint(visibleAttribute.getMCType())));
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED.build(),
              attrType, visibleAttribute.getName());
      glex.replaceTemplate("cd2java.Annotations", attribute,
              new TemplateHookPoint("umlp.domain.AttributeAnnotations", attribute.getName()));
      attributesList.add(attribute);
    }
    return attributesList;
  }

  protected List<ASTCDAttribute> getAttributesForAssociations(ASTCDClass domainClass) {
    List<ASTCDAttribute> attrList = Lists.newLinkedList();
    List<ASTCDAssocSide> associations = CDSymbolTables.getAssociationsInHierarchy(domainClass).stream()
            .filter(a -> GenService.isVisible(a)).collect(Collectors.toList());

    // handle associations
    for (ASTCDAssocSide assocSide : associations) {
      ASTMCType type = assocSide.getMCQualifiedType().deepClone();
      if (assocSide.isPresentCDCardinality()) {
        if (assocSide.getCDCardinality().isMult()) {
          type = getMCTypeFacade().createListTypeOf(type);
        } else if(assocSide.getCDCardinality().isOpt()) {
          type = getMCTypeFacade().createOptionalTypeOf(type);
        }
      }
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED.build(),
              type, assocSide.getName());
      //attribute.setInitial(createInitialValue(attribute.getMCType()));
      attrList.add(attribute);
    }
    return attrList;
  }

  protected void createGetterSetterMethods(ASTCDClass domainClass, ASTCDClass clazz,
                                           List<ASTCDAttribute> attrList) {
    MethodDecorator methodDecoarator = new MethodDecorator(glex, new AccessorDecorator(glex),
            new MutatorDecorator(glex, new SimpleMethodMutatorDecorator(glex), new OptionalMutatorDecorator(glex), new ListMutatorDecorator(glex)));
    List<ASTCDMethod> methodList = Lists.newArrayList();
    for (ASTCDAttribute attribute : attrList) {
      methodList.addAll(methodDecoarator.decorate(attribute));
    }
    clazz.addAllCDMembers(methodList);
  }

  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, "java.util.Observable");
    getCD4C().addImport(clazz, "umlp.rte.GemIdentifiable");
    getCD4C().addImport(clazz, "org.hibernate.annotations.GenericGenerator");
    getCD4C().addImport(clazz, "javax.persistence.*");
    getCD4C().addImport(clazz, "umlp.rte.NotificationScope");
  }

}
