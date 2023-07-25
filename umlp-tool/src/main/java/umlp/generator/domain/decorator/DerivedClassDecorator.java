/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;

public class DerivedClassDecorator extends AbstractDecorator {

  public DerivedClassDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> c.getSymbol().isIsDerived())
      .forEach(c -> astcdPackage.addCDElement(withOrigin(createClass(c))));
  }

  protected ASTCDClass createClass(ASTCDClass domainClass) {
    String classname = domainClass.getName();

    ASTCDClass ast = UMLPMill.cDClassBuilder().setModifier(PUBLIC_ABSTRACT.build())
      .setName(classname).build();

    getCD4C().addConstructor(ast, "umlp.derivedclass.Constructor", classname);

    getCD4C().addMethod(ast, "umlp.derivedclass.Init");

    CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
      .filter(a -> !a.getModifier().isDerived())
      .forEach(a -> getCD4C().addMethod(ast, "umlp.derivedclass.Get", a.getName(),
        a.getSymbol().getType().print()));

    CDSymbolTables.getAssociationsInHierarchy(domainClass).stream()
      .filter(a -> !a.getSymbol().getAssoc().getAssociation().getAstNode().getModifier().isDerived())
      .forEach(a -> {
        String type = a.getSymbol().getType().print();
        if (GenService.isList(a)) {
          getCD4C().addMethod(ast, "umlp.derivedclass.Size", a.getName());
          type = "List<" + type + ">";
        }
        getCD4C().addMethod(ast, "umlp.derivedclass.Get", a.getName(), type);
      });

    addImports(domainClass, ast);

    return ast;
  }

  @Override
  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
  }
}
