/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.commands;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.AbstractDecorator;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class CreateDecorator extends AbstractDecorator {

  public static final String SUFFIX = "_create";

  public CreateDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }
  
  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, COMMAND_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getSymbol().isIsDerived())
      .forEach(c -> astcdPackage.addCDElement(createClass(origCD, c)));
  }
  
  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, ASTCDClass domainClass) {
    String classname = domainClass.getName();
    
    ASTCDClass ast = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Request<Void>"))
      .setName(classname+SUFFIX)
      .build();
    
    String firstLetter = classname.substring(0,1).toUpperCase();
    String attributeName = classname.substring(1);
    getCD4C().addAttribute(ast, "protected final "+firstLetter+attributeName+" "
      +firstLetter.toLowerCase()+attributeName);
   
    getCD4C().addConstructor(ast, "umlp.commands.Constructor",classname, COMMAND_PACKAGE, SUFFIX);
    getCD4C().addMethod(ast, "umlp.commands.create.DoRun", classname, origCD.getCDDefinition().getName());
    getCD4C().addMethod(ast, "umlp.commands.GetEffectedGemIds", classname);

    getCD4C().addImport(ast, "umlp.request.Request");
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+".*");
    getCD4C().addImport(ast, "umlp.command.StatusResult");

    return ast;
  }
}
