/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

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
            .setName(name+SUFFIX).build();

    // Attribute observerList
    ASTCDAttribute attrObserver = getCDAttributeFacade().createAttribute(PROTECTED.build(),
            "List<"+name+"Observer>", StringTransformations.uncapitalize(name) +"Observers");
    glex.replaceTemplate("cd2java.Value", attrObserver, new StringHookPoint("= new ArrayList<>()"));
    managerClass.addCDMember(attrObserver);

    addSingletonElements(managerClass);
    getCD4C().addMethod(managerClass, "umlp.manager.ErrorNotificationScope");

    origCD.getCDDefinition().getCDClassesList()
            .forEach(c -> addClassElements(c, managerClass));

    addImports(managerClass);

    return managerClass;
  }

  protected void addSingletonElements(ASTCDClass managerClass) {
    String name = managerClass.getName();
    // add attribute
    getCD4C().addAttribute(managerClass, "protected static " + name + " instance");

    // add methods
    getCD4C().addMethod(managerClass, "umlp.manager.InitMe", name);
    getCD4C().addMethod(managerClass, "umlp.manager.GetSingleton", name);
    getCD4C().addMethod(managerClass, "umlp.manager.SetInstance", name);

    getCD4C().addMethod(managerClass, "umlp.manager.GetFreeGemId");
    getCD4C().addMethod(managerClass, "umlp.manager.DoGetFreeGemId");
  }

  protected void addClassElements(ASTCDClass domainClass, ASTCDClass managerClass) {
    String name = domainClass.getName();

    if (domainClass.getModifier().isDerived()) {
      getCD4C().addMethod(managerClass, "umlp.manager.DoGetDerived", name);
      getCD4C().addMethod(managerClass, "umlp.manager.GetDerived", name);
    } else {
      getCD4C().addMethod(managerClass, "umlp.manager.CreateGem", name);
      getCD4C().addMethod(managerClass, "umlp.manager.DoCreateGem", name);
      getCD4C().addMethod(managerClass, "umlp.manager.Builder", name);
      getCD4C().addMethod(managerClass, "umlp.manager.DoBuilderParam", name);
      getCD4C().addMethod(managerClass, "umlp.manager.DoBuilder", name);
      getCD4C().addMethod(managerClass, "umlp.manager.BuilderParam", name);
      getCD4C().addMethod(managerClass, "umlp.manager.DeSerBuilder", name);
      getCD4C().addMethod(managerClass, "umlp.manager.DoDeSerBuilder", name);
      getCD4C().addMethod(managerClass, "umlp.manager.Opt", name);
      getCD4C().addMethod(managerClass, "umlp.manager.DoOpt", name);
      getCD4C().addMethod(managerClass, "umlp.manager.GetSure", name);
      getCD4C().addMethod(managerClass, "umlp.manager.DoGetSure", name);
      getCD4C().addMethod(managerClass, "umlp.manager.Add", name);
      getCD4C().addMethod(managerClass, "umlp.manager.DoAdd", name);
      getCD4C().addMethod(managerClass, "umlp.manager.GetList", name);
      getCD4C().addMethod(managerClass, "umlp.manager.DoGetList", name);
      getCD4C().addMethod(managerClass, "umlp.manager.Delete", name);
      getCD4C().addMethod(managerClass, "umlp.manager.DoDelete", name);
      // Associations
      CDSymbolTables.getAssociations(domainClass).stream().filter(a -> GenService.isList(a)).forEach(a -> {
        String type = a.getSymbol().getType().print();
        getCD4C().addMethod(managerClass, "umlp.manager.CreateAssoc", a.getName(), type, true);
        getCD4C().addMethod(managerClass, "umlp.manager.DoCreateAssoc", a.getName(), type, true);
        getCD4C().addMethod(managerClass, "umlp.manager.CreateAssoc", a.getName(), type, false);
        getCD4C().addMethod(managerClass, "umlp.manager.DoCreateAssoc", a.getName(), type, false);
        getCD4C().addMethod(managerClass, "umlp.manager.BuilderAssoc", a.getName(), type);
        getCD4C().addMethod(managerClass, "umlp.manager.DoBuilderAssoc", a.getName(), type);
      });
    }

  }

  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, "umlp.rte.*");
  }


}
