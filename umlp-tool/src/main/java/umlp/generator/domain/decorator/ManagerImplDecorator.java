/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PRIVATE;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

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
            .setName(name+SUFFIX)
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(name + ManagerDecorator.SUFFIX))
            .build();

    // methods
    getCD4C().addMethod(managerClass, "umlp.managerimpl.Constructor", name+SUFFIX);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.Init", name, name+ManagerDecorator.SUFFIX);
    List<String> classList = origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
                    .map(c -> c.getName()).collect(Collectors.toList());
    getCD4C().addMethod(managerClass, "umlp.managerimpl.DoGetFreeGemId");
    getCD4C().addMethod(managerClass, "umlp.managerimpl.ErrorNotificationScope");

    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
            .forEach(c -> addClassElements(c, managerClass, name));

    // Imports
    getCD4C().addImport(managerClass, "umlp.rte.*");
    getCD4C().addImport(managerClass, getPackageName(origCD, GEMLIST_PACKAGE) + ".*");

    return managerClass;
  }

  protected void addClassElements(ASTCDClass domainClass, ASTCDClass managerClass, String managerName) {
    String name = domainClass.getName();

    managerClass.addCDMember(getDaoAttribute(domainClass));
    managerClass.addCDMember(getCacheAttribute(domainClass));
    managerClass.addCDMember(getDBObserverAttribute(domainClass));

    getCD4C().addMethod(managerClass, "umlp.managerimpl.DoBuilderParam", name);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.DoDeSerBuilder", name);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.DoOpt", name);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.DoGetSure", name);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.DoAdd", name, managerName);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.DoGetList", name);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.DoDelete", name, managerName);
    getCD4C().addMethod(managerClass, "umlp.managerimpl.IsLoaded", name);

    // Associations
    CDSymbolTables.getAssociations(domainClass).stream().filter(a -> GenService.isList(a)).forEach(a -> {
      String type = a.getSymbol().getType().print();
      getCD4C().addMethod(managerClass, "umlp.managerimpl.DoCreateAssoc", a.getName(), type);
      getCD4C().addMethod(managerClass, "umlp.managerimpl.DoBuilderAssoc", domainClass.getName(), a.getName(), type);
    });
  }

  private ASTCDAttribute getDaoAttribute(ASTCDClass domainClass) {
    String type = domainClass.getName()+"DAO";
    ASTCDAttribute domainClassDaoAttr = getCDAttributeFacade().createAttribute(PRIVATE.build(),
        getMCTypeFacade().createQualifiedType(type),
        StringTransformations.uncapitalize(type));
    getGlex().replaceTemplate("cd2java.Value", domainClassDaoAttr, new StringHookPoint(" = new "+type+"()"));
    return domainClassDaoAttr;
  }

  private ASTCDAttribute getCacheAttribute(ASTCDClass domainClass) {
    ASTCDAttribute domainClassDaoAttr = getCDAttributeFacade().createAttribute(PRIVATE.build(),
        getMCTypeFacade().createBasicGenericTypeOf("Map", "Long", domainClass.getName()),
        StringTransformations.uncapitalize("known"+domainClass.getName()+"s"));
    getGlex().replaceTemplate("cd2java.Value", domainClassDaoAttr, new StringHookPoint(" = new HashMap<>()"));
    return domainClassDaoAttr;
  }

  private ASTCDAttribute getDBObserverAttribute(ASTCDClass domainClass) {
    String type = domainClass.getName()+"DbObserver";
    ASTCDAttribute domainClassDaoAttr = getCDAttributeFacade().createAttribute(PRIVATE.build(),
        getMCTypeFacade().createQualifiedType(type), "the"+type);
    getGlex().replaceTemplate("cd2java.Value", domainClassDaoAttr, new StringHookPoint(" = new "+type+"()"));
    return domainClassDaoAttr;
  }

}
