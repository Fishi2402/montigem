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

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.GenService.createInitialValue;
import static umlp.generator.domain.decorator.GenService.createType;

public class BuilderDecorator extends AbstractDecorator {

  public static final String BUILDER_SUFFIX = "Builder";

  public BuilderDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    String name = origCD.getCDDefinition().getName();
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
      .forEach(c -> astcdPackage.addCDElement(createClass(c, name)));
  }

  protected ASTCDClass createClass(ASTCDClass domainClass, String name) {
    String className = domainClass.getName() + BUILDER_SUFFIX;
    String managerName = name + ManagerDecorator.SUFFIX;

    ASTCDClass builderClass = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(domainClass.getName()))
            .setName(className).build();

    List<ASTCDAttribute> attrFields = CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
            .filter(a -> GenService.isVisibleNotDerived(a)).collect(Collectors.toList());
    List<ASTCDAssocSide> attrAssocs = CDSymbolTables.getAssociationsInHierarchy(domainClass).stream()
            .filter(a -> GenService.isVisibleNotDerived(a)).collect(Collectors.toList());
    List<ASTCDAssocSide> attrAssocLists = attrAssocs.stream()
        .filter(GenService::isList)
        .collect(Collectors.toList());

    // Constructors
    getCD4C().addConstructor(builderClass, "umlp.builder.Constructor", domainClass.getName(), managerName, attrAssocLists);

    // Attributes
    List<ASTCDAttribute> attrList = createAttributes(domainClass, attrFields.stream().map(a -> a.getName()).collect(Collectors.toList()), false);
    attrList.addAll(createAttributes(domainClass, attrAssocs.stream()
        .filter(Predicate.not(attrAssocLists::contains))
        .map(a -> a.getName()).collect(Collectors.toList()),
        false));
    attrList.addAll(createAttributes(domainClass, attrAssocLists.stream().map(a -> a.getName()).collect(Collectors.toList()), true));
    builderClass.addAllCDMembers(attrList);
    ASTCDAttribute finishedAttr = getCD4C().addAttribute(builderClass, "protected boolean buildFinished = false");
    ASTCDAttribute internalAttr = getCD4C().addAttribute(builderClass, "protected boolean INTERNAL_deser_mode = false");

    // Methods
    MethodDecorator methodDecorator = new MethodDecorator(glex);
    attrList.forEach(a -> builderClass.addAllCDMembers(methodDecorator.decorate(a)));
    builderClass.addAllCDMembers(methodDecorator.decorate(finishedAttr));
    builderClass.addAllCDMembers(methodDecorator.decorate(internalAttr));
    createSetAndCheckMethods(domainClass, builderClass, attrFields, attrAssocs);
    getCD4C().addMethod(builderClass, "umlp.builder.Build", domainClass.getName(), attrAssocLists);
    getCD4C().addMethod(builderClass, "umlp.builder.IsBuildable", attrList);

    // Imports
    addImports(builderClass);

    return builderClass;
  }

  protected List<ASTCDAttribute> createAttributes(ASTCDClass domainClass, List<String> attrNames, boolean initialValue) {
    List<ASTCDAttribute> attributesList = Lists.newLinkedList();

    // get all visible parameters
    for (String name : attrNames) {
      ASTMCPrimitiveType type = getMCTypeFacade().createBooleanType();
      type.setEnclosingScope(BasicSymbolsMill.globalScope());
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(CDModifier.PROTECTED.build(),
              type,  name + "Present");
      attribute.getModifier().setReadonly(true);
      attribute.setInitial(UMLPMill.nameExpressionBuilder().setName(String.valueOf(initialValue)).build());
      attributesList.add(attribute);
    }
    return attributesList;
  }

  protected void createSetAndCheckMethods(ASTCDClass domainClass, ASTCDClass builderClass,
                                          List<ASTCDAttribute> attrList,
                                          List<ASTCDAssocSide> assocList) {
    for (ASTCDAttribute attribute : attrList) {
      getCD4C().addMethod(builderClass, "umlp.builder.Attr",
              attribute.getName(), getCDPrinter().prettyprint(createType(attribute)),
              domainClass.getName() + BUILDER_SUFFIX);
      getCD4C().addMethod(builderClass, "umlp.builder.Check",
              attribute.getName(), domainClass.getName());
    }
    for (ASTCDAssocSide assoc : assocList) {
      getCD4C().addMethod(builderClass, "umlp.builder.Check",
              assoc.getName(), domainClass.getName());
      if (GenService.isList(assoc)) {
        getCD4C().addMethod(builderClass, "umlp.builder.AssocList",
                assoc.getName(), GenService.getType(assoc.getSymbol().getType()),
                domainClass.getName() + BUILDER_SUFFIX);
        getCD4C().addMethod(builderClass, "umlp.builder.AssocAdd",
                assoc.getName(), GenService.getType(assoc.getSymbol().getType()),
                domainClass.getName() + BUILDER_SUFFIX);
        getCD4C().addMethod(builderClass, "umlp.builder.AssocGemList",
                assoc.getName(),
                domainClass.getName() + BUILDER_SUFFIX);

      } else {
        getCD4C().addMethod(builderClass, "umlp.builder.AssocOne",
                assoc.getName(), GenService.getType(assoc.getSymbol().getType()),
                domainClass.getName() + BUILDER_SUFFIX);
        getCD4C().addMethod(builderClass, "umlp.builder.AssocOneGem",
                assoc.getName(), GenService.getType(assoc.getSymbol().getType()),
                domainClass.getName() + BUILDER_SUFFIX);
      }
    }
  }

  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, "java.util.List");
    getCD4C().addImport(clazz, "java.util.Optional");
    getCD4C().addImport(clazz, "umlp.rte.NotificationScope");
    getCD4C().addImport(clazz, "umlp.rte.Gem");
  }

}
