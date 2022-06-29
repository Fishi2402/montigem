/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import umlp.UMLPMill;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class ObserverDecorator extends AbstractDecorator {
  
  public static final String SUFFIX = "Observer";
  
  public ObserverDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }
  
  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().forEach(c -> astcdPackage.addCDElement(createInterface(c)));
  }
  
  protected ASTCDInterface createInterface(ASTCDClass domainClass) {
    String className = domainClass.getName();
    ASTCDInterface ast = UMLPMill.cDInterfaceBuilder()
      .setModifier(PUBLIC.build())
      .setName(className + SUFFIX).build();

    //Add Notify for attributes
    CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
      .filter(a -> !a.getModifier().isDerived())
      .forEach(a -> {
        getCD4C().addMethod(ast, "umlp.observer.Notify", a.getName(), className, a.getMCType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())));
        getCD4C().addMethod(ast, "umlp.observer.MaybeNotify", a.getName(), className, a.getMCType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())));
      });
  
    getCD4C().addMethod(ast, "umlp.observer.GetScope");
    getCD4C().addMethod(ast, "umlp.observer.NotifyBuild", className);
    getCD4C().addMethod(ast, "umlp.observer.MaybeNotifyBuild", className);

    getCD4C().addImport(ast, "umlp.rte.NotificationScope");

    return ast;
  }
}
