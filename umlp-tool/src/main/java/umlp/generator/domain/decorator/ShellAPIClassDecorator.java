/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ShellAPIClassDecorator extends DomainClassDecorator {

  public static final String SUFFIX = "ShellAPI";
  public ShellAPIClassDecorator(GlobalExtensionManagement glex, MCPath hwPath) {
    super(glex, hwPath);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, SHELLAPI_PACKAGE);
    String cdName = origCD.getCDDefinition().getName();
    origCD.getCDDefinition().getCDClassesList()
        .stream()
        .filter(cdclass -> !cdclass.getModifier().isDerived())
        .forEach(cdClass -> astcdPackage.addCDElement(createAPI(origCD, cdName, cdClass)));
  }

  protected ASTCDElement createAPI(ASTCDCompilationUnit origCD, String cdName, ASTCDClass cdClass) {
    String originalClassName = cdClass.getName();

    ASTCDClass shellapiClass = UMLPMill.cDClassBuilder()
        .setName(cdClass.getName() + SUFFIX)
        .setModifier(CDModifier.PUBLIC.build())
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(origCD.getCDDefinition().getName() + ShellAPIDecorator.SUFFIX))
        .build();
    glex.replaceTemplate("cd2java.Annotations", shellapiClass, new StringHookPoint("@ShellComponent"));

    addBasicImports(origCD, shellapiClass, originalClassName);
    List<ASTCDAttribute> domainAttributes = CDSymbolTables.getAttributesInHierarchy(cdClass).stream()
        .filter(attr -> !attr.getModifier().isDerived())
        .collect(Collectors.toList());
    addCreateMethod(originalClassName, shellapiClass, domainAttributes);
    addGetMethod(originalClassName, shellapiClass);
    addDeleteMethod(originalClassName, shellapiClass);
    domainAttributes.stream().filter(a -> !GenService.isList(a)).forEach(a -> {
      addAttributeImport(origCD, shellapiClass, a.getName(), originalClassName, "set");
      addAttributeSetMethod(originalClassName, shellapiClass, a);
    });
    domainAttributes.stream().filter(a -> GenService.isList(a)).forEach(a -> {
      addAttributeImport(origCD, shellapiClass, a.getName(), originalClassName, "add");
      addAttributeImport(origCD, shellapiClass, a.getName(), originalClassName, "remove");
      addAttributeAddMethod(originalClassName, shellapiClass, a);
      addAttributeRemoveMethod(originalClassName, shellapiClass, a);
    });

    List<ASTCDAssocSide> assocs = CDSymbolTables.getAssociationsInHierarchy(cdClass).stream()
        .filter(a -> !a.getModifier().isDerived())
        .collect(Collectors.toList());
    assocs.stream().filter(a -> !GenService.isList(a) && !GenService.isBidirectional(a)).forEach(a -> {
      addAttributeImport(origCD, shellapiClass, a.getCDRole().getName(), originalClassName, "set");
      addAssociationSetMethod(originalClassName, shellapiClass, a);
    });
    assocs.stream().filter(a -> GenService.isList(a) && !GenService.isBidirectional(a)).forEach(a -> {
      addAttributeImport(origCD, shellapiClass, a.getName(), originalClassName, "add");
      addAttributeImport(origCD, shellapiClass, a.getName(), originalClassName, "remove");
      addAssociationAddMethod(originalClassName, shellapiClass, a);
      addAssociationRemoveMethod(originalClassName, shellapiClass, a);
    });

    return shellapiClass;
  }

  private void addCreateMethod(String originalClassName, ASTCDClass api,
      List<ASTCDAttribute> domainAttributes) {
    List<String> parameters = new ArrayList<>();
    for (ASTCDAttribute attribute : domainAttributes) {
      parameters.add(attribute.getMCType().printType() + " " + attribute.getName());
    }
    List<String> domainAttributNames = domainAttributes.stream().map(a -> a.getName())
        .collect(Collectors.toList());
    ASTCDMethodSignature m = getCD4C().addMethod(api, "umlp.shellapi.Create",
        originalClassName, parameters.stream().collect(Collectors.joining(", ")), domainAttributNames);
    String annotation = String.format("@ShellMethod(key = \"%s-create\", value = \"Create %s\") @ShellMethodAvailability(\"isConnected\")",
        StringTransformations.uncapitalize(originalClassName), originalClassName);
    glex.replaceTemplate("cd2java.Annotations", m, new StringHookPoint(annotation));
    ArrayList<String> annotations = new ArrayList<>();
    for (ASTCDAttribute attribute : domainAttributes) {
      annotations.add(String.format("@ShellOption(value = \"--%s\")", attribute.getName()));
    }
    addAnnotationToParameters(m, annotations);
  }

  private void addGetMethod(String originalClassName, ASTCDClass api) {
    ASTCDMethodSignature m = getCD4C().addMethod(api, "umlp.shellapi.Get",
        originalClassName);
    String annotation = String.format("@ShellMethod(key = \"%s-get\", value = \"Get %s\") @ShellMethodAvailability(\"isConnected\")",
        StringTransformations.uncapitalize(originalClassName), originalClassName);
    glex.replaceTemplate("cd2java.Annotations", m, new StringHookPoint(annotation));
    addAnnotationToParameters(m, "@ShellOption(value = \"--id\")");
  }

  private void addDeleteMethod(String originalClassName, ASTCDClass api) {
    ASTCDMethodSignature m = getCD4C().addMethod(api, "umlp.shellapi.Delete",
        originalClassName);
    String annotation = String.format("@ShellMethod(key = \"%s-delete\", value = \"Delete %s\") @ShellMethodAvailability(\"isConnected\")",
        StringTransformations.uncapitalize(originalClassName), originalClassName);
    glex.replaceTemplate("cd2java.Annotations", m, new StringHookPoint(annotation));
    addAnnotationToParameters(m, "@ShellOption(value = \"--id\")");
  }

  protected void addAttributeSetMethod(String originalClassName, ASTCDClass api, ASTCDAttribute attr) {
    addAttributeMethod(attr, api, "umlp.shellapi.Attribute_Set", originalClassName,
        "@ShellMethod(key = \"%s-set-%s\", value = \"Set %s of %s\") @ShellMethodAvailability(\"isConnected\")");
  }

  protected void addAttributeGetMethod(String originalClassName, ASTCDClass api, ASTCDAttribute attr) {
    ASTCDMethodSignature m = getCD4C().addMethod(api, "umlp.shellapi.Attribute_Get",
        originalClassName, attr.getName());
    String annotation = String.format("@ShellMethod(key = \"%s-get-%s\", value = \"Get %s of %s\") @ShellMethodAvailability(\"isConnected\")",
        StringTransformations.uncapitalize(originalClassName), attr.getName(), attr.getName(), originalClassName);
    glex.replaceTemplate("cd2java.Annotations", m, new StringHookPoint(annotation));
    addAnnotationToParameters(m, "@ShellOption(value = \"--id\")");
  }

  private void addAttributeAddMethod(String originalClassName, ASTCDClass api, ASTCDAttribute attr) {
    addAttributeMethod(attr, api, "umlp.shellapi.Attribute_Add", originalClassName,
        "@ShellMethod(key = \"%s-add-%s\", value = \"Add %s to %s\") @ShellMethodAvailability(\"isConnected\")");
  }

  private void addAttributeRemoveMethod(String originalClassName, ASTCDClass api, ASTCDAttribute attr) {
    addAttributeMethod(attr, api, "umlp.shellapi.Attribute_Remove", originalClassName,
        "@ShellMethod(key = \"%s-remove-%s\", value = \"Remove %s from %s\") @ShellMethodAvailability(\"isConnected\")");
  }

  private void addAttributeMethod(ASTCDAttribute attr, ASTCDClass api, String template,
      String originalClassName, String methodAnnotation) {
    String type;
    if (GenService.isList(attr)) {
      type = attr.getSymbol().getType().printFullName().replace("List<","").replace(">","");
    } else {
      type = attr.getSymbol().getType().printFullName();
    }
    ASTCDMethodSignature m = getCD4C().addMethod(api, template,
        originalClassName, attr.getName(), type);
    String annotation = String.format(methodAnnotation,
        StringTransformations.uncapitalize(originalClassName), attr.getName(), attr.getName(),
        originalClassName);
    glex.replaceTemplate("cd2java.Annotations", m, new StringHookPoint(annotation));
    addAnnotationToParameters(m, "@ShellOption(value = \"--id\")",
        "@ShellOption(value = \"--value\")");
  }



  protected void addAssociationSetMethod(String originalClassName, ASTCDClass api, ASTCDAssocSide assoc) {
    addAssociationMethod(assoc, api, "umlp.shellapi.Attribute_Set", originalClassName,
        "@ShellMethod(key = \"%s-set-%s\", value = \"Set %s of %s\") @ShellMethodAvailability(\"isConnected\")");
  }

  private void addAssociationAddMethod(String originalClassName, ASTCDClass api, ASTCDAssocSide assoc) {
    addAssociationMethod(assoc, api, "umlp.shellapi.Attribute_Add", originalClassName,
        "@ShellMethod(key = \"%s-add-%s\", value = \"Add %s to %s\") @ShellMethodAvailability(\"isConnected\")");
  }

  private void addAssociationRemoveMethod(String originalClassName, ASTCDClass api, ASTCDAssocSide assoc) {
    addAssociationMethod(assoc, api, "umlp.shellapi.Attribute_Remove", originalClassName,
        "@ShellMethod(key = \"%s-remove-%s\", value = \"Remove %s from %s\") @ShellMethodAvailability(\"isConnected\")");
  }

  private void addAssociationMethod(ASTCDAssocSide assoc, ASTCDClass api, String template,
      String originalClassName, String methodAnnotation) {
    if (GenService.isBidirectional(assoc)) {
      // TODO MH not supported yet
      return;
    }
    String type = "long";
    ASTCDMethodSignature m = getCD4C().addMethod(api, template,
        originalClassName, assoc.getName(), type);
    String annotation = String.format(methodAnnotation,
        StringTransformations.uncapitalize(originalClassName), assoc.getName(), assoc.getName(),
        originalClassName);
    glex.replaceTemplate("cd2java.Annotations", m, new StringHookPoint(annotation));
    addAnnotationToParameters(m, "@ShellOption(value = \"--id\")",
        "@ShellOption(value = \"--value\")");
  }

  protected void addBasicImports(ASTCDCompilationUnit origCD, ASTCDType clazz, String baseClassName) {
    getCD4C().addImport(clazz, "org.springframework.shell.standard.ShellComponent");
    getCD4C().addImport(clazz, "org.springframework.shell.standard.ShellMethod");
    getCD4C().addImport(clazz, "org.springframework.shell.standard.ShellOption");
    getCD4C().addImport(clazz, "org.springframework.shell.standard.ShellMethodAvailability");
    getCD4C().addImport(clazz, getPackageName(origCD, DEFAULT_PACKAGE) + "." + baseClassName);
    getCD4C().addImport(clazz, getPackageName(origCD, DEFAULT_PACKAGE) + "." + baseClassName + "Builder");
    getCD4C().addImport(clazz, getPackageName(origCD, COMMAND_PACKAGE) + "." + baseClassName + "_create");
    getCD4C().addImport(clazz, getPackageName(origCD, COMMAND_PACKAGE) + "." + baseClassName + "_delete");
    getCD4C().addImport(clazz, getPackageName(origCD, COMMAND_PACKAGE) + "." + baseClassName + "_get");
  }

  private void addAttributeImport(ASTCDCompilationUnit origCD, ASTCDType clazz,
      String attributeName, String baseClassName, String getSet) {
    getCD4C().addImport(clazz, getPackageName(origCD, COMMAND_PACKAGE) + "." +
        baseClassName + "_" + getSet + StringTransformations.capitalize(attributeName));
  }

  private void addAnnotationToParameters(ASTCDMethodSignature m, String... annotations) {
    addAnnotationToParameters(m, Arrays.asList(annotations));
  }

  private void addAnnotationToParameters(ASTCDMethodSignature m, List<String> annotations) {
    if (annotations.size() != m.getCDParameterList().size()) {
      return;
    }
    for (int i = 0; i < m.getCDParameterList().size(); i++) {
      m.getCDParameter(i).setAnnotation(Optional.of(annotations.get(i)));
    }
  }
}
