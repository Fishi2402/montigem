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
import umlp.generator.domain.decorator.commands.GetFreeGemIdRequestDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.TagLookUp;
import umlp.generator.util.UMLPSymbolTables;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.GemListAssocDecorator.getName;

public class ManagerClientImplDecorator extends AbstractDecorator {

  public static final String SUFFIX = ManagerDecorator.SUFFIX + "ClientImpl";

  public ManagerClientImplDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
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

    // methods
    getCD4C().addMethod(managerClass, getInitTemplate(), name, name + ManagerDecorator.SUFFIX);
    getCD4C().addMethod(managerClass, getConstructorTemplate(), name + SUFFIX);
    getCD4C().addMethod(managerClass, getDoGetFreeGemIdTemplate());
    getCD4C().addMethod(managerClass, getErrorNotificationScopeTemplate());

    List<ASTCDClass> concreteDomainClasses = origCD.getCDDefinition().getCDClassesList().stream()
        .filter(c -> !c.getModifier().isAbstract() && !c.getModifier().isDerived())
        .filter(c -> !TagLookUp.isSingleton(c))
        .collect(Collectors.toList());
    getCD4C().addMethod(managerClass, getClearCacheTemplate(), concreteDomainClasses);


    origCD.getCDDefinition().getCDClassesList()
        .forEach(c -> addClassElements(origCD, c, managerClass, name));

    origCD.getCDDefinition().getCDInterfacesList().stream().filter(i -> !i.getModifier().isDerived())
        .forEach(i -> addInterfaceElements(origCD, i, managerClass));

