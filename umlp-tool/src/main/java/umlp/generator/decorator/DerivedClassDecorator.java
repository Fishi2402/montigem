/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import umlp.UMLPMill;

import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;

//import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class DerivedClassDecorator extends AbstractDecorator {
  
  public DerivedClassDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }
  
  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> c.getSymbol().isIsDerived())
      .forEach(c -> astcdPackage.addCDElement(createClass(c)));
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
        a.getMCType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()))));
    
    CDSymbolTables.getAssociationsInHierarchy(domainClass).stream()
      .filter(a -> !a.getModifier().isDerived())
      .forEach(a -> getCD4C().addMethod(ast, "umlp.derivedclass.Get", a.getName(),
        a.getMCQualifiedType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()))));
    
    return ast;
  }
  
}
