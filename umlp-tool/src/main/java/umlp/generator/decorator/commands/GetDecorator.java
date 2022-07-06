/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.decorator.commands;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.decorator.AbstractDecorator;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class GetDecorator extends AbstractDecorator {
  
  public static final String SUFFIX = "_get";
  
  public static final String COMMAND_Package = DEFAULT_PACKAGE + ".commands";
  
  public GetDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }
  
  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, COMMAND_Package);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getSymbol().isIsDerived())
      .forEach(c -> astcdPackage.addCDElement(createClass(c, origCD.getCDDefinition().getName())));
  }

  protected ASTCDClass createClass(ASTCDClass domainClass, String definitionName) {
    String classname = domainClass.getName();
    
    ASTCDClass ast = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setCDExtendUsage(CDExtendUsageFacade.getInstance().createCDExtendUsage("Request<"+classname+">"))
      .setName(classname+SUFFIX)
      .build();
    
    getCD4C().addAttribute(ast, "private final long gemId");
    getCD4C().addConstructor(ast, "umlp.commands.error.Constructor", classname,"", SUFFIX,COMMAND_Package);
    getCD4C().addMethod(ast, "umlp.commands.get.DoRun", classname, DEFAULT_PACKAGE, definitionName);
    getCD4C().addMethod(ast, "umlp.commands.get.GetEffectedGemIds");
    getCD4C().addMethod(ast, "umlp.commands.get.GetNewlySubscribedGemIds");
    getCD4C().addImport(ast, "umlp.request.Request");
    getCD4C().addImport(ast, "umlp."+classname);
    getCD4C().addImport(ast, "umlp.command.SimpleResult");
    getCD4C().addImport(ast, "umlp.rte.LoadingState");
  
    return ast;
  }
  
}
