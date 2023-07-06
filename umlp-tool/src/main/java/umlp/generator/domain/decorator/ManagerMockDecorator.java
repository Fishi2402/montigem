/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.GemListAssocDecorator.getName;

public class ManagerMockDecorator extends AbstractDecorator {

  public static final String SUFFIX = "Mock";

  public ManagerMockDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(withOrigin(createClass(origCD)));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass managerClass = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(name + SUFFIX)
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(name + ManagerDecorator.SUFFIX))
            .build();

    // attributes
    getCD4C().addAttribute(managerClass, "static protected long freeGemId = 1001L");
    getCD4C().addAttribute(managerClass, "protected static " + name + "DummyProvider dummyProvider");

    // methods
    getCD4C().addMethod(managerClass, "umlp.managermock.Init", name);
    getCD4C().addMethod(managerClass, "umlp.managermock.DoGetFreeGemId");
    getCD4C().addMethod(managerClass, "umlp.managermock.ErrorNotificationScope");
    getCD4C().addMethod(managerClass, "umlp.managermock.SecretlyAdaptNextGemId");

    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
            .forEach(c -> addClassElements(origCD, c, managerClass, name));

    origCD.getCDDefinition().getCDInterfacesList().stream().filter(i -> !i.getModifier().isDerived())
            .forEach(i -> addInterfaceElements(origCD, i, managerClass));

    // Imports
    addImports(managerClass);

    return managerClass;
  }

  protected void addClassElements(ASTCDCompilationUnit origCD, ASTCDClass domainClass,
                                  ASTCDClass managerClass, String managerName) {
    String name = domainClass.getName();
    String lName = StringTransformations.uncapitalize(name);

    List<ASTCDAssocSide> assocs = CDSymbolTables.getAssociations(domainClass);
    Set<ASTCDType> concreteSubclassesInclSelf = UMLPSymbolTables.collectConcreteSubclassesInclSelf(origCD, domainClass);
    getCD4C().addMethod(managerClass, "umlp.managermock.DoOpt", name, concreteSubclassesInclSelf);
    getCD4C().addMethod(managerClass, "umlp.managermock.DoGetSure", name);
    getCD4C().addMethod(managerClass, "umlp.managermock.IsLoaded", name, concreteSubclassesInclSelf);
    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), domainClass.getName() + "GemList"));

    if(!domainClass.getModifier().isAbstract()) {
      getCD4C().addMethod(managerClass, "umlp.managermock.DoGetList", name, concreteSubclassesInclSelf);

      ASTCDAttribute attr = getCD4C().addAttribute(managerClass, "protected HashMapWithOrderedKeys<Long, " + name + "> " + lName + "s");
      glex.replaceTemplate("cd2java.Value", attr, new StringHookPoint("= new HashMapWithOrderedKeys<>()"));
      attr = getCD4C().addAttribute(managerClass, "protected GemList<" + name + "> " + lName + "GemList");
      glex.replaceTemplate("cd2java.Value", attr, new StringHookPoint("= new " + name + "GemList().build().get()"));
      attr = getCD4C().addAttribute(managerClass, "protected " + name + "CommandSendingObserver the" + name + "CommandObserver");
      glex.replaceTemplate("cd2java.Value", attr, new StringHookPoint("= new " + name + "CommandSendingObserver()"));

      getCD4C().addMethod(managerClass, "umlp.managermock.DoBuilderParam", name);
      getCD4C().addMethod(managerClass, "umlp.managermock.DoDeSerBuilder", name);
      getCD4C().addMethod(managerClass, "umlp.managermock.DoAdd", name, managerName,
          assocs.stream().filter(a -> GenService.isBidirectional(a)).collect(Collectors.toList()));
      getCD4C().addMethod(managerClass, "umlp.managermock.DoDelete", name, managerName);
      getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, CLIENT_PACKAGE), domainClass.getName() + CommandSendingObserverDecorator.SUFFIX));
    }else{
      getCD4C().addMethod(managerClass, "umlp.managermock.DoGetListAbstract", name);
    }

    // Associations
    assocs.stream().filter(a -> GenService.isList(a)).forEach(a -> {
      SymTypeExpression type = a.getSymbol().getType();
      String printedType = type.print();
      ASTCDCompilationUnit cd = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());

      getCD4C().addImport(managerClass, getPackageName(cd, DEFAULT_PACKAGE) + "." + printedType);

      getCD4C().addMethod(managerClass, "umlp.managermock.DoCreateAssoc", getAssocName(domainClass, a), printedType);
      getCD4C().addMethod(managerClass, "umlp.managermock.DoBuilderAssoc", getAssocName(domainClass,a), printedType);
      getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), getName(domainClass, a)));
    });
  }

  protected void addInterfaceElements(ASTCDCompilationUnit origCD, ASTCDInterface domainClass,
                                      ASTCDClass managerClass) {
    String name = domainClass.getName();
    String lName = StringTransformations.uncapitalize(name);

    ASTCDAttribute attr = getCD4C().addAttribute(managerClass, "protected HashMapWithOrderedKeys<Long, " + name + "> " + lName + "s");
    glex.replaceTemplate("cd2java.Value", attr, new StringHookPoint("= new HashMapWithOrderedKeys<>()"));
    attr = getCD4C().addAttribute(managerClass, "protected GemList<" + name + "> " + lName + "GemList");
    glex.replaceTemplate("cd2java.Value", attr, new StringHookPoint("= new " + name + "GemList().build().get()"));

    List<ASTCDAssocSide> assocs = CDSymbolTables.getAssociations(domainClass);
    Set<ASTCDType> concreteSubclassesInclSelf = UMLPSymbolTables.collectConcreteSubclassesInclSelf(origCD, domainClass);
    getCD4C().addMethod(managerClass, "umlp.managermock.DoOpt", name, concreteSubclassesInclSelf);
    getCD4C().addMethod(managerClass, "umlp.managermock.DoGetSure", name);
    getCD4C().addMethod(managerClass, "umlp.managermock.DoGetList", name, concreteSubclassesInclSelf);
    getCD4C().addMethod(managerClass, "umlp.managermock.IsLoaded", name, concreteSubclassesInclSelf);

    // Imports
    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), domainClass.getName() + "GemList"));

    // Associations
    assocs.stream().filter(GenService::isList).forEach(a -> {
      SymTypeExpression type = a.getSymbol().getType();
      String printedType = type.print();
      ASTCDCompilationUnit cd = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());

      getCD4C().addImport(managerClass, getPackageName(cd, DEFAULT_PACKAGE) + "." + printedType);

      getCD4C().addMethod(managerClass, "umlp.managermock.DoCreateAssoc", getAssocName(domainClass, a), printedType);
      getCD4C().addMethod(managerClass, "umlp.managermock.DoBuilderAssoc", getAssocName(domainClass, a), printedType);
      getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), getName(domainClass, a)));
    });
  }

  protected void addImports(ASTCDClass clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("Gem"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("GemList"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("HashMapWithOrderedKeys"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("NotificationScope"));
  }
}
