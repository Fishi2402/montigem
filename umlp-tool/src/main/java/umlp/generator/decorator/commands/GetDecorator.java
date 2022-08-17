/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.decorator.commands;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.decorator.AbstractDecorator;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class GetDecorator extends AbstractDecorator {
  
  public static final String SUFFIX = "_get";
  
  public GetDecorator(GlobalExtensionManagement glex) {
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
      .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Request<"+classname+">"))
      .setName(classname+SUFFIX)
      .build();
    
    getCD4C().addAttribute(ast, "private final long gemId");
    getCD4C().addConstructor(ast, "umlp.commands.error.Constructor", classname,"", SUFFIX, COMMAND_PACKAGE);
    getCD4C().addMethod(ast, "umlp.commands.get.DoRun", classname, COMMAND_PACKAGE, origCD.getCDDefinition().getName());
    getCD4C().addMethod(ast, "umlp.commands.get.GetEffectedGemIds");
    getCD4C().addMethod(ast, "umlp.commands.get.GetNewlySubscribedGemIds");

    getCD4C().addImport(ast, "umlp.request.Request");
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+".*");
    getCD4C().addImport(ast, "umlp.command.SimpleResult");
    getCD4C().addImport(ast, "umlp.rte.LoadingState");

    return ast;
  }
  
}
