/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.decorator;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.facade.*;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.*;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpressionBuilder;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.umlmodifier._ast.ASTModifier;
import umlp.UMLPMill;
import umlp.generator.builder.TransformationUtils;
import umlp.generator.builder.TypeHelper;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.generating.GeneratorEngine.existsHandwrittenClass;
import static de.se_rwth.commons.Names.constructQualifiedName;
import static umlp.generator.decorator.TopDecorator.TOP_SUFFIX;
import static umlp.generator.builder.CDAssociationUtil.*;
import static umlp.generator.builder.TransformationUtils.capitalize;
import static umlp.generator.builder.TransformationUtils.makeCamelCase;

public class BuilderDecorator extends AbstractDecorator {

  public static final String BUILDER_PREFIX = "";

  public static final String BUILDER_SUFFIX = "Builder";

  protected final MCPath hwPath;

  private TypeHelper typeHelper = new TypeHelper();

  public BuilderDecorator(final GlobalExtensionManagement glex, MCPath hwPath) {
    super(glex);
    this.hwPath = hwPath;
  }

  public final void transform(ASTCDCompilationUnit topCD) {
    checkNotNull(topCD);
    checkNotNull(topCD.getCDDefinition());

    List<ASTCDClass> cdClassesList = topCD.getCDDefinition().getCDClassesList();
    for (ASTCDClass domainClass : cdClassesList) {
      Optional<ASTCDClass> astcdClass = extendDomainClass(domainClass, topCD);
      if (astcdClass.isPresent()) {
        topCD.getCDDefinition().addCDElement(astcdClass.get());
      }
    }
  }

  protected Optional<ASTCDClass> extendDomainClass(ASTCDClass domainClass, ASTCDCompilationUnit topCD) {
    CDTypeSymbol typeSymbol = domainClass.getSymbol();
    Optional<ASTCDClass> clazz = getOrCreateExtendedClass(domainClass, typeSymbol, topCD);
    if (clazz.isPresent()) {
      ASTCDClass extendedClass = clazz.get();
      extendedClass.addAllCDMembers(createAttributes(domainClass, extendedClass));
      extendedClass.addAllCDMembers(createConstructors(extendedClass));
      extendedClass.addAllCDMembers(createMethods(extendedClass, domainClass, typeSymbol));
      addImports(extendedClass);
    }
    return clazz;
  }

  protected Optional<ASTCDClass> getOrCreateExtendedClass(ASTCDClass domainClass, CDTypeSymbol typeSymbol, ASTCDCompilationUnit topCD) {
    ASTMCPackageDeclaration packageName = UMLPMill.mCPackageDeclarationBuilder()
            .setMCQualifiedName(DEFAULT_PACKAGE).build(); // typeSymbol.getName().toLowerCase()
    String className = domainClass.getName() + BUILDER_SUFFIX;

    ASTCDClass builderClass = UMLPMill.cDClassBuilder().setModifier(PUBLIC.build()).setName(className).build();
    if (typeSymbol.isPresentSuperClass()) {
      String superClassName = typeSymbol.getSuperClass().print();
      ASTCDExtendUsage extendUsage = UMLPMill.cDExtendUsageBuilder().addSuperclass(UMLPMill.mCQualifiedTypeBuilder().setMCQualifiedName(
              UMLPMill.mCQualifiedNameBuilder().setPartsList(Lists.newArrayList(superClassName + BUILDER_SUFFIX)).build()).build()).build();
      builderClass.setCDExtendUsage(extendUsage);

//      String join = Joiners.DOT.join(/*typeSymbol.getSuperClass().getTypeInfo().getPackageName()*/ "monti",
//              TransformationUtils.CLASSES_PACKAGE, superClassName.toLowerCase(),
//              superClassName + BUILDER_SUFFIX);
//      // add import
//      CD4C.getInstance().addImport(builderClass, join);
    }
    getGlex().replaceTemplate(CD2JavaTemplates.PACKAGE, builderClass, new StringHookPoint(
            packageName.getMCQualifiedName().toString()));

    // Check if a handwritten class exists
    if (existsHandwrittenClass(hwPath, constructQualifiedName(topCD.getCDPackageList(), builderClass.getName()))) {
      applyTopMechanism(builderClass);
    }
    builderClass.setSymbol(typeSymbol);
    return Optional.of(builderClass);
  }

