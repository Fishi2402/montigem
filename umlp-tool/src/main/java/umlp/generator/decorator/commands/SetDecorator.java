/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.decorator.commands;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import umlp.UMLPMill;
import umlp.generator.decorator.AbstractDecorator;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class SetDecorator extends AbstractDecorator {
  
  public SetDecorator(GlobalExtensionManagement glex) {
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
          .forEach(a -> astcdPackage.addCDElement(createClass(a,c, origCD.getCDDefinition().getName()))));
  }
  
  protected ASTCDClass createClass(ASTCDAttribute attribute, ASTCDClass domainClass, String definitionName) {
    String classname = domainClass.getName()+"_set_"+attribute.getName();
    
    ASTCDClass ast = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setName(classname)
      .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Command<Void>"))
      .build();
    
    getCD4C().addAttribute(ast, "protected long gemId");
    getCD4C().addAttribute(ast, "protected "
      +attribute.getMCType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()))
      +" "+ attribute.getName());
    getCD4C().addConstructor(ast, "umlp.commands.set.Constructor", classname, COMMAND_PACKAGE,
      attribute.getName(), attribute.getMCType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())));
    getCD4C().addMethod(ast, "umlp.commands.set.DoRun", attribute.getName(), domainClass.getName(), definitionName);
    getCD4C().addMethod(ast, "umlp.commands.error.GetEffectedGemIds");
    getCD4C().addImport(ast, "umlp.command.StatusResult");
    getCD4C().addImport(ast, "umlp.command.Command");
    getCD4C().addImport(ast, "umlp.util.LogUtil");
    getCD4C().addImport(ast, "umlp."+domainClass.getName());
    getCD4C().addImport(ast, "umlp."+definitionName+"Manager");
    getCD4C().addImport(ast, "umlp.rte.NotificationScope");
    getCD4C().addImport(ast, "umlp.rte.GeneralErrorCollector");
    
    return ast;
  }
}