    // Imports
    addImports(origCD, managerClass);
    return managerClass;
  }

  protected String getClearCacheTemplate() {
    return "umlp.managerclientimpl.ClearCache";
  }

  protected String getInitTemplate() {
    return "umlp.managerclientimpl.Init";
  }

  protected String getErrorNotificationScopeTemplate() {
    return "umlp.managerclientimpl.ErrorNotificationScope";
  }

  protected String getDoGetFreeGemIdTemplate() {
    return "umlp.managerclientimpl.DoGetFreeGemId";
  }

  protected String getConstructorTemplate() {
    return "umlp.managerclientimpl.Constructor";
  }


  protected void addClassElements(ASTCDCompilationUnit origCD, ASTCDClass domainClass,
                                  ASTCDClass managerClass, String managerName) {
    String name = domainClass.getName();

    if (domainClass.getModifier().isDerived()) {
      getCD4C().addMethod(managerClass, "umlp.managerclientimpl.GetDerived", name);
      return;
    }

    boolean isSingleton = TagLookUp.isSingleton(domainClass);

    if (!domainClass.getModifier().isAbstract()) {
      if (isSingleton) {
        managerClass.addCDMember(getSingletonAttribute(domainClass));
      }else {
        managerClass.addCDMember(getCacheAttribute(domainClass));
      }
      managerClass.addCDMember(getCommandObserverAttribute(domainClass));
    }

    List<ASTCDType> concreteSubclassesOrderedByDistance = concreteSubclassesOrderedByDistance(origCD, domainClass);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.DoGetSure", name);
    getCD4C().addMethod(managerClass, "umlp.managerclientimpl.DoGetList", name, false);

    if (isSingleton) {
      getCD4C().addMethod(managerClass, "umlp.managerclientimpl.DoOptSingletonElement", name);
      getCD4C().addMethod(managerClass, "umlp.managerclientimpl.DoGetSingletonElement", name);
    } else {
      getCD4C().addMethod(managerClass, "umlp.managerclientimpl.DoOpt", name, concreteSubclassesOrderedByDistance);
      getCD4C().addMethod(managerClass, "umlp.managerimpl.IsLoaded", name, concreteSubclassesOrderedByDistance);
    }

    if (!domainClass.getModifier().isAbstract()) {
      getCD4C().addMethod(managerClass, "umlp.managerimpl.DoBuilderParam", name);
      getCD4C().addMethod(managerClass, "umlp.managerimpl.DoDeSerBuilder", name);
      if (!isSingleton) {
        getCD4C().addMethod(managerClass, "umlp.managerclientimpl.DoAdd", name, managerName);
        getCD4C().addMethod(managerClass, "umlp.managerclientimpl.DoDelete", name, managerName);
      }
    }

    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), domainClass.getName() + "GemList"));
    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, COMMAND_PACKAGE), domainClass.getName() + "_get"));
    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, COMMAND_PACKAGE), domainClass.getName() + "_get_filtered"));

    if (!domainClass.getModifier().isAbstract()) {
      getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, CLIENT_PACKAGE), domainClass.getName() + "CommandObserver"));
      getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, COMMAND_PACKAGE), domainClass.getName() + "_create"));
      getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, COMMAND_PACKAGE), domainClass.getName() + "_delete"));
    }

    // Associations
    CDSymbolTables.getAssociations(domainClass).stream()
        .filter(GenService::isList)
        .filter(Predicate.not(GenService::isSymbolDerived))
        .forEach(a -> {
          SymTypeExpression type = a.getSymbol().getType();
          String printedType = type.print();
          ASTCDCompilationUnit cd = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());

          getCD4C().addImport(managerClass, getPackageName(cd, DEFAULT_PACKAGE) + "." + printedType);

          getCD4C().addMethod(managerClass, "umlp.managerimpl.DoCreateAssoc", getAssocName(domainClass, a), printedType);
          getCD4C().addMethod(managerClass, "umlp.managerimpl.DoBuilderAssoc", getAssocName(domainClass, a), printedType);
          getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), getName(domainClass, a)));
        });
  }

  protected static List<ASTCDType> concreteSubclassesOrderedByDistance(ASTCDCompilationUnit origCD, ASTCDType domainType) {
    return UMLPSymbolTables.collectConcreteSubclassesInclSelfWithDist(origCD, domainType)
        .entrySet().stream()
        .sorted((o1, o2) -> o2.getValue() - o1.getValue())
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  protected void addInterfaceElements(ASTCDCompilationUnit origCD, ASTCDInterface domainInterface,
                                      ASTCDClass managerClass) {
    String name = domainInterface.getName();

    List<ASTCDType> concreteSubclassesOrderedByDistance = concreteSubclassesOrderedByDistance(origCD, domainInterface);
    getCD4C().addMethod(managerClass, "umlp.managerclientimpl.DoOpt", name, concreteSubclassesOrderedByDistance);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.DoGetSure", name);
    getCD4C().addMethod(managerClass, "umlp.managerclientimpl.DoGetList", name, true);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.IsLoaded", name, concreteSubclassesOrderedByDistance);
    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, COMMAND_PACKAGE), domainInterface.getName() + "_get"));
    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, COMMAND_PACKAGE), domainInterface.getName() + "_get_filtered"));

    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), name + "GemList"));

    // Associations
    CDSymbolTables.getAssociations(domainInterface).stream()
        .filter(Predicate.not(GenService::isSymbolDerived))
        .filter(GenService::isList)
        .forEach(a -> {
          SymTypeExpression type = a.getSymbol().getType();
          String printedType = type.print();
          ASTCDCompilationUnit cd = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());

          getCD4C().addImport(managerClass, getPackageName(cd, DEFAULT_PACKAGE) + "." + printedType);

          getCD4C().addMethod(managerClass, "umlp.managerimpl.DoCreateAssoc", getAssocName(domainInterface, a), printedType);
          getCD4C().addMethod(managerClass, "umlp.managerimpl.DoBuilderAssoc", getAssocName(domainInterface, a), printedType);
          getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), getName(domainInterface, a)));
        });
  }

  protected ASTCDAttribute getCacheAttribute(ASTCDType domainClass) {
    ASTCDAttribute domainClassDaoAttr = getCDAttributeFacade().createAttribute(PROTECTED.build(),
        getMCTypeFacade().createBasicGenericTypeOf("Map", "Long", domainClass.getName()),
        StringTransformations.uncapitalize("known" + domainClass.getName() + "s"));
    getGlex().replaceTemplate("cd2java.Value", domainClassDaoAttr, new StringHookPoint(defaultMapValue()));
    return domainClassDaoAttr;
  }

  protected String defaultMapValue() {
    return " = new HashMap<>()";
  }

  protected ASTCDAttribute getCommandObserverAttribute(ASTCDClass domainClass) {
    String type = domainClass.getName() + "CommandObserver";
    ASTCDAttribute domainClassDaoAttr = getCDAttributeFacade().createAttribute(PROTECTED.build(),
        getMCTypeFacade().createQualifiedType(type), "the" + type);
    getGlex().replaceTemplate("cd2java.Value", domainClassDaoAttr, new StringHookPoint(" = new " + type + "()"));
    return domainClassDaoAttr;
  }

  protected ASTCDAttribute getSingletonAttribute(ASTCDType domainClass) {
    ASTCDAttribute singletonAttr = getCDAttributeFacade().createAttribute(PROTECTED.build(),
        getMCTypeFacade().createOptionalTypeOf(domainClass.getName()),
        StringTransformations.uncapitalize(domainClass.getName() + "Instance"));
    getGlex().replaceTemplate("cd2java.Value", singletonAttr, new StringHookPoint(defaultEmptyOptional()));
    return singletonAttr;
  }

  protected String defaultEmptyOptional() {
    return " = Optional.empty()";
  }

  protected void addImports(ASTCDCompilationUnit origCD, ASTCDClass clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandManager"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("SimpleResult"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("ListResult"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandResult"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("StatusResult"));
    getCD4C().addImport(clazz, Joiners.DOT.join(getPackageName(origCD, COMMAND_PACKAGE), GetFreeGemIdRequestDecorator.CLASS_NAME));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("Gem"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("GemList"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("GemListBuilder"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTEUtilPath("LogUtil"));
    getCD4C().addImport(clazz, "java.util.HashMap");
    getCD4C().addImport(clazz, "java.util.Map");
  }

}
