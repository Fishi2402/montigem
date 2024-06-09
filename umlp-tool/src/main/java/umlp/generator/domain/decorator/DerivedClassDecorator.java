/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
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
    getDerivedDomainClasses(origCD).forEach(c -> astcdPackage.addCDElement(withOrigin(createClass(c))));
  }

  protected ASTCDClass createClass(ASTCDClass domainClass) {
    String classname = domainClass.getName();

    ASTCDClass ast = UMLPMill.cDClassBuilder().setModifier(PUBLIC_ABSTRACT.build())
      .setName(classname).build();

    getCD4C().addConstructor(ast, getConstructorTemplate(), classname);

    getCD4C().addMethod(ast, getInitTemplate(), "NYI", "NYI", "NYI", "NYI");

    CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
      .filter(a -> !a.getModifier().isDerived())
      .forEach(a -> {
        ASTCDMethodSignature calc = getCD4C().addMethod(ast, getCalcTemplate(),
            a.getName(), a.getSymbol().getType().print());
        getCD4C().addMethod(ast, getGetTemplate(),
            a.getName(), a.getSymbol().getType().print(), calc);
      });

    CDSymbolTables.getAssociationsInHierarchy(domainClass).stream()
      .forEach(a -> {
        String type = a.getSymbol().getType().print();
        if (GenService.isList(a)) {
          ASTCDMethodSignature calcSize = getCD4C().addMethod(ast, getCalcSizeTemplate(), a.getName());
          getCD4C().addMethod(ast, getSizeTemplate(), a.getName(), calcSize);
          type = "List<" + type + ">";
        }
        ASTCDMethodSignature calc = getCD4C().addMethod(ast, getCalcTemplate(), a.getName(), type);
        getCD4C().addMethod(ast, getGetTemplate(), a.getName(), type, calc);
      });

    addImports(domainClass, ast);

    return ast;
  }

  protected String getConstructorTemplate() {
    return "umlp.derivedclass.Constructor";
  }

  protected String getInitTemplate() {
    return "umlp.derivedclass.Init";
  }

  protected String getGetTemplate() {
    return "umlp.derivedclass.Get";
  }

  protected String getSizeTemplate() {
    return "umlp.derivedclass.Size";
  }

  protected String getCalcTemplate() {
    return "umlp.derivedclass.Calc";
  }

  protected String getCalcSizeTemplate() {
    return "umlp.derivedclass.CalcSize";
  }

  @Override
  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
  }
}
