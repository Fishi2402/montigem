/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.decorator;

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import umlp.UMLPMill;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.decorator.GenService.createInitialValue;

public class ErrorCollectorDecorator extends AbstractDecorator {

  public static final String SUFFIX = "ErrorCollector";

  public ErrorCollectorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    String name = origCD.getCDDefinition().getName();
    origCD.getCDDefinition().getCDClassesList().forEach(c -> astcdPackage.addCDElement(createClass(c, name)));
  }

  protected ASTCDClass createClass(ASTCDClass domainClass, String name) {
    String className = domainClass.getName() + SUFFIX;

    ASTCDClass collectorClass = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(className).build();

    // Constructors

    // Attributes
    List<ASTCDAttribute> attrFields = getAttributesForFields(domainClass);
    List<ASTCDAttribute> attrAssocs = getAttributesForAssociations(domainClass);
    List<ASTCDAttribute> attrList = Lists.newArrayList(attrFields);
    attrList.addAll(attrAssocs);
    collectorClass.addAllCDMembers(attrList);

    /* Variante ohne CD4C
    collectorClass.addCDMember(getCDAttributeFacade()
            .createAttribute(PROTECTED.build(),domainClass.getName()+"Validator", "validator)"));
    collectorClass.addCDMember(getCDAttributeFacade()
            .createAttribute(PROTECTED.build(),"boolean", "buildFinished)"));
    */

    getCD4C().addAttribute(collectorClass, "protected static " + domainClass.getName() + "ErrorCollector instance;");
    // TODO:  CD4Code kann das nicht parsen "= new ArrayList<>()". Ist das erlaubt?
    getCD4C().addAttribute(collectorClass, "protected List<" + domainClass.getName() + "ErrorObserver> observers;");

    // Methods
    getCD4C().addMethod(collectorClass, "umlp.errorcollector.Init", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.errorcollector.GetInstance", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.errorcollector.AddErrorObserver", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.errorcollector._AddErrorObserver", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.errorcollector.ContainsErrorObserver", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.errorcollector._ContainsErrorObserver", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.errorcollector.RemoveErrorObserver", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.errorcollector._RemoveErrorObserver", domainClass.getName());

    // Imports
    addImports(collectorClass);

    return collectorClass;
  }

  protected List<ASTCDAttribute> getAttributesForFields(ASTCDClass domainClass) {
    List<ASTCDAttribute> attributesList = Lists.newLinkedList();
    List<ASTCDAttribute> visibleAttributes = domainClass.getCDAttributeList().stream()
            .filter(a -> GenService.isVisible(a)).collect(Collectors.toList());

    // get all visible parameters
    for (ASTCDAttribute visibleAttribute : visibleAttributes) {
      ASTMCPrimitiveType type = getMCTypeFacade().createBooleanType();
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(CDModifier.PROTECTED.build(),
              type,  visibleAttribute.getName() + "Present");
      attribute.setInitial(createInitialValue(type));
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
      // TODO: Was muss hier gemacht werden
    }
    return attrList;
  }

  protected void createSetAndCheckMethods(ASTCDClass domainClass, ASTCDClass builderClass,
                                          List<ASTCDAttribute> attrList) {
    for (ASTCDAttribute attribute : attrList) {
      getCD4C().addMethod(builderClass, "umlp.builder.Attr",
              attribute.getName(), getCDPrinter().prettyprint(attribute.getMCType()), domainClass.getName());
      getCD4C().addMethod(builderClass, "umlp.builder.Check",
              attribute.getName(), domainClass.getName());
    }
  }

  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, "java.util.List");
    getCD4C().addImport(clazz, "java.util.ArrayList");
  }

}
