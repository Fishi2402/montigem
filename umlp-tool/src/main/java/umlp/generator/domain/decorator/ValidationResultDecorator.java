package umlp.generator.domain.decorator;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDAttributeTOP;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class ValidationResultDecorator extends AbstractDecorator {
  public static final String SUFFIX = "ValidationResult";
  public ValidationResultDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    String packageName = astcdPackage.getMCQualifiedName().getQName();
    origCD.getCDDefinition().getCDClassesList().stream()
            .filter(c -> !c.getSymbol().isIsDerived())
            .filter(c -> !c.getSymbol().isIsAbstract())
            .forEach(c -> astcdPackage.addCDElement(withOrigin(createValidatorClassForDomainClass(c, origCD, packageName))));
  }

  protected void addImports(ASTCDClass validatorClass) {
    getCD4C().addImport(validatorClass, PathUtil.getBackendRTECommonPath("ValidationMessage"));
    getCD4C().addImport(validatorClass, PathUtil.getBackendRTECommonPath("ValidationStatus"));
    getCD4C().addImport(validatorClass, PathUtil.getBackendRTECommonPath("DomainValidationResult"));
  }

  protected ASTCDElement createValidatorClassForDomainClass(ASTCDType domainClass, ASTCDCompilationUnit origCD, String packageName) {
    String className = domainClass.getName();

    ASTCDClass validationResultClass = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(className + SUFFIX)
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("DomainValidationResult"))
            .build();

    getCD4C().addConstructor(validationResultClass, getConstructorTemplate(), className);
    getCD4C().addConstructor(validationResultClass, getIdConstructorTemplate(), className);

    List<ASTCDAttribute> attributes = origCD.getCDDefinition().getCDClassesList().stream()
            .filter(c -> c.getName().equals(className))
            .findFirst().get().getCDAttributeList();
    List<ASTCDAssociation> rightAssociations = origCD.getCDDefinition().getCDAssociationsList().stream()
            .filter(a -> a.getLeftQualifiedName().getQName().equals(className))
            .collect(Collectors.toList());
    List<ASTCDAssociation> leftAssociations = origCD.getCDDefinition().getCDAssociationsList().stream()
            .filter(a -> a.getRightQualifiedName().getQName().equals(className))
            .collect(Collectors.toList());

    createAttributes(validationResultClass, attributes, leftAssociations, rightAssociations);
    createMethods(validationResultClass, domainClass.getName(), packageName, attributes, leftAssociations, rightAssociations);
    addImports(validationResultClass);

    return validationResultClass;
  }

  private void createAttributes(ASTCDType validationResultClass, List<ASTCDAttribute> attributes, List<ASTCDAssociation> leftAssociations, List<ASTCDAssociation> rightAssociations) {
    getCD4C().addAttribute(validationResultClass, getSignatureForTypeAndAttributeName("long", "domainObjectId"));
    for(ASTCDAttribute attribute : attributes) {
      addAttribute(validationResultClass, getListSignatureForTypeAndAttributeName("ValidationMessage", attribute.getName() + "ValidationMessages"), getListInitializer());
    }
    addAttribute(validationResultClass, getListSignatureForTypeAndAttributeName("ValidationMessage", "thisObjectRelated" + "ValidationMessages"), getListInitializer());

    for(ASTCDAssociation association : leftAssociations) {
      addAssociationAttributes(validationResultClass, association, true);
    }
    for(ASTCDAssociation association : rightAssociations) {
      addAssociationAttributes(validationResultClass, association, false);
    }
  }

  private void createMethods(ASTCDType validationResultClass, String className, String packageName, List<ASTCDAttribute> attributes, List<ASTCDAssociation> leftAssociations, List<ASTCDAssociation> rightAssociations) {
    getCD4C().addMethod(validationResultClass, getGetDomainObjectIdTemplate());

    ASTCDMethodSignature getDomainType = getCD4C().addMethod(validationResultClass, getGetDomainTypeTemplate(), packageName + "." + className);
    annotateWithOverride(getDomainType);

    List<String> attributeNames = attributes.stream().map(ASTCDAttributeTOP::getName).collect(Collectors.toList());
    attributeNames.add("thisObjectRelated");
    getCD4C().addMethod(validationResultClass, getShallowCloneTemplate(), className, attributeNames);
    ASTCDMethodSignature hasError = getCD4C().addMethod(validationResultClass, getHasErrorTemplate(), className, attributeNames);
    annotateWithOverride(hasError);

    for(ASTCDAttribute attribute : attributes) {
      getCD4C().addMethod(validationResultClass, getAddValidationMessageTemplate(), className, attribute.getName());
      getCD4C().addMethod(validationResultClass, getGetValidationMessagesTemplate(), attribute.getName());
      getCD4C().addMethod(validationResultClass, getAttributeHasErrorTemplate(), className, attribute.getName());
    }
    // For object related validation messages, such as "object != null or object != otherObject"
    getCD4C().addMethod(validationResultClass, getAddValidationMessageTemplate(), className, "thisObjectRelated");
    getCD4C().addMethod(validationResultClass, getGetValidationMessagesTemplate(), "thisObjectRelated");
    getCD4C().addMethod(validationResultClass, getAttributeHasErrorTemplate(), className, "thisObjectRelated");

    for(ASTCDAssociation association : leftAssociations) {
      addAssociationMethods(validationResultClass, association, true);
    }

    for(ASTCDAssociation association : rightAssociations) {
      addAssociationMethods(validationResultClass, association, false);
    }

    getCD4C().addMethod(validationResultClass, getMergeTemplate(), className + SUFFIX, attributeNames, leftAssociations, rightAssociations);
  }

  private String deriveRelevantTypeForAssociation(ASTCDAssociation association, boolean isLeft) {
    String type = isLeft ? association.getLeft().getMCQualifiedType().printType() : association.getRight().getMCQualifiedType().printType();
    boolean isMultiple = isLeft ? association.getLeft().getCDCardinality().isMult() : association.getRight().getCDCardinality().isMult();
    if(isMultiple) {
      return "List<" + type + SUFFIX + ">";
    } else {
      return type + SUFFIX;
    }
  }

  protected void annotateWithOverride(ASTCDMethodSignature method) {
    annotate(method, "@Override");
  }

  protected void annotate(ASTCDMethodSignature method, String annotation) {
    glex.replaceTemplate("cd2java.Annotations", method, new StringHookPoint(annotation));
  }

  protected String getSignatureForTypeAndAttributeName(String type, String attributeName) {
    return "private " + type + " " + attributeName + ";";
  }

  protected String getListSignatureForTypeAndAttributeName(String type, String attributeName) {
    return "private List<" + type + "> " + attributeName + ";";
  }

  protected String getListInitializer() {
    return "= new ArrayList<>()";
  }

  protected String getGetAssociationDomainValidationResultsTemplate() {
    return "umlp.validator.validationResult.GetAssociationDomainValidationResults";
  }

  protected String getAddAssociationDomainValidationResultTemplate() {
    return "umlp.validator.validationResult.AddAssociationDomainValidationResult";
  }

  protected String getConstructorTemplate() {
    return "umlp.validator.validationResult.Constructor";
  }

  protected String getIdConstructorTemplate() {
    return "umlp.validator.validationResult.IdConstructor";
  }

  protected String getHasErrorTemplate() {
      return "umlp.validator.validationResult.HasErrors";
  }

  protected String getGetDomainTypeTemplate() {
    return "umlp.validator.validationResult.GetDomainType";
  }

  protected String getAddValidationMessageTemplate() {
      return "umlp.validator.validationResult.AddValidationMessage";
  }

  protected String getGetValidationMessagesTemplate() {
    return "umlp.validator.validationResult.GetValidationMessages";
  }

  protected String getGetAssociationDomainValidationResultTemplate() {
    return "umlp.validator.validationResult.GetAssociationDomainValidationResult";
  }

  protected String getSetAssociationDomainValidationResultTemplate() {
    return "umlp.validator.validationResult.SetAssociationDomainValidationResult";
  }

  protected String getAttributeHasErrorTemplate() {
    return "umlp.validator.validationResult.AttributeHasError";
  }

  protected String getHasAssociationValidationResultTemplate() {
    return "umlp.validator.validationResult.HasAssociationDomainValidationResult";
  }

  protected String getMergeTemplate() {
    return "umlp.validator.validationResult.Merge";
  }

  protected String getShallowCloneTemplate() {
    return "umlp.validator.validationResult.ShallowClone";
  }

  protected String getGetDomainObjectIdTemplate() {
    return "umlp.validator.validationResult.GetDomainObjectId";
  }

  private void addAssociationAttributes(ASTCDType validationResultClass, ASTCDAssociation association, boolean isLeft) {
    String type = deriveRelevantTypeForAssociation(association, isLeft);
    String name = association.getLeft().getMCQualifiedType().printType().equals(association.getRight().getMCQualifiedType().printType()) ? (isLeft ? "left" : "right") + StringTransformations.capitalize(association.getName()) + SUFFIX : association.getName() + SUFFIX;
    boolean isMultiple = type.startsWith("List<");
    if(isMultiple) {
      ASTCDAttribute listAttr = getCD4C().addAttribute(validationResultClass, getListSignatureForTypeAndAttributeName(type.substring(5, type.length()-1), name + "s"));
      glex.replaceTemplate("cd2java.Value", listAttr, new StringHookPoint(getListInitializer()));
    } else {
      getCD4C().addAttribute(validationResultClass, getSignatureForTypeAndAttributeName(type, name));
    }
  }

  private void addAssociationMethods(ASTCDType validationResultClass, ASTCDAssociation association, boolean isLeft) {
    String type = deriveRelevantTypeForAssociation(association, isLeft);
    String name = association.getLeft().getMCQualifiedType().printType().equals(association.getRight().getMCQualifiedType().printType()) ? (isLeft ? "left" : "right") + StringTransformations.capitalize(association.getName()) : association.getName();
    boolean isMultiple = type.startsWith("List<");
    if(isMultiple) {
      type = type.substring(5, type.length() - 1);
      getCD4C().addMethod(validationResultClass, getGetAssociationDomainValidationResultsTemplate(), type, name);
      getCD4C().addMethod(validationResultClass, getAddAssociationDomainValidationResultTemplate(), type, name);
    } else {
      getCD4C().addMethod(validationResultClass, getGetAssociationDomainValidationResultTemplate(), type, name);
      getCD4C().addMethod(validationResultClass, getSetAssociationDomainValidationResultTemplate(), type, name);
      getCD4C().addMethod(validationResultClass, getHasAssociationValidationResultTemplate(), name);
    }
  }
}
