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
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;

public class ManagerDecorator extends AbstractDecorator {

  public static final String SUFFIX = "Manager";

  public ManagerDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(createClass(origCD));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass managerClass = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC_ABSTRACT.build())
            .setName(name + SUFFIX).build();

    // Attribute observerList
    ASTCDAttribute attrObserver = getCDAttributeFacade().createAttribute(PROTECTED.build(),
            "List<" + name + "Observer>", StringTransformations.uncapitalize(name) + "Observers");
    glex.replaceTemplate("cd2java.Value", attrObserver, new StringHookPoint(defaultListValue()));
    managerClass.addCDMember(attrObserver);

    addSingletonElements(name, managerClass);
    getCD4C().addMethod(managerClass, getErrorNotificationScopeTemplate());

    origCD.getCDDefinition().getCDClassesList()
            .forEach(c -> addClassElements(c, managerClass));

    origCD.getCDDefinition().getCDInterfacesList()
            .forEach(i -> addInterfaceElements(i, managerClass));

    addImports(managerClass);

    return managerClass;
  }

  protected String defaultListValue() {
    return "= new ArrayList<>()";
  }

  protected String getErrorNotificationScopeTemplate() {
    return "umlp.manager.ErrorNotificationScope";
  }

  protected void addSingletonElements(String simpleName, ASTCDClass managerClass) {
    String name = managerClass.getName();
    // add attribute
    getCD4C().addAttribute(managerClass, "protected static " + name + " instance");

    // add methods
    getCD4C().addMethod(managerClass, "umlp.manager.InitMe", name);
    getCD4C().addMethod(managerClass, "umlp.manager.GetSingleton", name);
    getCD4C().addMethod(managerClass, "umlp.manager.SetInstance", simpleName);

    getCD4C().addMethod(managerClass, "umlp.manager.GetFreeGemId", true);
    getCD4C().addMethod(managerClass, "umlp.manager.GetFreeGemId", false);
  }

  protected void addClassElements(ASTCDClass domainClass, ASTCDClass managerClass) {
    String name = domainClass.getName();

    if(domainClass.getModifier().isDerived()) {
      getCD4C().addMethod(managerClass, "umlp.manager.GetDerived", name, true);
      getCD4C().addMethod(managerClass, "umlp.manager.GetDerived", name, false);
    } else {
      getCD4C().addMethod(managerClass, "umlp.manager.CreateGem", name, true);
      getCD4C().addMethod(managerClass, "umlp.manager.CreateGem", name, false);
      getCD4C().addMethod(managerClass, "umlp.manager.Opt", name, true);
      getCD4C().addMethod(managerClass, "umlp.manager.Opt", name, false);
      getCD4C().addMethod(managerClass, "umlp.manager.GetSure", name, true);
      getCD4C().addMethod(managerClass, "umlp.manager.GetSure", name, false);
      getCD4C().addMethod(managerClass, "umlp.manager.GetList", name, true);
      getCD4C().addMethod(managerClass, "umlp.manager.GetList", name, false);
      getCD4C().addMethod(managerClass, "umlp.manager.IsLoaded", name, true);
      getCD4C().addMethod(managerClass, "umlp.manager.IsLoaded", name, false);

      if(!domainClass.getModifier().isAbstract()) {
        getCD4C().addMethod(managerClass, "umlp.manager.Builder", name, true);
        getCD4C().addMethod(managerClass, "umlp.manager.Builder", name, false);
        getCD4C().addMethod(managerClass, "umlp.manager.BuilderParam", name, true);
        getCD4C().addMethod(managerClass, "umlp.manager.BuilderParam", name, false);
        getCD4C().addMethod(managerClass, "umlp.manager.DeSerBuilder", name, true);
        getCD4C().addMethod(managerClass, "umlp.manager.DeSerBuilder", name, false);
        getCD4C().addMethod(managerClass, "umlp.manager.Delete", name, true);
        getCD4C().addMethod(managerClass, "umlp.manager.Delete", name, false);
        getCD4C().addMethod(managerClass, "umlp.manager.Add", name, true);
        getCD4C().addMethod(managerClass, "umlp.manager.Add", name, false);
      }

      // Associations
      CDSymbolTables.getAssociations(domainClass).stream().filter(a -> GenService.isList(a)).forEach(a -> {
        String type = a.getSymbol().getType().print();
        getCD4C().addMethod(managerClass, "umlp.manager.CreateAssoc", a.getName(), type, true);
        getCD4C().addMethod(managerClass, "umlp.manager.CreateAssoc", a.getName(), type, false);
        getCD4C().addMethod(managerClass, "umlp.manager.BuilderAssoc", a.getName(), type, true);
        getCD4C().addMethod(managerClass, "umlp.manager.BuilderAssoc", a.getName(), type, false);
      });
    }

  }

  protected void addInterfaceElements(ASTCDInterface domainInterface, ASTCDClass managerClass) {
    String name = domainInterface.getName();

    if(domainInterface.getModifier().isDerived()) {
      getCD4C().addMethod(managerClass, "umlp.manager.GetDerived", name, true);
      getCD4C().addMethod(managerClass, "umlp.manager.GetDerived", name, false);
    } else {
      getCD4C().addMethod(managerClass, "umlp.manager.CreateGem", name, true);
      getCD4C().addMethod(managerClass, "umlp.manager.CreateGem", name, false);
      getCD4C().addMethod(managerClass, "umlp.manager.Opt", name, true);
      getCD4C().addMethod(managerClass, "umlp.manager.Opt", name, false);
      getCD4C().addMethod(managerClass, "umlp.manager.GetSure", name, true);
      getCD4C().addMethod(managerClass, "umlp.manager.GetSure", name, false);
      getCD4C().addMethod(managerClass, "umlp.manager.GetList", name, true);
      getCD4C().addMethod(managerClass, "umlp.manager.GetList", name, false);
      getCD4C().addMethod(managerClass, "umlp.manager.IsLoaded", name, true);
      getCD4C().addMethod(managerClass, "umlp.manager.IsLoaded", name, false);

      // Associations
      CDSymbolTables.getAssociations(domainInterface).stream().filter(GenService::isList).forEach(a -> {
        String type = a.getSymbol().getType().print();
        getCD4C().addMethod(managerClass, "umlp.manager.CreateAssoc", a.getName(), type, true);
        getCD4C().addMethod(managerClass, "umlp.manager.CreateAssoc", a.getName(), type, false);
        getCD4C().addMethod(managerClass, "umlp.manager.BuilderAssoc", a.getName(), type, true);
        getCD4C().addMethod(managerClass, "umlp.manager.BuilderAssoc", a.getName(), type, false);
      });
    }
  }

    protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("Gem"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("LoadResult"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("LoadingState"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("GemList"));
  }


}
