/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.decorator.commands;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.decorator.AbstractDecorator;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class ErrorDecorator extends AbstractDecorator {
  
  public static final String NAME = "_error_";
  
  public ErrorDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }
  
  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, COMMAND_Package);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getSymbol().isIsDerived())
      .forEach(c ->
        c.getCDAttributeList()
          .stream()
          .filter(a -> !a.getModifier().isDerived())
          .forEach(a -> astcdPackage.addCDElement(createClass(c, a, origCD.getCDDefinition().getName()))));
  }
  
  protected ASTCDClass createClass(ASTCDClass domainClass, ASTCDAttribute attribute, String definitionName) {
    String classname = domainClass.getName();
    String attributename = attribute.getName();
    
    ASTCDClass ast = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Command<Void>"))
      .setName(classname+NAME+attributename)
      .build();
    
    getCD4C().addAttribute(ast, "private final long gemId");
    getCD4C().addConstructor(ast, "umlp.commands.error.Constructor",classname, attributename, NAME, "");
    getCD4C().addMethod(ast, "umlp.commands.error.DoRun", classname, attributename, definitionName);
    getCD4C().addMethod(ast, "umlp.commands.error.GetEffectedGemIds");
    getCD4C().addImport(ast, "umlp."+definitionName+"Manager");
    getCD4C().addImport(ast,"umlp.command.Command");
    getCD4C().addImport(ast, "umlp.command.StatusResult");
    getCD4C().addImport(ast, "umlp."+classname);
    getCD4C().addImport(ast, "umlp."+classname+"ErrorCollector");
  
    return ast;
  }
}
