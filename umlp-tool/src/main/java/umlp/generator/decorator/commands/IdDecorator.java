/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.decorator.commands;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.decorator.AbstractDecorator;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class IdDecorator extends AbstractDecorator {
  
  public static final String SUFFIX = "_ids";
  
  public IdDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }
  
  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, COMMAND_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getSymbol().isIsDerived())
      .forEach(c -> astcdPackage.addCDElement(createClass(c, origCD.getCDDefinition().getName())));
  }
  
  protected ASTCDClass createClass(ASTCDClass domainClass, String definitionName) {
    String classname = domainClass.getName();
  
    ASTCDClass ast = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Command<LongList>"))
      .setName(classname+SUFFIX)
      .build();
    
    getCD4C().addConstructor(ast, "umlp.commands.ids.Constructor", classname, COMMAND_PACKAGE, SUFFIX);
    getCD4C().addMethod(ast, "umlp.commands.ids.DoRun", classname, definitionName);

    getCD4C().addImport(ast, "umlp.*");
    getCD4C().addImport(ast,"umlp.command.Command");
    getCD4C().addImport(ast, "umlp.command.LongList");
    getCD4C().addImport(ast, "umlp.command.SimpleResult");
    getCD4C().addImport(ast, "umlp."+definitionName+"Manager");
  
    return ast;
  }
}