  protected List<ASTCDConstructor> createConstructors(ASTCDClass builderClass) {
    return Lists.newArrayList(getCDConstructorFacade().createConstructor(PUBLIC.build(), builderClass.getName()));
  }

  protected List<ASTCDAttribute> createAttributes(ASTCDClass domainClass, ASTCDClass clazz) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    attributes.addAll(getAttributesForFields(domainClass));
    // attributes.addAll(getAttributesForAssociations(domainClass));
    if (!clazz.isPresentCDExtendUsage()) {
      // attributes.add(getCDAttributeFacade().createAttribute(PROTECTED.build(), "Long", UMLP_ID_NAME));
    }
    return attributes;
  }

  private List<ASTCDAttribute> getAttributesForFields(ASTCDClass clazz) {
    List<ASTCDAttribute> attributesList = Lists.newLinkedList();
    List<ASTCDAttribute> visibleAttributes = getVisibleAttributes(clazz);

    // get all visible parameters
    ASTNameExpressionBuilder expressionBuilder = new ASTNameExpressionBuilder();
    for (ASTCDAttribute visibleAttribute : visibleAttributes) {
      if (!visibleAttribute.getModifier().isDerived()) {
        ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(CDModifier.PROTECTED.build(),
                visibleAttribute.getMCType(), visibleAttribute.getName());

        if (typeHelper.isOptional(visibleAttribute.printType())) {
          attribute.setInitial(expressionBuilder.setName("Optional.empty()").build());

        } else if (typeHelper.isWrapperType(visibleAttribute.printType())) {
          attribute.setInitial(expressionBuilder.setName(
                  typeHelper.getDefaultValue(visibleAttribute.printType())).build());

        } else if (typeHelper.isPrimitive(visibleAttribute.printType())) {
          attribute.setInitial(expressionBuilder.setName(
                  typeHelper.getDefaultValue(visibleAttribute.printType())).build());

        } else if (typeHelper.isString(visibleAttribute.printType())) {
          attribute.setInitial(expressionBuilder.setName(
                  typeHelper.getDefaultValue(visibleAttribute.printType())).build());

        } else if (typeHelper.isList(visibleAttribute.printType())
                || typeHelper.isGenericList(visibleAttribute.printType())) {
          attribute.setInitial(expressionBuilder.setName("new LinkedList<>()").build());

        } else if (typeHelper.isSet(visibleAttribute.printType())) {
          attribute.setInitial(expressionBuilder.setName("new HashSet<>()").build());
        }
        attributesList.add(attribute);
      }
    }
    return attributesList;
  }

  private List<ASTCDAttribute> getAttributesForAssociations(ASTCDClass clazz) {
    List<ASTCDAttribute> attrList = Lists.newLinkedList();
    List<CDRoleSymbol> associations = getAllNonDerivedAssociationsForClass(clazz);

    // handle associations
    for (CDRoleSymbol association : associations) {
      ASTCDParameter parameter = associationToCDParameter(association);
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(CDModifier.PROTECTED.build(),
              parameter.getMCType(), parameter.getName());
      attrList.add(attribute);

      // qualified association
      ASTNameExpressionBuilder expressionBuilder = new ASTNameExpressionBuilder();
      if (isQualified(association) && (isOne(association) || isOptional(association))) {
        attribute.setInitial(expressionBuilder.setName("new HashMap<>()").build());

      } else if (isQualified(association) && isOrdered(association)) {
        attribute.setInitial(expressionBuilder.setName("LinkedListMultimap.create()").build());

      } else if (isQualified(association)) {
        attribute.setInitial(expressionBuilder.setName("LinkedListMultimap.create()").build());

      } else if (isMultiple(association) && isOrdered(association)) {
        attribute.setInitial(expressionBuilder.setName("new java.util.ArrayList<>()").build());

      } else if (isMultiple(association) && !isOrdered(association)) {
        attribute.setInitial(expressionBuilder.setName("new LinkedList<>()").build());

      } else if (isOptional(association)) {
        attribute.setInitial(expressionBuilder.setName("Optional.empty()").build());
      }
    }
    return attrList;
  }

  protected List<ASTCDMethod> createMethods(ASTCDClass builderClass, ASTCDClass clazz, CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    methods.addAll(createGetterSetterMethods(clazz, typeSymbol, builderClass));
    methods.add(createBuildMethod(clazz));
    methods.add(createBuildUpdateMethod(clazz, typeSymbol));
    return methods;
  }

  private List<ASTCDMethod> createGetterSetterMethods(ASTCDClass clazz, CDTypeSymbol typeSymbol, ASTCDClass builderClass) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    List<ASTCDAttribute> inheritedAttributes = getInheritedAttributes(clazz);
    List<ASTCDAttribute> attributes = getVisibleNotDerivedAttributesInHierarchy(clazz);
    attributes.removeAll(inheritedAttributes);

    if (!typeSymbol.isPresentSuperClass()) {
      // attributes.add(getCDAttributeFacade().createAttribute(PROTECTED.build(), "Long", UMLP_ID_NAME));
    }

    for (ASTCDAttribute attribute : attributes) {
      methodList.add(createSetterMethod(builderClass.getName(), attribute));
      methodList.add(createGetterMethod(attribute));
    }

    for (ASTCDAttribute inheritedAttribute : inheritedAttributes) {
      methodList.add(createInheritedSetMethod(builderClass.getName(), inheritedAttribute.getSymbol()));
    }

