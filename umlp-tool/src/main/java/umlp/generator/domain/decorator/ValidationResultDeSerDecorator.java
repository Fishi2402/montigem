/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cd.facade.CDModifier;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ValidationResultDeSerDecorator extends AbstractDecorator {

  public static final String SUFFIX = "ValidationResultDeSer";

  public ValidationResultDeSerDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    String cdName = origCD.getCDDefinition().getName();
    getConcreteDomainClasses(origCD).forEach(c -> astcdPackage.addCDElement(withOrigin(createDeSer(origCD,
                                                                                                   cdName,
                                                                                                   c))));
  }

  protected ASTCDElement createDeSer(ASTCDCompilationUnit origCD, String cdName, ASTCDClass cdClass) {
    String className = cdClass.getName();

    ASTCDClass deSer = UMLPMill.cDClassBuilder()
        .setName(cdClass.getName() + SUFFIX)
        .setModifier(CDModifier.PUBLIC.build())
        .setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage("JsonDeSer<" + cdClass.getName() + ValidationResultDecorator.SUFFIX +  ">"))
        .build();

    List<ASTCDAttribute> attributes = cdClass.getCDAttributeList();
    List<String> attributeNames = attributes.stream().map(ASTCDAttributeTOP::getName).collect(Collectors.toList());
    attributeNames.add("thisObjectRelated");
    List<String> associationTypes = new ArrayList<>();
    List<String> associationNames = new ArrayList<>();
    List<String> listAssociationTypes = new ArrayList<>();
    List<String> listAssociationNames = new ArrayList<>();

    for(ASTCDAssociation association : origCD.getCDDefinition().getCDAssociationsList().stream()
            .filter(association -> association.getLeftQualifiedName().getQName().equals(className)
                    || association.getRightQualifiedName().getQName().equals(className))
            .collect(Collectors.toList())) {
      String type;
      Optional<String> leftType = Optional.empty();
      boolean leftTypeIsMultiple = false;
      boolean isMultiple;
      String rightName = association.getRightQualifiedName().getQName();
      String leftName = association.getLeftQualifiedName().getQName();

      String assocName = StringTransformations.uncapitalize(association.getName());

      if(leftName.equals(className)) {
        if(rightName.equals(className)) {
          leftType = Optional.of(className);
          leftTypeIsMultiple = association.getLeft().getCDCardinality().isMult();
        }
        type = rightName;
        isMultiple = association.getRight().getCDCardinality().isMult();
      } else {
        type = origCD.getCDDefinition().getCDClassesList().stream().filter(c -> c.getName().equals(association.getLeftQualifiedName().getQName())).findFirst().get().getName();
        isMultiple = association.getLeft().getCDCardinality().isMult();
      }

      if(isMultiple){
        listAssociationTypes.add(type);
        listAssociationNames.add(leftType.isPresent() ? "right" + StringTransformations.capitalize(assocName) : assocName);
      } else {
        associationTypes.add(type);
        associationNames.add(leftType.isPresent() ? "right" + StringTransformations.capitalize(assocName) : assocName);
      }

      if(leftType.isPresent()) {
        if(leftTypeIsMultiple) {
          listAssociationTypes.add(leftType.get());
          listAssociationNames.add("left" + StringTransformations.capitalize(assocName));
        } else {
          associationTypes.add(leftType.get());
          associationNames.add("left" + StringTransformations.capitalize(assocName));
        }
      }
    }

    getCD4C().addMethod(deSer, getSerializeTemplate(), cdClass.getName());
    getCD4C().addMethod(deSer, getDeserializeTemplate(),
            cdClass.getName(),
            attributeNames,
            associationTypes,
            associationNames,
            listAssociationTypes,
            listAssociationNames
    );
    getCD4C().addMethod(deSer, getBuildJsonTemplate(),
            cdClass.getName(),
            attributeNames,
            associationTypes,
            associationNames,
            listAssociationTypes,
            listAssociationNames
    );
    getCD4C().addMethod(deSer, getFromJsonTemplate(), cdClass.getName());

    addImports(cdClass, deSer);

    return deSer;
  }

  protected String getSerializeTemplate() {
    return "umlp.validator.deser.Serialize";
  }

  protected String getDeserializeTemplate() {
    return "umlp.validator.deser.Deserialize";
  }

  protected String getBuildJsonTemplate() {
    return "umlp.validator.deser.BuildJson";
  }

  protected String getFromJsonTemplate() {
    return "umlp.validator.deser.FromJson";
  }

  protected void addImports(ASTCDType domainType, ASTCDType clazz) {
    getCD4C().addImport(clazz, "de.monticore.symboltable.serialization.JsonPrinter");
    getCD4C().addImport(clazz, "de.monticore.symboltable.serialization.json.JsonObject");
    getCD4C().addImport(clazz, "de.monticore.symboltable.serialization.json.JsonElement");
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("ValidationMessage"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("ValidationMessageDeSer"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("ValidationStatus"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("Gem"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("JsonDeSer"));
  }

}
