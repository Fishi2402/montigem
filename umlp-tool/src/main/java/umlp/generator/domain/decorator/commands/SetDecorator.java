/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.commands;/* (c) https://github.com/MontiCore/monticore */

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.ManagerDecorator;

import java.util.Optional;

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
            .forEach(c -> {
              CDSymbolTables.getAttributesInHierarchy(c)
                      .stream()
                      .filter(a -> !a.getModifier().isDerived())
                      .filter(a -> !GenService.isList(a))
                      .forEach(a -> {
                        astcdPackage.addCDElement(createClass(origCD, a.getName(), a.getSymbol().getType(), c, false));
                        astcdPackage.addCDElement(createDeSer(origCD, a.getName(), a.getSymbol().getType(), c, false));
                      });
              CDSymbolTables.getAssociationsInHierarchy(c)
                      .stream()
                      .filter(a -> !a.getModifier().isDerived())
                      .filter(a -> !GenService.isBidirectional(a))
                      .filter(a -> !GenService.isList(a))
                      .forEach(a -> {
                        astcdPackage.addCDElement(createClass(origCD, a.getName(), a.getSymbol().getType(), c, true));
                        astcdPackage.addCDElement(createDeSer(origCD, a.getName(), getSymType("long"), c, true));
                      });            });
  }


  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, String name, SymTypeExpression type, ASTCDClass domainClass, boolean isGem) {
    String classname = domainClass.getName()+SUFFIX
            + StringTransformations.capitalize(name);
    String completeName = isGem?name+"GemId":name;

    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(classname)
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Command<Void>"))
            .build();

    ASTCDAttribute gemAttr = getCD4C().addAttribute(ast, "protected long gemId");

    ASTCDAttribute attr = isGem?getCD4C().addAttribute(ast, "protected long " + completeName)
            :getCD4C().addAttribute(ast, "protected " + type.print() + " " + completeName);

    getCD4C().addConstructor(ast, "umlp.commands.set.Constructor", classname, COMMAND_PACKAGE,
            completeName, isGem?"long":type.print());

    getCD4C().addMethod(ast, "umlp.commands.set.DoRun", name, domainClass.getName(), origCD.getCDDefinition().getName(), isGem, type.print());
    if (isGem) {
      getCD4C().addMethod(ast, "umlp.commands.GetEffectedGemIds", Lists.newArrayList("gemId" , name+"GemId"));
    } else {
      getCD4C().addMethod(ast, "umlp.commands.GetEffectedGemIds", Lists.newArrayList("getId()"));
    }
    AccessorDecorator accessor = new AccessorDecorator(glex);
    ast.addAllCDMembers(accessor.decorate(gemAttr));
    ast.addAllCDMembers(accessor.decorate(attr));

    getCD4C().addImport(ast, "umlp.command.StatusResult");
    getCD4C().addImport(ast, "umlp.command.Command");
    getCD4C().addImport(ast, "umlp.util.LogUtil");
    getCD4C().addImport(ast, "umlp.rte.NotificationScope");
    getCD4C().addImport(ast, "umlp.rte.GeneralErrorCollector");
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+domainClass.getName());
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+origCD.getCDDefinition().getName()+ ManagerDecorator.SUFFIX);
    if (GenService.isJavaType(type)) {
      getCD4C().addImport(ast, type.printFullName());
    } else if (isGem) {
      getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+type.print());
    }

    return ast;
  }

  protected ASTCDClass createDeSer(ASTCDCompilationUnit origCD, String name, SymTypeExpression type, ASTCDClass domainClass, boolean isGem) {
    String cmdName = domainClass.getName()+SUFFIX
            + StringTransformations.capitalize(name);

    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("CommandDeSer<" + cmdName + ">"))
            .setName(cmdName+DE_SER)
            .build();

    if (isGem) {
      type = getSymType("long");
      name += "GemId";
    }
    getCD4C().addMethod(ast, "umlp.commands.deser.Set_FromJsonObject", cmdName, name, GenService.calculateJSon(type));
    getCD4C().addMethod(ast, "umlp.commands.deser.Set_BuildJson", cmdName, name, GenService.getGetterName(name, type));

    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.JsonPrinter");
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.json.JsonObject");
    getCD4C().addImport(ast, "umlp.command.CommandDeSer");

    return ast;
  }

  // TODO (MB): Bessere Lösung?
  protected SymTypeExpression getSymType(String type) {
    Optional<TypeSymbol> primitive = BasicSymbolsMill.globalScope().resolveType(type);
    if(primitive.isPresent()){
      return SymTypeExpressionFactory.createPrimitive(primitive.get());
    }else{
      return SymTypeExpressionFactory.createObscureType();
    }
  }
}
