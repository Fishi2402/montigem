/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
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

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.GemListAssocDecorator.getName;

public class ManagerImplDecorator extends AbstractDecorator {

  public static final String SUFFIX = ManagerDecorator.SUFFIX + "Impl";

  public ManagerImplDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(createClass(origCD));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass managerClass = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(name + SUFFIX)
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(name + ManagerDecorator.SUFFIX))
            .build();

    // methods
    getCD4C().addMethod(managerClass, "umlp.managerimpl.Constructor", name + SUFFIX);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.Init", name, name + ManagerDecorator.SUFFIX);
    List<String> classList = origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
            .map(c -> c.getName()).collect(Collectors.toList());
    getCD4C().addMethod(managerClass, "umlp.managerimpl.DoGetFreeGemId");
    getCD4C().addMethod(managerClass, "umlp.managerimpl.ErrorNotificationScope");

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

    if(!domainClass.getModifier().isAbstract()) {
      managerClass.addCDMember(getDaoAttribute(domainClass));
      managerClass.addCDMember(getCacheAttribute(domainClass));
      managerClass.addCDMember(getDBObserverAttribute(domainClass));
      getCD4C().addMethod(managerClass, "umlp.managerimpl.DoBuilderParam", name);
      getCD4C().addMethod(managerClass, "umlp.managerimpl.DoDeSerBuilder", name);
      getCD4C().addMethod(managerClass, "umlp.managerimpl.DoAdd", name, managerName);
      getCD4C().addMethod(managerClass, "umlp.managerimpl.DoDelete", name, managerName);
    }

    Set<ASTCDType> collectConcreteSubclassesInclSelf = UMLPSymbolTables.collectConcreteSubclassesInclSelf(origCD, domainClass);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.DoOpt", name, collectConcreteSubclassesInclSelf);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.DoGetSure", name);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.DoGetList", name, false);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.IsLoaded", name, collectConcreteSubclassesInclSelf);

    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), domainClass.getName() + "GemList"));

    // Associations
    CDSymbolTables.getAssociations(domainClass).stream().filter(a -> GenService.isList(a)).forEach(a -> {
      SymTypeExpression type = a.getSymbol().getType();
      String printedType = type.print();
      ASTCDCompilationUnit cd = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());

      getCD4C().addImport(managerClass, getPackageName(cd, DEFAULT_PACKAGE) + "." + printedType);

      getCD4C().addMethod(managerClass, "umlp.managerimpl.DoCreateAssoc", a.getName(), printedType);
      getCD4C().addMethod(managerClass, "umlp.managerimpl.DoBuilderAssoc", domainClass.getName(), a.getName(), printedType);
      getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), getName(domainClass, a)));
    });
  }

  protected void addInterfaceElements(ASTCDCompilationUnit origCD, ASTCDInterface domainInterface,
                                      ASTCDClass managerClass) {
    String name = domainInterface.getName();

    Set<ASTCDType> concreteSubclassesInclSelf = UMLPSymbolTables.collectConcreteSubclassesInclSelf(origCD, domainInterface);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.DoOpt", name, concreteSubclassesInclSelf);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.DoGetSure", name);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.DoGetList", name, true);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.IsLoaded", name, concreteSubclassesInclSelf);

    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), name + "GemList"));

    // Associations
    CDSymbolTables.getAssociations(domainInterface).stream().filter(GenService::isList).forEach(a -> {
      SymTypeExpression type = a.getSymbol().getType();
      String printedType = type.print();
      ASTCDCompilationUnit cd = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());

      getCD4C().addImport(managerClass, getPackageName(cd, DEFAULT_PACKAGE) + "." + printedType);

      getCD4C().addMethod(managerClass, "umlp.managerimpl.DoCreateAssoc", a.getName(), printedType);
      getCD4C().addMethod(managerClass, "umlp.managerimpl.DoBuilderAssoc", name, a.getName(), printedType);
      getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), getName(domainInterface, a)));
    });
  }

  protected ASTCDAttribute getDaoAttribute(ASTCDType domainClass) {
    String type = domainClass.getName() + "DAO";
    ASTCDAttribute domainClassDaoAttr = getCDAttributeFacade().createAttribute(PROTECTED.build(),
            getMCTypeFacade().createQualifiedType(type),
            StringTransformations.uncapitalize(type));
    getGlex().replaceTemplate("cd2java.Value", domainClassDaoAttr, new StringHookPoint(" = new " + type + "()"));
    return domainClassDaoAttr;
  }

  protected ASTCDAttribute getCacheAttribute(ASTCDType domainClass) {
    ASTCDAttribute domainClassDaoAttr = getCDAttributeFacade().createAttribute(PROTECTED.build(),
            getMCTypeFacade().createBasicGenericTypeOf("Map", "Long", domainClass.getName()),
            StringTransformations.uncapitalize("known" + domainClass.getName() + "s"));
    getGlex().replaceTemplate("cd2java.Value", domainClassDaoAttr, new StringHookPoint(" = new HashMap<>()"));
    return domainClassDaoAttr;
  }

  protected ASTCDAttribute getDBObserverAttribute(ASTCDClass domainClass) {
    String type = domainClass.getName() + "DbObserver";
    ASTCDAttribute domainClassDaoAttr = getCDAttributeFacade().createAttribute(PROTECTED.build(),
            getMCTypeFacade().createQualifiedType(type), "the" + type);
    getGlex().replaceTemplate("cd2java.Value", domainClassDaoAttr, new StringHookPoint(" = new " + type + "()"));
    return domainClassDaoAttr;
  }

  protected void addImports(ASTCDClass clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("DatabaseAccessManager"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("Gem"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("GemList"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("GemListBuilder"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(clazz, "java.util.HashMap");
    getCD4C().addImport(clazz, "java.util.Map");
  }

}
