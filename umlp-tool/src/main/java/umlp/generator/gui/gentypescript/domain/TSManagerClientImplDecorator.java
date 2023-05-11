/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.ManagerClientImplDecorator;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.GemListAssocDecorator.getName;

public class TSManagerClientImplDecorator extends ManagerClientImplDecorator {

  public TSManagerClientImplDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected String getErrorNotificationScopeTemplate() {
    return "umlp.gui2ts.managerclientimpl.ErrorNotificationScope";
  }

  @Override
  protected String getDoGetFreeGemIdTemplate() {
    return "umlp.gui2ts.managerclientimpl.DoGetFreeGemId";
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.managerclientimpl.Constructor";
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
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managerclientimpl.DoOpt", name, concreteSubclassesOrderedByDistance);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.DoGetSure", name);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.DoGetList", name, false);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.IsLoaded", name, concreteSubclassesOrderedByDistance);

    if(!domainClass.getModifier().isAbstract()) {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.DoBuilderParam", name);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.DoDeSerBuilder", name);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerclientimpl.DoAdd", name, managerName);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerclientimpl.DoDelete", name, managerName);
    }

    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), domainClass.getName() + "GemList"));
    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, COMMAND_PACKAGE), domainClass.getName() + "_get"));

    if(!domainClass.getModifier().isAbstract()) {
      getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, CLIENT_PACKAGE), domainClass.getName() + "CommandObserver"));
      getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, COMMAND_PACKAGE), domainClass.getName() + "_create"));
      getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, COMMAND_PACKAGE), domainClass.getName() + "_delete"));
    }

    // Associations
    CDSymbolTables.getAssociations(domainClass).stream().filter(a -> GenService.isList(a)).forEach(a -> {
      String type = a.getSymbol().getType().print();
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.DoCreateAssoc", a.getName(), type);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.DoBuilderAssoc", domainClass.getName(), a.getName(), type);
      getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), getName(domainClass, a)));
    });
  }

  @Override
  protected void addInterfaceElements(ASTCDCompilationUnit origCD, ASTCDInterface domainInterface,
                                      ASTCDClass managerClass) {
    String name = domainInterface.getName();

    List<ASTCDType> concreteSubclassesOrderedByDistance = concreteSubclassesOrderedByDistance(origCD, domainInterface);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managerclientimpl.DoOpt", name, concreteSubclassesOrderedByDistance);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.DoGetSure", name);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.DoGetList", name, true);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.IsLoaded", name, concreteSubclassesOrderedByDistance);
    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, COMMAND_PACKAGE), domainInterface.getName() + "_get"));

    getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), name + "GemList"));

    // Associations
    CDSymbolTables.getAssociations(domainInterface).stream().filter(GenService::isList).forEach(a -> {
      String type = a.getSymbol().getType().print();
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.DoCreateAssoc", a.getName(), type);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managerimpl.DoBuilderAssoc", name, a.getName(), type);
      getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), getName(domainInterface, a)));
    });
  }

  @Override
  protected String defaultMapValue() {
    return " = new Map()";
  }

}
