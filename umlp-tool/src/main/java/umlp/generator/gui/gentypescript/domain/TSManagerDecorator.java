/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.BaseClassObjectMapperDecorator;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.domain.decorator.CommandObjectMapperDecorator;
import umlp.generator.domain.decorator.DeSerBuilderDecorator;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.domain.decorator.ObserverDecorator;

public class TSManagerDecorator extends ManagerDecorator {

  protected String astcdPackage;

  protected ASTCDCompilationUnit origCD;

  public TSManagerDecorator(GlobalExtensionManagement glex) {
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
          + c.getName() + BuilderDecorator.BUILDER_SUFFIX);
    }
    for (ASTCDInterface c : this.origCD.getCDDefinition().getCDInterfacesList()) {
      getCD4C().addImport(clazz, this.astcdPackage + "."
          + c.getName() + BuilderDecorator.BUILDER_SUFFIX);
    }

    return clazz;
  }

  @Override
  protected String defaultListValue() {
    return "= []";
  }

  @Override
  protected String getErrorNotificationScopeTemplate() {
    return "umlp.gui2ts.manager.TSErrorNotificationScope";
  }

  @Override
  protected String getObserversType(String name) {
    return name + "Observer" + "[]";
  }

  @Override
  protected void addSingletonElements(String simpleName, ASTCDClass managerClass) {
    String name = managerClass.getName();
    // add attribute
    getCD4C().addAttribute(managerClass, "protected static " + name + " instance");

    // add methods
    getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSInitMe", name);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSGetSingleton", name);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSSetInstance", simpleName);

    getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSGetFreeGemId", true);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSGetFreeGemId", false);
  }

  @Override
  protected void addClassElements(ASTCDClass domainClass, ASTCDClass managerClass) {
    String name = domainClass.getName();

    if (domainClass.getModifier().isDerived()) {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSGetDerived", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSGetDerived", name, false);
    }
    else {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSCreateGem", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSCreateGem", name, false);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSOpt", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSOpt", name, false);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSGetSure", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSGetSure", name, false);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSGetList", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSGetList", name, false);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSIsLoaded", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSIsLoaded", name, false);

      if (!domainClass.getModifier().isAbstract()) {
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSBuilder", name, true, managerClass.getName());
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSBuilder", name, false, managerClass.getName());
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSBuilderParam", name, true, managerClass.getName());
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSBuilderParam", name, false, managerClass.getName());
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSDeSerBuilder", name, true);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSDeSerBuilder", name, false);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSDelete", name, true);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSDelete", name, false);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSAdd", name, true);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSAdd", name, false);
      }

      // Associations
      CDSymbolTables.getAssociations(domainClass).stream().filter(a -> GenService.isList(a)).forEach(a -> {
        String type = a.getSymbol().getType().print();
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSCreateAssoc", a.getName(), type, true);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSCreateAssoc", a.getName(), type, false);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSBuilderAssoc", a.getName(), type, true);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSBuilderAssoc", a.getName(), type, false);
      });
    }

  }

  @Override
  protected void addInterfaceElements(ASTCDInterface domainInterface, ASTCDClass managerClass) {
    String name = domainInterface.getName();

    if (domainInterface.getModifier().isDerived()) {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSGetDerived", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSGetDerived", name, false);
    }
    else {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSCreateGem", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSCreateGem", name, false);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSOpt", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSOpt", name, false);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSGetSure", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSGetSure", name, false);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSGetList", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSGetList", name, false);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSIsLoaded", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSIsLoaded", name, false);

      // Associations
      CDSymbolTables.getAssociations(domainInterface).stream().filter(GenService::isList).forEach(a -> {
        String type = a.getSymbol().getType().print();
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSCreateAssoc", a.getName(), type, true);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSCreateAssoc", a.getName(), type, false);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSBuilderAssoc", a.getName(), type, true);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSBuilderAssoc", a.getName(), type, false);
      });
    }
  }

}
