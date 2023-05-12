/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.ManagerDecorator;

public class TSManagerDecorator extends ManagerDecorator {

  public TSManagerDecorator(GlobalExtensionManagement glex) {
    super(glex);
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
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSBuilder", name, true);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSBuilder", name, false);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSBuilderParam", name, true);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.TSBuilderParam", name, false);
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
