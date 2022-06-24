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

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class DbObserverDecorator extends AbstractDecorator {
  
  public static final String SUFFIX = "DbObserver";
  
  public DbObserverDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }
  
  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getSymbol().isIsDerived())
      .forEach(c -> astcdPackage.addCDElement(createClass(c)));
  }
  
  protected ASTCDClass createClass(ASTCDClass domainClass) {
    String className = domainClass.getName();
    
    ASTCDClass ast = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(className + "Observer"))
      .setName(className + SUFFIX).build();
  
    CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
      .filter(a -> !a.getModifier().isDerived())
      .forEach(a -> getCD4C().addMethod(ast, "umlp.dbobserver.Notify", a.getName(), className,a.getMCType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()))));
  
    return ast;
  }
}
