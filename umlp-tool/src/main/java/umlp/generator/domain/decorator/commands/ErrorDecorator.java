/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.commands;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class ErrorDecorator extends AbstractDecorator {
  
  public static final String NAME = "_error_";
  
  public ErrorDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }
  
  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, COMMAND_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getSymbol().isIsDerived())
      .forEach(c ->
        c.getCDAttributeList()
          .stream()
          .filter(a -> !a.getModifier().isDerived())
          .forEach(a -> astcdPackage.addCDElement(createClass(origCD, c, a))));
  }
  
  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, ASTCDClass domainClass, ASTCDAttribute attribute) {
    String classname = domainClass.getName();
    String attributename = attribute.getName();
    
    ASTCDClass ast = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Command<Void>"))
      .setName(classname+NAME+attributename)
      .build();
    
    getCD4C().addAttribute(ast, "private final long gemId");
    getCD4C().addConstructor(ast, "umlp.commands.error.Constructor",classname, attributename, NAME, "");
    getCD4C().addMethod(ast, "umlp.commands.error.DoRun", classname, attributename, origCD.getCDDefinition().getName());
    getCD4C().addMethod(ast, "umlp.commands.error.GetEffectedGemIds");
    getCD4C().addImport(ast,"umlp.command.Command");
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+".*");
    getCD4C().addImport(ast, "umlp.command.StatusResult");

    return ast;
  }
}
