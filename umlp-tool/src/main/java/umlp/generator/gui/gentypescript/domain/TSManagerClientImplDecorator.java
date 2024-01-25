/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.domain.decorator.BaseClassObjectMapperDecorator;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.domain.decorator.CommandObjectMapperDecorator;
import umlp.generator.domain.decorator.DeSerBuilderDecorator;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.ManagerClientImplDecorator;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.domain.decorator.requests.GetFreeGemIdRequestDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.TagLookUp;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static umlp.generator.domain.decorator.GemListAssocDecorator.getName;

public class TSManagerClientImplDecorator extends ManagerClientImplDecorator {

  protected String astcdPackage;

  protected ASTCDCompilationUnit origCD;

  public TSManagerClientImplDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE).getName();
    this.origCD = origCD;
    super.transform(origCD, decoratedCD);
  }

  @Override
  protected ASTCDClass createClass(ASTCDCompilationUnit origCD) {
    ASTCDClass clazz = super.createClass(origCD);

    String name = origCD.getCDDefinition().getName();
    getCD4C().addImport(clazz, this.astcdPackage + "."
        + name + ManagerDecorator.SUFFIX);
    getCD4C().addImport(clazz, this.astcdPackage + "."
        + name + BaseClassObjectMapperDecorator.SUFFIX);
    getCD4C().addImport(clazz, this.astcdPackage + "."
        + name + CommandObjectMapperDecorator.SUFFIX);
    getCD4C().addImport(clazz, this.astcdPackage + "."
        + name + ObserverDecorator.SUFFIX);

    ArrayList<ASTCDCompilationUnit> cdDefs = new ArrayList<>();
    cdDefs.add(origCD);
    cdDefs.addAll(getExtendedDefinitions(origCD));

    for (ASTCDCompilationUnit cdDef : cdDefs) {
      for (ASTCDClass c : cdDef.getCDDefinition().getCDClassesList()) {
        getCD4C().addImport(clazz, getPackageName(cdDef, DEFAULT_PACKAGE) + "." + c.getName());
        if (!c.getModifier().isDerived() && !c.getModifier().isAbstract()) {
          getCD4C().addImport(clazz, getPackageName(cdDef, DEFAULT_PACKAGE) + "."
              + c.getName() + BuilderDecorator.BUILDER_SUFFIX);
          getCD4C().addImport(clazz, getPackageName(cdDef, DEFAULT_PACKAGE) + "."
              + c.getName() + DeSerBuilderDecorator.DESER_BUILDER_SUFFIX);
        }
      }
      for (ASTCDEnum c : cdDef.getCDDefinition().getCDEnumsList()) {
        getCD4C().addImport(clazz, getPackageName(cdDef, DEFAULT_PACKAGE) + "."
            + c.getName());
      }
      for (ASTCDInterface c : cdDef.getCDDefinition().getCDInterfacesList()) {
        getCD4C().addImport(clazz, getPackageName(cdDef, DEFAULT_PACKAGE) + "."
            + c.getName());
      }
    }

    return clazz;
  }

  @Override
  protected String getClearCacheTemplate() {
    return "umlp.gui2ts.managerclientimpl.TSClearCache";
  }

  @Override
  protected String getInitTemplate() {
    return "umlp.gui2ts.managerclientimpl.TSInit";
  }

  @Override
  protected String getErrorNotificationScopeTemplate() {
    return "umlp.gui2ts.managerclientimpl.TSErrorNotificationScope";
  }

  @Override
  protected String getDoGetFreeGemIdTemplate() {
    return "umlp.gui2ts.managerclientimpl.TSDoGetFreeGemId";
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.managerclientimpl.TSConstructor";
  }


  @Override
  protected void addClassElements(ASTCDCompilationUnit origCD, ASTCDClass domainClass,
                                  ASTCDClass managerClass, String managerName) {
    String name = domainClass.getName();
    if (domainClass.getModifier().isDerived()) {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerclientimpl.TSGetDerived", name);
      getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), name));
      getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), name + "ClientImpl"));
      return;
    }
    boolean isSingleton = TagLookUp.isSingleton(domainClass);

    if (!domainClass.getModifier().isAbstract()) {
      if (isSingleton) {
        managerClass.addCDMember(getSingletonAttribute(domainClass));
      } else {
        managerClass.addCDMember(getCacheAttribute(domainClass));
      }
      managerClass.addCDMember(getCommandObserverAttribute(domainClass));
    }

    List<ASTCDType> concreteSubclassesOrderedByDistance = concreteSubclassesOrderedByDistance(origCD, domainClass);

    getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.TSDoGetSure", name);


    if (!domainClass.getModifier().isAbstract()) {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.TSDoBuilderParam", name);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.TSDoDeSerBuilder", name);
      if (!isSingleton) {
        getCD4C().addMethod(managerClass, "umlp.gui2ts.managerclientimpl.TSDoAdd", name, managerName);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.managerclientimpl.TSDoDelete", name, managerName);
      }
    }

    if (isSingleton) {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerclientimpl.TSDoOptSingletonElement", name);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerclientimpl.TSDoGetSingletonElement", name);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerclientimpl.TSDoGetListSingletonElement", name);
    } else {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerclientimpl.TSDoOpt", name, concreteSubclassesOrderedByDistance);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.TSIsLoaded", name, concreteSubclassesOrderedByDistance);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerclientimpl.TSDoGetList", name, false);
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
          String type = a.getSymbol().getType().print();
          getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.TSDoCreateAssoc", getAssocName(domainClass, a), type, managerClass.getName());
          getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.TSDoBuilderAssoc", domainClass.getName(), getAssocName(domainClass, a), type);
          getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), getName(domainClass, a)));
        });
  }

  @Override
  protected void addInterfaceElements(ASTCDCompilationUnit origCD, ASTCDInterface domainInterface,
                                      ASTCDClass managerClass) {
    String name = domainInterface.getName();

    List<ASTCDType> concreteSubclassesOrderedByDistance = concreteSubclassesOrderedByDistance(origCD, domainInterface);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managerclientimpl.TSDoOpt", name, concreteSubclassesOrderedByDistance);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.TSDoGetSure", name);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managerclientimpl.TSDoGetList", name, true);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.TSIsLoaded", name, concreteSubclassesOrderedByDistance);
    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, COMMAND_PACKAGE), domainInterface.getName() + "_get"));
    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, COMMAND_PACKAGE), domainInterface.getName() + "_get_filtered"));

    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), name + "GemList"));

    // Associations
    CDSymbolTables.getAssociations(domainInterface).stream()
        .filter(GenService::isList)
        .filter(Predicate.not(GenService::isSymbolDerived))
        .forEach(a -> {
          String type = a.getSymbol().getType().print();
          getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.TSDoCreateAssoc", getAssocName(domainInterface, a), type, managerClass.getName());
          getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.TSDoBuilderAssoc", name, getAssocName(domainInterface, a), type);
          getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), getName(domainInterface, a)));
        });
  }

  @Override
  protected ASTCDAttribute getCacheAttribute(ASTCDType domainClass) {
    ASTCDAttribute domainClassDaoAttr = getCDAttributeFacade().createAttribute(PROTECTED.build(),
        getMCTypeFacade().createBasicGenericTypeOf("Map", "Long", "Promise<" + domainClass.getName() + ">"),
        StringTransformations.uncapitalize("known" + domainClass.getName() + "s"));
    getGlex().replaceTemplate("cd2java.Value", domainClassDaoAttr, new StringHookPoint(defaultMapValue()));
    return domainClassDaoAttr;
  }

  @Override
  protected String defaultMapValue() {
    return " = new Map()";
  }

  @Override
  protected ASTCDAttribute getSingletonAttribute(ASTCDType domainClass) {
    ASTCDAttribute singletonInstanceAttr = getCDAttributeFacade().createAttribute(PROTECTED.build(),
        getMCTypeFacade().createOptionalTypeOf( "Promise<" + domainClass.getName() + ">"),
        StringTransformations.uncapitalize(domainClass.getName() + "Instance"));
    // Add the "?" notation for optional types
    getGlex().replaceTemplate("umlp.gui2ts.TSAttribute", singletonInstanceAttr,
            new StringHookPoint(" protected " + StringTransformations.uncapitalize(domainClass.getName()) + "Instance?: Promise<"+domainClass.getName()+">;"));
    return singletonInstanceAttr;
  }


  @Override
  protected void addImports(ASTCDCompilationUnit origCD, ASTCDClass clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandManager"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("LoadResult"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("LoadingState"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("SimpleResult"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("ListResult"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("StatusResult"));
    getCD4C().addImport(clazz, Joiners.DOT.join(getPackageName(origCD, REQUESTS_PACKAGE), GetFreeGemIdRequestDecorator.CLASS_NAME));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("Gem"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("GemList"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("GemListBuilder"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTEUtilPath("LogUtil"));
  }

}
