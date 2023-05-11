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
    return "umlp.gui2ts.manager.ErrorNotificationScope";
  }

  @Override
  protected void addSingletonElements(String simpleName, ASTCDClass managerClass) {
    String name = managerClass.getName();
    // add attribute
    getCD4C().addAttribute(managerClass, "protected static " + name + " instance");

    // add methods
    getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.InitMe", name);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.GetSingleton", name);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.SetInstance", simpleName);

    getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.GetFreeGemId", true);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.GetFreeGemId", false);
  }

  @Override
  protected void addClassElements(ASTCDClass domainClass, ASTCDClass managerClass) {
    String name = domainClass.getName();

    if (domainClass.getModifier().isDerived()) {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.GetDerived", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.GetDerived", name, false);
    }
    else {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.CreateGem", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.CreateGem", name, false);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.Opt", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.Opt", name, false);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.GetSure", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.GetSure", name, false);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.GetList", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.GetList", name, false);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.IsLoaded", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.IsLoaded", name, false);

      if (!domainClass.getModifier().isAbstract()) {
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.Builder", name, true);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.Builder", name, false);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.BuilderParam", name, true);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.BuilderParam", name, false);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.DeSerBuilder", name, true);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.DeSerBuilder", name, false);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.Delete", name, true);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.Delete", name, false);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.Add", name, true);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.Add", name, false);
      }

      // Associations
      CDSymbolTables.getAssociations(domainClass).stream().filter(a -> GenService.isList(a)).forEach(a -> {
        String type = a.getSymbol().getType().print();
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.CreateAssoc", a.getName(), type, true);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.CreateAssoc", a.getName(), type, false);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.BuilderAssoc", a.getName(), type, true);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.BuilderAssoc", a.getName(), type, false);
      });
    }

  }

  @Override
  protected void addInterfaceElements(ASTCDInterface domainInterface, ASTCDClass managerClass) {
    String name = domainInterface.getName();

    if (domainInterface.getModifier().isDerived()) {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.GetDerived", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.GetDerived", name, false);
    }
    else {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.CreateGem", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.CreateGem", name, false);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.Opt", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.Opt", name, false);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.GetSure", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.GetSure", name, false);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.GetList", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.GetList", name, false);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.IsLoaded", name, true);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.IsLoaded", name, false);

      // Associations
      CDSymbolTables.getAssociations(domainInterface).stream().filter(GenService::isList).forEach(a -> {
        String type = a.getSymbol().getType().print();
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.CreateAssoc", a.getName(), type, true);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.CreateAssoc", a.getName(), type, false);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.BuilderAssoc", a.getName(), type, true);
        getCD4C().addMethod(managerClass, "umlp.gui2ts.manager.BuilderAssoc", a.getName(), type, false);
      });
    }
  }

}
