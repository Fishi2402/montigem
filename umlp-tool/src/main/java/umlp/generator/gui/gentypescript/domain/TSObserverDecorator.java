/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.util.PathUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.GenService.createType;

public class TSObserverDecorator extends ObserverDecorator {

  protected String astcdPackage;

  public TSObserverDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    this.astcdPackage = astcdPackage.getName();
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
        .forEach(c -> astcdPackage.addCDElement(withOrigin(createClass(c))));

    origCD.getCDDefinition().getCDInterfacesList().stream().filter(c -> !c.getModifier().isDerived())
        .forEach(c -> astcdPackage.addCDElement(withOrigin(createClass(c))));
  }

  protected ASTCDClass createClass(ASTCDType domainClass) {
    String className = domainClass.getName();
    ASTCDClass ast = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setName(className + SUFFIX).build();
    List<SymTypeExpression> superTypes = domainClass.getSymbol().getSuperTypesList();
    if (!superTypes.isEmpty()) {
      ast.setCDExtendUsage(getExtendUsageFacade()
              .createCDExtendUsage(superTypes.stream().map(a -> a.print()+SUFFIX).toArray(String[]::new)));
    }

    Set<SymTypeExpression> additionalImports = new HashSet<>();
    // Add Notify for attributes
    domainClass.getCDAttributeList().stream()
      .filter(a -> !a.getModifier().isDerived())
      .forEach(a -> {
        if(GenService.isList(a)) {
          String innerType = GenService.getInnerType(a.getMCType());
          getCD4C().addMethod(ast, "umlp.gui2ts.observer.TSNotifyAdd", a.getName(), className, innerType);
          getCD4C().addMethod(ast, "umlp.gui2ts.observer.TSMaybeNotifyAdd", a.getName(), className, innerType);
          getCD4C().addMethod(ast, "umlp.gui2ts.observer.TSNotifyRemove", a.getName(), className, innerType);
          getCD4C().addMethod(ast, "umlp.gui2ts.observer.TSMaybeNotifyRemove", a.getName(), className, innerType);
        }

        if (GenService.isCDType(a.getSymbol().getType())) {
          additionalImports.add(a.getSymbol().getType());
        } else if (GenService.isInnerCDEnum(a.getSymbol().getType())) {
          additionalImports.add(((SymTypeOfGenerics) a.getSymbol().getType()).getArgument(0));
        }

        String fullType = getCDPrinter().prettyprint(createType(a));
        getCD4C().addMethod(ast, "umlp.gui2ts.observer.TSNotify", a.getName(), className, fullType, false);
        getCD4C().addMethod(ast, "umlp.gui2ts.observer.TSMaybeNotify", a.getName(), className, fullType, false);
      });

    // Add Notify for list associations
    CDSymbolTables.getAssociations(domainClass).stream()
            .filter(a -> !a.getSymbol().getAssoc().getAssociation().getAstNode().getModifier().isDerived())
            .filter(a -> GenService.isList(a))
            .forEach(a -> {
              getCD4C().addMethod(ast, "umlp.gui2ts.observer.TSNotifyAdd", a.getName(), className, "long");
              getCD4C().addMethod(ast, "umlp.gui2ts.observer.TSMaybeNotifyAdd", a.getName(), className, "long");
              getCD4C().addMethod(ast, "umlp.gui2ts.observer.TSNotifyRemove", a.getName(), className, "long");
              getCD4C().addMethod(ast, "umlp.gui2ts.observer.TSMaybeNotifyRemove", a.getName(), className, "long");
              getCD4C().addMethod(ast, "umlp.gui2ts.observer.TSNotify", a.getName(), className, "long", true);
              getCD4C().addMethod(ast, "umlp.gui2ts.observer.TSMaybeNotify", a.getName(), className, "long", true);
              if (GenService.isInnerCDEnum(a.getSymbol().getType())) {
                additionalImports.add(((SymTypeOfGenerics) a.getSymbol().getType()).getArgument(0));
              }
            });

    // Add Notify for associations
    CDSymbolTables.getAssociations(domainClass).stream()
            .filter(a -> !a.getSymbol().getAssoc().getAssociation().getAstNode().getModifier().isDerived())
            .filter(a -> !GenService.isList(a))
            .forEach(a -> {
              getCD4C().addMethod(ast, "umlp.gui2ts.observer.TSNotify", a.getName(), className, "long", false);
              getCD4C().addMethod(ast, "umlp.gui2ts.observer.TSMaybeNotify", a.getName(), className, "long", false);
              if (GenService.isCDEnum(a.getSymbol().getType())) {
                additionalImports.add(a.getSymbol().getType());
              }
            });

    getCD4C().addMethod(ast, "umlp.gui2ts.observer.TSGetScope");
    getCD4C().addMethod(ast, "umlp.gui2ts.observer.TSNotifyBuild", className);
    getCD4C().addMethod(ast, "umlp.gui2ts.observer.TSMaybeNotifyBuild", className);

    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(ast, this.astcdPackage + "." + domainClass.getName());
    if (!domainClass.getSuperclassList().isEmpty()) {
      getCD4C().addImport(ast, this.astcdPackage + "." + domainClass.getSuperclassList().get(0).printType() + ObserverDecorator.SUFFIX);
    }
    additionalImports.forEach(e -> getCD4C().addImport(ast, e.getTypeInfo().getFullName()));

    return ast;
  }
}
