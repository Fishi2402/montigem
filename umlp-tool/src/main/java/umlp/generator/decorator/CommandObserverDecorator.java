/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class CommandObserverDecorator extends AbstractDecorator {
  
  public static final String SUFFIX = "CommandObserver";
  
  public CommandObserverDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }
  
  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, CLIENT_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
      .forEach(c -> astcdPackage.addCDElement(createClass(c)));
  }
  
  protected ASTCDClass createClass(ASTCDClass domainClass) {
    String classname = domainClass.getName() + SUFFIX;
    
    ASTCDClass ast = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage(domainClass.getName()+"Observer"))
      .setName(classname)
      .build();
    
    getCD4C().addMethod(ast, "umlp.commandobserver.GetScope");
    
    domainClass.getCDAttributeList().stream()
      .filter(a -> !a.getModifier().isDerived())
      .forEach(a -> getCD4C().addMethod(ast, "umlp.commandobserver.NotifySetAttribute", domainClass.getName(),
        a.getName(), a.getSymbol().getType().print()));
    
    addImports(ast, domainClass);
    
    return ast;
  }
  
  protected void addImports(ASTCDClass clazz, ASTCDClass domainclass) {
    getCD4C().addImport(clazz,"umlp.rte.NotificationScope");
    getCD4C().addImport(clazz,"umlp."+domainclass.getName()+"Observer");
    if(!domainclass.getCDAttributeList().isEmpty()) {
      getCD4C().addImport(clazz, "umlp.command.CommandManager");
      getCD4C().addImport(clazz, "umlp."+domainclass.getName());
    }
    domainclass.getCDAttributeList().stream()
      .filter(a -> !a.getModifier().isDerived())
      .forEach(a -> getCD4C().addImport(clazz, "umlp.commands."+domainclass.getName()+"_set_"+a.getName()));
  }
  
}