//    for (CDRoleSymbol association : getAllNonDerivedAssociationsForClass(clazz)) {
//      if (isQualified(association)) {
//        methodList.addAll(getMethodsForQualifiedAssociation(association, clazz.getName()));
//
//      } else if (isMultiple(association)) {
//        methodList.add(createSetMultipleAssociationMethod(builderClass.getName(), association));
//        methodList.add(createAddMultipleAssociationMethod(builderClass.getName(), association));
//
//      } else {
//        methodList.add(createSetAssociationMethod(builderClass.getName(), association));
//      }
//      methodList.add(createGetMethod(association));
//    }
    return methodList;
  }

  private ASTCDMethod createSetterMethod(String builderName, ASTCDAttribute attribute) {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(attribute.getMCType(), attribute.getName());
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), builderName, attribute.getName(), parameter);
    getGlex().replaceTemplate(CD2JavaTemplates.EMPTY_BODY, method,
            new StringHookPoint("this." + attribute.getName() + " = " + attribute.getName() + "; return this;"));
    return method;
  }

  private ASTCDMethod createInheritedSetMethod(String builderName, FieldSymbol field) {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(field.getType().print(), field.getName());
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), builderName, field.getName(), parameter);
    getGlex().replaceTemplate(CD2JavaTemplates.EMPTY_BODY, method,
            new StringHookPoint("super." + field.getName() + "(" + field.getName() + "); return this;"));
    return method;
  }

  private ASTCDMethod createSetAssociationMethod(String builderName, CDRoleSymbol association) {
    ASTCDParameter parameter = associationToCDParameter(association);
    ASTModifier modifier = isOppositeQualified(association) ? PROTECTED.build() : PUBLIC.build();
    String associationName = getRoleName(association);
    ASTCDMethod method = getCDMethodFacade().createMethod(modifier, builderName,
            "set" + capitalize(associationName), parameter);
    getGlex().replaceTemplate(CD2JavaTemplates.EMPTY_BODY, method,
            new StringHookPoint("{ this." + TransformationUtils.uncapitalize(associationName)
                    + "= " + associationName + "; return this;}"));
    return method;
  }

  private ASTCDMethod createSetMultipleAssociationMethod(String builderName, CDRoleSymbol association) {
    ASTCDParameter parameter = associationToCDParameter(association);
    String associationName = getRoleName(association);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), builderName, associationName, parameter);
    getGlex().replaceTemplate(CD2JavaTemplates.EMPTY_BODY, method,
            new StringHookPoint("{ this." + associationName + "= " + associationName + "; return this;}"));
    return method;
  }

  private ASTCDMethod createAddMultipleAssociationMethod(String builderName, CDRoleSymbol association) {
    String associationName = getRoleName(association);
    ASTCDParameter parameter = getCDParameterFacade().createParameter(association.getType().print(), associationName);

    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), builderName,
            "add" + capitalize(associationName), parameter);
    getGlex().replaceTemplate(CD2JavaTemplates.EMPTY_BODY, method,
            new StringHookPoint("this." + associationName + ".add(" + associationName + "); return this;"));
    return method;
  }

  private ASTCDMethod createGetMethod(CDRoleSymbol association) {
    ASTCDParameter parameter = associationToCDParameter(association);
    String associationName = getRoleName(association);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), parameter.getMCType(),
            makeCamelCase("get", capitalize(associationName)));
    getGlex().replaceTemplate(CD2JavaTemplates.EMPTY_BODY, method,
            new StringHookPoint("return this." + associationName + ";"));
    return method;
  }

  private List<ASTCDMethod> getMethodsForQualifiedAssociation(CDRoleSymbol association, String className) {
    List<ASTCDMethod> methodList = Lists.newLinkedList();
    ASTCDParameter param = associationToCDParameter(association);

    // add method
    String associationName = getRoleName(association);
    ASTCDParameter setMethodParameter = getCDParameterFacade().createParameter(param.getMCType(), associationName);
    ASTModifier modifier = isOppositeQualified(association) ? PROTECTED.build() : PUBLIC.build();
    ASTCDMethod setMethod = getCDMethodFacade().createMethod(modifier, className + BUILDER_SUFFIX,
            param.getName(), setMethodParameter);
    getGlex().replaceTemplate(CD2JavaTemplates.EMPTY_BODY, setMethod,
            new StringHookPoint("this." + associationName + " = " + associationName + "; return this;"));
    methodList.add(setMethod);

    // create add method
    ASTCDParameter addMethodParameter = getCDParameterFacade().createParameter(association.getOtherSide().getType().print(), associationName);
    ASTCDMethod addMethod = getCDMethodFacade().createMethod(PUBLIC.build(), className + BUILDER_SUFFIX,
            "add" + capitalize(param.getName()), addMethodParameter);

    getGlex().replaceTemplate(CD2JavaTemplates.EMPTY_BODY, addMethod,
            new StringHookPoint("this." + associationName
                    + ".put(k," + associationName + "); return this;"));
    methodList.add(addMethod);
    return methodList;
  }

  protected void addImports(ASTCDClass clazz) {
    CD4C instance = CD4C.getInstance();
    instance.addImport(clazz, "java.util.List");
    instance.addImport(clazz, "java.util.ArrayList");
    instance.addImport(clazz, "java.util.LinkedList");
    instance.addImport(clazz, "java.util.Optional");

    getGlex().replaceTemplate("cd2java.Imports", clazz, new StringHookPoint(""));
  }

  private ASTCDMethod createBuildMethod(ASTCDClass clazz) {
    ASTCDMethod buildMethod = getCDMethodFacade().createMethod(PUBLIC.build(), clazz.getName(),
            "build");
//    ASTCDMethod buildMethod = getCDMethodFacade().createMethod(PUBLIC.build(), clazz.getName(),
//            "build", "ValidationException");
    getGlex().replaceTemplate(CD2JavaTemplates.EMPTY_BODY, buildMethod, new StringHookPoint("return build(false);"));
    return buildMethod;
  }

  private ASTCDMethod createBuildUpdateMethod(ASTCDClass clazz, CDTypeSymbol typeSymbol) {
    ASTCDParameter buildMethodParameter = getCDParameterFacade().createParameter("boolean", "addToBidirectionalAssociation");
    ASTCDMethod buildMethod = getCDMethodFacade().createMethod(PUBLIC.build(), clazz.getName(), "build", buildMethodParameter);
//    ASTCDMethod buildMethod = getCDMethodFacade().createMethod(PUBLIC.build(), clazz.getName(), "build",
//            "ValidationException", buildMethodParameter);
    List<ASTCDParameter> constructorParameters = getConstructorParameters(clazz, false);
    List<CDRoleSymbol> allAssociations = getAllAssociations(clazz);
    getGlex().replaceTemplate(CD2JavaTemplates.EMPTY_BODY, buildMethod, new TemplateHookPoint(
            "umlp.builder.Build", typeSymbol, constructorParameters, allAssociations));
    return buildMethod;
  }

  protected void applyTopMechanism(ASTCDClass cdClass) {
    makeAbstract(cdClass);
    cdClass.setName(cdClass.getName() + TOP_SUFFIX);
    cdClass.getCDConstructorList().forEach(constructor ->
            constructor.setName(constructor.getName() + TOP_SUFFIX));
  }

  protected void makeAbstract(ASTCDType type) {
    makeAbstract(type.getModifier());
  }

  protected void makeAbstract(ASTModifier modifier) {
    modifier.setAbstract(true);
  }
}
