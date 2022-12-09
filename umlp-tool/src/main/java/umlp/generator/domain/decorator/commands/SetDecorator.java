/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.decorator.commands;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.GenService;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.commands.CommandDecorator.DE_SER;

public class SetDecorator extends AbstractDecorator {

  public static final String SUFFIX = "_set";

  public SetDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }
  
  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, COMMAND_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getSymbol().isIsDerived())
      .forEach(c ->
              CDSymbolTables.getAttributesInHierarchy(c)
          .stream()
          .filter(a -> !a.getModifier().isDerived())
                      .filter(a -> !a.getModifier().isDerived())
                      .forEach(a -> {
            astcdPackage.addCDElement(createClass(origCD, a, c));
            astcdPackage.addCDElement(createDeSer(origCD, a, c));
          }));
  }
  
  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, ASTCDAttribute attribute, ASTCDClass domainClass) {
    String classname = domainClass.getName()+SUFFIX
            + StringTransformations.capitalize(attribute.getName());
    
    ASTCDClass ast = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setName(classname)
      .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Command<Void>"))
      .build();

    String type = (new CD4CodeFullPrettyPrinter()).prettyprint(attribute.getMCType());
    ASTCDAttribute gemAttr = getCD4C().addAttribute(ast, "protected long gemId");
    ASTCDAttribute attr = getCD4C().addAttribute(ast, "protected " + type + " " + attribute.getName());

    getCD4C().addConstructor(ast, "umlp.commands.set.Constructor", classname, COMMAND_PACKAGE,
      attribute.getName(), type);

    getCD4C().addMethod(ast, "umlp.commands.set.DoRun", attribute.getName(), domainClass.getName(), origCD.getCDDefinition().getName());
    getCD4C().addMethod(ast, "umlp.commands.GetEffectedGemIds");
    AccessorDecorator accessor = new AccessorDecorator(glex);
    ast.addAllCDMembers(accessor.decorate(gemAttr));
    ast.addAllCDMembers(accessor.decorate(attr));

    getCD4C().addImport(ast, "umlp.command.StatusResult");
    getCD4C().addImport(ast, "umlp.command.Command");
    getCD4C().addImport(ast, "umlp.util.LogUtil");
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+".*");
    getCD4C().addImport(ast, "umlp.rte.NotificationScope");
    getCD4C().addImport(ast, "umlp.rte.GeneralErrorCollector");
    
    return ast;
  }

  protected ASTCDClass createDeSer(ASTCDCompilationUnit origCD, ASTCDAttribute attribute, ASTCDClass domainClass) {
    String cmdName = domainClass.getName()+SUFFIX
            + StringTransformations.capitalize(attribute.getName());

    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("CommandDeSer<" + cmdName + ">"))
            .setName(cmdName+DE_SER)
            .build();

    getCD4C().addMethod(ast, "umlp.commands.deser.Set_FromJsonObject", cmdName, attribute.getName(), GenService.calculateJSon(attribute));
    getCD4C().addMethod(ast, "umlp.commands.deser.Set_BuildJson", cmdName, attribute.getName(), GenService.getGetterName(attribute));

    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.JsonPrinter");
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.json.JsonObject");
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+".*");
    getCD4C().addImport(ast, "umlp.command.CommandDeSer");

    return ast;
  }
}
