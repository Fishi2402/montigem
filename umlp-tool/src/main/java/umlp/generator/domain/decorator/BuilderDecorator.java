/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.GenService.createType;

public class BuilderDecorator extends AbstractDecorator {

  public static final String BUILDER_SUFFIX = "Builder";

  private ASTCDCompilationUnit origCD;

  public BuilderDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.origCD = origCD;
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    String name = origCD.getCDDefinition().getName();
    origCD.getCDDefinition().getCDClassesList().stream()
        .filter(c -> !c.getModifier().isDerived())
        .filter(c -> !c.getModifier().isAbstract())
        .forEach(c -> astcdPackage.addCDElement(withOrigin(createClass(c, name, origCD))));
  }

  public static String getClassName(String domainClassName) {
    return domainClassName + BUILDER_SUFFIX;
  }

  protected ASTCDClass createClass(ASTCDClass domainClass, String name, ASTCDCompilationUnit origCD) {
    String className = getClassName(domainClass.getName());
    String managerName = name + ManagerDecorator.SUFFIX;

    ASTCDClass builderClass = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(domainClass.getName()))
        .setName(className).build();

    List<ASTCDAttribute> attrFields = CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
        .filter(GenService::isVisibleNotDerived)
        .collect(Collectors.toList());
    List<ASTCDAssocSide> attrAssocs = CDSymbolTables.getAssociationsInHierarchy(domainClass).stream()
        .filter(GenService::isVisibleNotDerived)
        .filter(Predicate.not(GenService::isSymbolDerived))
        .collect(Collectors.toList());
    List<ASTCDAssocSide> attrAssocLists = attrAssocs.stream()
        .filter(GenService::isList)
        .collect(Collectors.toList());

    // Constructors
    addConstructor(builderClass, domainClass, managerName, attrAssocLists);

    // Attributes
    List<ASTCDAttribute> attrList = createAttributes(domainClass, attrFields.stream().map(ASTCDAttributeTOP::getName).collect(Collectors.toList()), false);
    attrList.addAll(createAttributes(domainClass, attrAssocs.stream()
            .filter(Predicate.not(attrAssocLists::contains))
            .map(ASTCDAssocSide::getName).collect(Collectors.toList()),
        false));
    attrList.addAll(createAttributes(domainClass, attrAssocLists.stream().map(ASTCDAssocSide::getName).collect(Collectors.toList()), true));
    builderClass.addAllCDMembers(attrList);
    ASTCDAttribute finishedAttr = getCD4C().addAttribute(builderClass, "protected boolean buildFinished = false");
    ASTCDAttribute internalAttr = getCD4C().addAttribute(builderClass, "protected boolean INTERNAL_deser_mode = false");

    // Methods
    MethodDecorator methodDecorator = new MethodDecorator(glex);
    attrList.forEach(a -> builderClass.addAllCDMembers(methodDecorator.decorate(a)));
    builderClass.addAllCDMembers(methodDecorator.decorate(finishedAttr));
    builderClass.addAllCDMembers(methodDecorator.decorate(internalAttr));
    createSetAndCheckMethods(domainClass, builderClass, attrFields, attrAssocs);
    getCD4C().addMethod(builderClass, getBuildTemplate(),
        domainClass.getName(),
        managerName,
        attrAssocLists
    );
    // TODO: filter by mandatory
    List<ASTCDAttribute> mandatoryAttrList = attrList;
    getCD4C().addMethod(builderClass, getIsBuildableTemplate(), mandatoryAttrList);

    // Imports
    addImports(domainClass, builderClass);
    attrAssocLists.forEach(assocSide -> addCorrectMangerImportForTargetType(origCD.getCDDefinition(), builderClass, assocSide.getSymbol().getType()));

    return builderClass;
  }

  protected void addConstructor(
      ASTCDClass builderClass, ASTCDClass domainClass,
      String managerName, List<ASTCDAssocSide> attrAssocLists) {
    HashMap<ASTCDAssocSide, String> correctManagerToAttrAssocSides = new HashMap<>();
    attrAssocLists.forEach(assocSide -> correctManagerToAttrAssocSides.put(assocSide, getCorrectManager(assocSide)));
    getCD4C().addConstructor(builderClass, getConstructorTemplate(), domainClass.getName(), correctManagerToAttrAssocSides);
  }

  protected String getConstructorTemplate() {
    return "umlp.builder.Constructor";
  }

  protected String getBuildTemplate() {
    return "umlp.builder.Build";
  }

  protected String getIsBuildableTemplate() {
    return "umlp.builder.IsBuildable";
  }

  protected List<ASTCDAttribute> createAttributes(ASTCDClass domainClass, List<String> attrNames, boolean initialValue) {
    List<ASTCDAttribute> attributesList = Lists.newLinkedList();

    // get all visible parameters
    for (String name : attrNames) {
      ASTMCPrimitiveType type = getMCTypeFacade().createBooleanType();
      type.setEnclosingScope(BasicSymbolsMill.globalScope());
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(CDModifier.PROTECTED.build(),
          type, name + "Present");
      attribute.getModifier().setReadonly(true);
      attribute.setInitial(UMLPMill.nameExpressionBuilder().setName(String.valueOf(initialValue)).build());
      attributesList.add(attribute);
    }
    return attributesList;
  }

  protected void createSetAndCheckMethods(ASTCDClass domainClass, ASTCDClass builderClass, List<ASTCDAttribute> attrList, List<ASTCDAssocSide> assocList) {
    for (ASTCDAttribute attribute : attrList) {
      getCD4C().addMethod(builderClass, "umlp.builder.Attr",
          attribute.getName(), getCDPrinter().prettyprint(createType(attribute)),
          getClassName(domainClass.getName()));
      getCD4C().addMethod(builderClass, "umlp.builder.Check",
          attribute.getName(), domainClass.getName(),
          !GenService.isList(attribute) && !GenService.isOptional(attribute));
    }
    for (ASTCDAssocSide assoc : assocList) {
      getCD4C().addMethod(builderClass, "umlp.builder.Check",
          assoc.getName(), domainClass.getName(),
          !GenService.isOptional(assoc));
      if (GenService.isList(assoc)) {
        if (!GenService.isSymbolDerived(assoc)) {
          getCD4C().addMethod(builderClass, "umlp.builder.AssocAdd",
              assoc.getName(), GenService.getType(assoc.getSymbol().getType()),
              getClassName(domainClass.getName()));
          getCD4C().addMethod(builderClass, "umlp.builder.AssocGemList",
              assoc.getName(),
              getClassName(domainClass.getName()));
          getCD4C().addMethod(builderClass, "umlp.builder.AssocList",
              assoc.getName(), GenService.getType(assoc.getSymbol().getType()),
              getClassName(domainClass.getName()));
        }
      } else {
        getCD4C().addMethod(builderClass, "umlp.builder.AssocOne",
            assoc.getName(), GenService.getType(assoc.getSymbol().getType()),
            getClassName(domainClass.getName()));
        getCD4C().addMethod(builderClass, "umlp.builder.AssocOneGem",
            assoc.getName(), GenService.getType(assoc.getSymbol().getType()),
            getClassName(domainClass.getName()));
      }
    }
  }

  protected void addImports(ASTCDType domainType, ASTCDType clazz) {
    super.addImports(domainType, clazz);
    getCD4C().addImport(clazz, "java.util.List");
    getCD4C().addImport(clazz, "java.util.Optional");
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("Gem"));
  }

}
