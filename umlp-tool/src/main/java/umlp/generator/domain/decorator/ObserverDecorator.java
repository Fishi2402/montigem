/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.check.SymTypeExpression;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.util.List;
import java.util.function.Predicate;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.GenService.createType;

public class ObserverDecorator extends AbstractDecorator {

  public static final String SUFFIX = "Observer";

  public ObserverDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
        .forEach(c -> astcdPackage.addCDElement(withOrigin(createInterface(c))));

    origCD.getCDDefinition().getCDInterfacesList().stream().filter(c -> !c.getModifier().isDerived())
        .forEach(c -> astcdPackage.addCDElement(withOrigin(createInterface(c))));
  }

  protected ASTCDInterface createInterface(ASTCDType domainClass) {
    String className = domainClass.getName();
    ASTCDInterface ast = UMLPMill.cDInterfaceBuilder()
        .setModifier(PUBLIC.build())
        .setName(className + SUFFIX).build();
    List<SymTypeExpression> superTypes = domainClass.getSymbol().getSuperTypesList();
    if (!superTypes.isEmpty()) {
      ast.setCDExtendUsage(getExtendUsageFacade()
          .createCDExtendUsage(superTypes.stream().map(a -> a.print() + SUFFIX).toArray(String[]::new)));
    }

    // Add Notify for attributes
    domainClass.getCDAttributeList().stream()
        .filter(a -> !a.getModifier().isDerived())
        .forEach(a -> {
          if (GenService.isList(a)) {
            String innerType = GenService.getInnerType(a.getMCType());
            getCD4C().addMethod(ast, "umlp.observer.NotifyAdd", a.getName(), className, innerType);
            getCD4C().addMethod(ast, "umlp.observer.MaybeNotifyAdd", a.getName(), className, innerType);
            getCD4C().addMethod(ast, "umlp.observer.NotifyRemove", a.getName(), className, innerType);
            getCD4C().addMethod(ast, "umlp.observer.MaybeNotifyRemove", a.getName(), className, innerType);
          }

          String fullType = getCDPrinter().prettyprint(createType(a));
          getCD4C().addMethod(ast, "umlp.observer.Notify", a.getName(), className, fullType, false);
          getCD4C().addMethod(ast, "umlp.observer.MaybeNotify", a.getName(), className, fullType, false);
        });

    // Add Notify for list associations
    CDSymbolTables.getAssociations(domainClass).stream()
        .filter(Predicate.not(GenService::isSymbolDerived))
        .filter(GenService::isList)
        .forEach(a -> {
          getCD4C().addMethod(ast, "umlp.observer.NotifyAdd", a.getName(), className, "long");
          getCD4C().addMethod(ast, "umlp.observer.MaybeNotifyAdd", a.getName(), className, "long");
          getCD4C().addMethod(ast, "umlp.observer.NotifyRemove", a.getName(), className, "long");
          getCD4C().addMethod(ast, "umlp.observer.MaybeNotifyRemove", a.getName(), className, "long");
          getCD4C().addMethod(ast, "umlp.observer.Notify", a.getName(), className, "long", true);
          getCD4C().addMethod(ast, "umlp.observer.MaybeNotify", a.getName(), className, "long", true);
        });

    // Add Notify for associations
    CDSymbolTables.getAssociations(domainClass).stream()
        .filter(Predicate.not(GenService::isSymbolDerived))
        .filter(Predicate.not(GenService::isList))
        .forEach(a -> {
          getCD4C().addMethod(ast, "umlp.observer.Notify", a.getName(), className, "long", false);
          getCD4C().addMethod(ast, "umlp.observer.MaybeNotify", a.getName(), className, "long", false);
        });

    getCD4C().addMethod(ast, "umlp.observer.GetScope");
    getCD4C().addMethod(ast, "umlp.observer.NotifyBuild", className);
    getCD4C().addMethod(ast, "umlp.observer.MaybeNotifyBuild", className);

    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("NotificationScope"));
    addImportForImportedObservers(domainClass, ast);
    super.addImports(domainClass, ast);

    return ast;
  }

  private void addImportForImportedObservers(ASTCDType domainClass, ASTCDType observerType) {
    CDTypeSymbol symbol = domainClass.getSymbol();
    if (symbol.isPresentSuperClass()) {
      SymTypeExpression superclass = symbol.getSuperClass();
      ASTCDCompilationUnit scCD = UMLPSymbolTables.getDiagramFor((ASTCDType) superclass.getTypeInfo().getAstNode());
      getCD4C().addImport(observerType, getPackageName(scCD, DEFAULT_PACKAGE) + "." + superclass.print() + "Observer");
    }
  }
}
