/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.decorator.GenService.createType;

public class ObserverDecorator extends AbstractDecorator {
  
  public static final String SUFFIX = "Observer";
  
  public ObserverDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }
  
  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
            .forEach(c -> astcdPackage.addCDElement(createInterface(c)));
  }
  
  protected ASTCDInterface createInterface(ASTCDClass domainClass) {
    String className = domainClass.getName();
    ASTCDInterface ast = UMLPMill.cDInterfaceBuilder()
      .setModifier(PUBLIC.build())
      .setName(className + SUFFIX).build();
    if (domainClass.isPresentCDExtendUsage()) {
      ast.setCDExtendUsage(getExtendUsageFacade()
              .createCDExtendUsage(domainClass.getCDExtendUsage().getSuperclass(0).deepClone()));
    }

    // Add Notify for attributes
    domainClass.getCDAttributeList().stream()
      .filter(a -> !a.getModifier().isDerived())
      .forEach(a -> {
        getCD4C().addMethod(ast, "umlp.observer.Notify", a.getName(), className, getCDPrinter().prettyprint(createType(a)));
        getCD4C().addMethod(ast, "umlp.observer.MaybeNotify", a.getName(), className, getCDPrinter().prettyprint(createType(a)));
      });

    // Add Notify for list associations
    CDSymbolTables.getAssociations(domainClass).stream()
            .filter(a -> !a.getModifier().isDerived())
            .filter(a -> GenService.isList(a))
            .forEach(a -> {
              getCD4C().addMethod(ast, "umlp.observer.NotifyAdd", a.getName(), className);
              getCD4C().addMethod(ast, "umlp.observer.MaybeNotifyAdd", a.getName(), className);
              getCD4C().addMethod(ast, "umlp.observer.NotifyRemove", a.getName(), className);
              getCD4C().addMethod(ast, "umlp.observer.MaybeNotifyRemove", a.getName(), className);
            });

    // Add Notify for associations
    CDSymbolTables.getAssociations(domainClass).stream()
            .filter(a -> !a.getModifier().isDerived())
            .filter(a -> !GenService.isList(a))
            .forEach(a -> {
              getCD4C().addMethod(ast, "umlp.observer.Notify", a.getName(), className, "long");
              getCD4C().addMethod(ast, "umlp.observer.MaybeNotify", a.getName(), className, "long");
            });

    getCD4C().addMethod(ast, "umlp.observer.GetScope");
    getCD4C().addMethod(ast, "umlp.observer.NotifyBuild", className);
    getCD4C().addMethod(ast, "umlp.observer.MaybeNotifyBuild", className);

    getCD4C().addImport(ast, "umlp.rte.NotificationScope");

    return ast;
  }
}
