/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.decorator.commands;

import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.decorator.AbstractDecorator;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class DeleteDecorator extends AbstractDecorator {
  
  public static final String SUFFIX = "_delete";
  
  public DeleteDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }
  
  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, COMMAND_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getSymbol().isIsDerived())
      .forEach(c -> astcdPackage.addCDElement(createClass(origCD, c, astcdPackage.getName())));
  }
  
  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, ASTCDClass domainClass, String packageName) {
    String classname = domainClass.getName();
    
    ASTCDClass ast = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Command<Void>"))
      .setName(classname+SUFFIX)
      .build();
    
    String aName = classname.substring(1);
    String aFirst = classname.substring(0,1);
    ASTCDAttribute attr = getCD4C().addAttribute(ast, "protected final " + aFirst.toUpperCase() + aName + " " + aFirst.toLowerCase() + aName);
    
    getCD4C().addConstructor(ast, "umlp.commands.Constructor", classname, packageName, SUFFIX);
    getCD4C().addMethod(ast, "umlp.commands.delete.DoRun", classname, origCD.getCDDefinition().getName());
    getCD4C().addMethod(ast, "umlp.commands.GetEffectedGemIds");
    AccessorDecorator accessor = new AccessorDecorator(glex);
    ast.addAllCDMembers(accessor.decorate(attr));

    getCD4C().addImport(ast, "umlp.command.Command");
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+".*");
    getCD4C().addImport(ast, "umlp.command.StatusResult");

    return ast;
  }
  
}
