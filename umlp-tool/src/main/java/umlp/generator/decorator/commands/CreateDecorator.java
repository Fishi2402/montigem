/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.decorator.commands;

import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.decorator.AbstractDecorator;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class CreateDecorator extends AbstractDecorator {

  public static final String SUFFIX = "_create";

  public static final String COMMAND_Package = DEFAULT_PACKAGE + ".commands";

  public CreateDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }
  
  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, COMMAND_Package);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getSymbol().isIsDerived())
      .forEach(c -> astcdPackage.addCDElement(createClass(c)));
  }
  
  protected ASTCDClass createClass(ASTCDClass domainClass) {
    String classname = domainClass.getName();
    
    ASTCDClass ast = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setCDExtendUsage(CDExtendUsageFacade.getInstance().createCDExtendUsage("Request<Void>"))
      .setName(classname+SUFFIX)
      .build();
    
    String firstLetter = classname.substring(0,1).toUpperCase();
    String attributeName = classname.substring(1);
    getCD4C().addAttribute(ast, "protected final "+firstLetter+attributeName+" "
      +firstLetter.toLowerCase()+attributeName);
   
    getCD4C().addConstructor(ast, "umlp.commands.create.Constructor",classname, DEFAULT_PACKAGE);
    
    getCD4C().addMethod(ast, "umlp.commands.create.DoRun", classname);
    getCD4C().addMethod(ast, "umlp.commands.create.GetEffectedGemIds", classname);
    
    return ast;
  }
}
