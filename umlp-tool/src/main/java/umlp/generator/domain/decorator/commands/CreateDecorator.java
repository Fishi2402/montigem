/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.commands;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.DeSerDecorator;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.util.PathUtil;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.commands.CommandDecorator.DE_SER;

public class CreateDecorator extends AbstractDecorator {

  public static final String SUFFIX = "_create";

  public CreateDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }
  
  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, COMMAND_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getSymbol().isIsDerived())
      .forEach(c -> {
        astcdPackage.addCDElement(createClass(origCD, c, astcdPackage.getName()));
        astcdPackage.addCDElement(createDeSer(origCD, c));
      });
  }
  
  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, ASTCDClass domainClass, String packageName) {
    String classname = domainClass.getName();
    
    ASTCDClass ast = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Request<Void>"))
      .setName(classname+SUFFIX)
      .build();
    
    ASTCDAttribute attr = getCD4C().addAttribute(ast, "protected final " + classname + " " + StringTransformations.uncapitalize(classname));

    getCD4C().addConstructor(ast, "umlp.commands.Constructor",classname, packageName, SUFFIX);
    getCD4C().addMethod(ast, "umlp.commands.create.DoRun", classname, origCD.getCDDefinition().getName());
    getCD4C().addMethod(ast, "umlp.commands.GetEffectedGemIds", Lists.newArrayList(StringTransformations.uncapitalize(classname) + ".getGemId()"));
    AccessorDecorator accessor = new AccessorDecorator(glex);
    ast.addAllCDMembers(accessor.decorate(attr));

    getCD4C().addImport(ast, PathUtil.getBackendRTERequestPath("Request"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+classname);
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+origCD.getCDDefinition().getName()+ ManagerDecorator.SUFFIX);
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("StatusResult"));

    return ast;
  }

  protected ASTCDClass createDeSer(ASTCDCompilationUnit origCD, ASTCDClass domainClass) {
    String cmdName = domainClass.getName() + SUFFIX;

    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("CommandDeSer<" + cmdName + ">"))
            .setName(cmdName+DE_SER)
            .build();

    getCD4C().addMethod(ast, "umlp.commands.deser.FromJsonObject", domainClass.getName(), cmdName);
    getCD4C().addMethod(ast, "umlp.commands.deser.BuildJson", domainClass.getName(), cmdName);

    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.JsonPrinter");
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.json.JsonObject");
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+domainClass.getName());
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+domainClass.getName()+ DeSerDecorator.SUFFIX);

    return ast;
  }
}
