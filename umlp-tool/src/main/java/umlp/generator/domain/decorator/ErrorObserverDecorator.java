/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

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
            .forEach(c -> astcdPackage.addCDElement(withOrigin(createInterface(c))));
  }

  public static String getClassName(String domainClassName) {
    return domainClassName + SUFFIX;
  }

  protected ASTCDInterface createInterface(ASTCDClass domainClass) {
    String className = domainClass.getName();

    ASTCDInterface ast = UMLPMill.cDInterfaceBuilder()
      .setModifier(PUBLIC.build())
      .setName(getClassName(className)).build();

    // Add Notify for attribute
    CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
      .filter(c -> !c.getModifier().isDerived())
      .forEach(c ->
        getCD4C().addMethod(ast, getNotifyInvalidTemplate(), c.getName(), className));

    // Add Notify for associations
    CDSymbolTables.getAssociationsInHierarchy(domainClass).stream()
      .filter(c -> !c.getModifier().isDerived())
      .forEach(c ->
        getCD4C().addMethod(ast, getNotifyInvalidTemplate(), c.getName(), className));
    addImports(domainClass, ast);
    return ast;
  }

  protected String getNotifyInvalidTemplate() {
    return "umlp.errorobserver.NotifyInvalid";
  }

  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
  }
}
