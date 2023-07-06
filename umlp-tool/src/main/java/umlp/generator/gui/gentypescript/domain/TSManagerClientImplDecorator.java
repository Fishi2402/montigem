/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
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
import umlp.generator.util.UMLPSymbolTables;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
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

    for (ASTCDClass c : this.origCD.getCDDefinition().getCDClassesList()) {
      getCD4C().addImport(clazz, this.astcdPackage + "." + c.getName());
      if (!c.getModifier().isDerived() && !c.getModifier().isAbstract()) {
        getCD4C().addImport(clazz, this.astcdPackage + "."
            + c.getName() + BuilderDecorator.BUILDER_SUFFIX);
        getCD4C().addImport(clazz, this.astcdPackage + "."
            + c.getName() + DeSerBuilderDecorator.DESER_BUILDER_SUFFIX);
      }
    }
    for (ASTCDEnum c : this.origCD.getCDDefinition().getCDEnumsList()) {
      getCD4C().addImport(clazz, this.astcdPackage + "."
          + c.getName());
    }
    for (ASTCDInterface c : this.origCD.getCDDefinition().getCDInterfacesList()) {
      getCD4C().addImport(clazz, this.astcdPackage + "."
          + c.getName());
    }

    return clazz;
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

    if(!domainClass.getModifier().isAbstract()) {
      managerClass.addCDMember(getCacheAttribute(domainClass));
      managerClass.addCDMember(getCommandObserverAttribute(domainClass));
    }

    List<ASTCDType> concreteSubclassesOrderedByDistance = concreteSubclassesOrderedByDistance(origCD, domainClass);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managerclientimpl.TSDoOpt", name, concreteSubclassesOrderedByDistance);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.TSDoGetSure", name);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managerclientimpl.TSDoGetList", name, false);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.TSIsLoaded", name, concreteSubclassesOrderedByDistance);

    if(!domainClass.getModifier().isAbstract()) {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.TSDoBuilderParam", name);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.TSDoDeSerBuilder", name);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerclientimpl.TSDoAdd", name, managerName);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerclientimpl.TSDoDelete", name, managerName);
    }

    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), domainClass.getName() + "GemList"));
    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, COMMAND_PACKAGE), domainClass.getName() + "_get"));
    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, COMMAND_PACKAGE), domainClass.getName() + "_get_filtered"));

    if(!domainClass.getModifier().isAbstract()) {
      getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, CLIENT_PACKAGE), domainClass.getName() + "CommandObserver"));
      getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, COMMAND_PACKAGE), domainClass.getName() + "_create"));
      getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, COMMAND_PACKAGE), domainClass.getName() + "_delete"));
    }

    // Associations
    CDSymbolTables.getAssociations(domainClass).stream().filter(a -> GenService.isList(a)).forEach(a -> {
      String type = a.getSymbol().getType().print();
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.TSDoCreateAssoc", a.getName(), type, managerClass.getName());
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.TSDoBuilderAssoc", domainClass.getName(), a.getName(), type);
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
    CDSymbolTables.getAssociations(domainInterface).stream().filter(GenService::isList).forEach(a -> {
      String type = a.getSymbol().getType().print();
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.TSDoCreateAssoc", a.getName(), type, managerClass.getName());
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.TSDoBuilderAssoc", name, a.getName(), type);
      getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), getName(domainInterface, a)));
    });
  }

  @Override
  protected String defaultMapValue() {
    return " = new Map()";
  }

  @Override
  protected void addImports(ASTCDCompilationUnit origCD, ASTCDClass clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandManager"));
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
