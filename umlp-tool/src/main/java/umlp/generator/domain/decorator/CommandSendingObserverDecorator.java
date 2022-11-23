/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class CommandSendingObserverDecorator extends AbstractDecorator {

  public static final String SUFFIX = "CommandSendingObserver";

  public CommandSendingObserverDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }
  
  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, CLIENT_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
      .forEach(c -> astcdPackage.addCDElement(createClass(origCD, c)));
  }
  
  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, ASTCDClass domainClass) {
    String classname = domainClass.getName() + SUFFIX;
    
    ASTCDClass ast = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage(domainClass.getName()+"Observer"))
      .setName(classname)
      .build();
    
    getCD4C().addMethod(ast, "umlp.commandobserver.GetScope");

    // TODO: Muss man hier Listen und einfache Attribute unterscheiden? Siehe Assoziationen
    CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
      .filter(a -> !a.getModifier().isDerived())
      .forEach(a -> getCD4C().addMethod(ast, "umlp.commandsendingobserver.NotifySetAttribute", domainClass.getName(),
        a.getName(), GenService.getType(a.getSymbol().getType())));

    // Assoziationen
    List<ASTCDAssocSide> assocs = CDSymbolTables.getAssociationsInHierarchy(domainClass);
    for (ASTCDAssocSide assoc: assocs) {
      if (assoc.getModifier().isReadonly() || assoc.getModifier().isDerived()) {
        continue;
      }
      if (GenService.isList(assoc)) {
        getCD4C().addMethod(ast, "umlp.commandsendingobserver.NotifyAddAttribute", domainClass.getName(),
                assoc.getName());
        getCD4C().addMethod(ast, "umlp.commandsendingobserver.NotifyRemoveAttribute", domainClass.getName(),
                assoc.getName());
      } else {
        getCD4C().addMethod(ast, "umlp.commandsendingobserver.NotifySetAttribute", domainClass.getName(),
                assoc.getName(), "long");
      }
    }
    addImports(origCD, ast, domainClass);
    
    return ast;
  }
  
  protected void addImports(ASTCDCompilationUnit origCD, ASTCDClass clazz, ASTCDClass domainclass) {
    getCD4C().addImport(clazz,"umlp.rte.NotificationScope");
    getCD4C().addImport(clazz, getPackageName(origCD, DEFAULT_PACKAGE)+".*");
    getCD4C().addImport(clazz, "umlp.command.CommandManager");
    getCD4C().addImport(clazz, getPackageName(origCD, COMMAND_PACKAGE)+".*");
  }
  
}
