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

public class ErrorObserverDecorator extends AbstractDecorator {
  
  public static final String SUFFIX = "ErrorObserver";
  
  public ErrorObserverDecorator(final GlobalExtensionManagement glex) {
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
    
    // Add Notify for attributes
    CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
      .filter(c -> !c.getModifier().isDerived())
      .forEach(c ->
        getCD4C().addMethod(ast, "umlp.errorobserver.NotifyInvalid", c.getName(), className));
    
    // Add Notify for associations
    CDSymbolTables.getAssociationsInHierarchy(domainClass).stream()
      .filter(c -> !c.getModifier().isDerived())
      .forEach(c ->
        getCD4C().addMethod(ast, "umlp.errorobserver.NotifyInvalid", c.getName(), className));
    return ast;
  }
}
