/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.decorator.commands;

import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.expressions.tr.javaclassexpressionstr._cocos.JavaClassExpressionsTRASTGenericInvocationExpression_ListCoCo;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.decorator.AbstractDecorator;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class DeleteDecorator extends AbstractDecorator {
  
  private static final String SUFFIX = "_delete";
  
  public DeleteDecorator(GlobalExtensionManagement glex) {
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
      .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Command<Void>"))
      .setName(classname+SUFFIX)
      .build();
    
    String aName = classname.substring(1);
    String aFirst = classname.substring(0,1);
    getCD4C().addAttribute(ast, "protected final "+ aFirst.toUpperCase() + aName +" "+ aFirst.toLowerCase() + aName);
    
    getCD4C().addConstructor(ast, "umlp.commands.Constructor", classname, DEFAULT_PACKAGE, SUFFIX);
    getCD4C().addMethod(ast, "umlp.commands.delete.DoRun", classname, definitionName);
    getCD4C().addMethod(ast, "umlp.commands.GetEffectedGemIds", classname);

    getCD4C().addImport(ast, "umlp.command.Command");
    getCD4C().addImport(ast, "umlp.*");
    getCD4C().addImport(ast, "umlp.command.StatusResult");
    getCD4C().addImport(ast, "umlp."+definitionName+"Manager");
    
    return ast;
  }
  
}
