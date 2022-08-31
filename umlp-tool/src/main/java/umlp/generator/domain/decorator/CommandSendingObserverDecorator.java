/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;

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
    
    domainClass.getCDAttributeList().stream()
      .filter(a -> !a.getModifier().isDerived())
      .forEach(a -> getCD4C().addMethod(ast, "umlp.commandsendingobserver.NotifySetAttribute", domainClass.getName(),
        a.getName(), a.getSymbol().getType().print()));
    
    addImports(origCD, ast, domainClass);
    
    return ast;
  }
  
  protected void addImports(ASTCDCompilationUnit origCD, ASTCDClass clazz, ASTCDClass domainclass) {
    getCD4C().addImport(clazz,"umlp.rte.NotificationScope");
    getCD4C().addImport(clazz, getPackageName(origCD, DEFAULT_PACKAGE)+".*");
    if(!domainclass.getCDAttributeList().isEmpty()) {
      getCD4C().addImport(clazz, "umlp.command.CommandManager");
    }
    domainclass.getCDAttributeList().stream()
      .filter(a -> !a.getModifier().isDerived())
      .forEach(a -> getCD4C().addImport(clazz, getPackageName(origCD, COMMAND_PACKAGE)+"."+domainclass.getName()+"_set_"+a.getName()));
  }
  
}
