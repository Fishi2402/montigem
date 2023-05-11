/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.commands;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.util.PathUtil;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.commands.CommandDecorator.DE_SER;

public class GetFilteredDecorator extends AbstractDeSerDecorator {

  public static final String SUFFIX = "_get_filtered";

  public GetFilteredDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    addedDeSers.clear();
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
      .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Request<Long>"))
      .setName(classname+SUFFIX)
      .build();

    getCD4C().addConstructor(ast, getConstructorTemplate(), classname,"", SUFFIX, packageName);
    getCD4C().addMethod(ast, getDoRunTemplate(), classname, origCD.getCDDefinition().getName());

    getCD4C().addImport(ast, PathUtil.getBackendRTERequestPath("Request"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("ListResult"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+origCD.getCDDefinition().getName()+ ManagerDecorator.SUFFIX);

    return ast;
  }

  protected String getDoRunTemplate() {
    return "umlp.commands.get_filtered.DoRun";
  }

  protected String getConstructorTemplate() {
    return "umlp.commands.get_filtered.Constructor";
  }

  protected ASTCDClass createDeSer(ASTCDCompilationUnit origCD, ASTCDClass domainClass) {
    String cmdName = domainClass.getName() + SUFFIX;

    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("CommandDeSer<" + cmdName + ">"))
            .setName(cmdName+DE_SER)
            .build();

    getCD4C().addMethod(ast, getGetFiltered_FromJsonObjectTemplate(), cmdName);
    getCD4C().addMethod(ast, getGetFiltered_BuildJsonTemplate(),cmdName);

    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.JsonPrinter");
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.json.JsonObject");
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));

    addedDeSers.add(ast);
    return ast;
  }

  protected String getGetFiltered_BuildJsonTemplate() {
    return "umlp.commands.deser.GetFiltered_BuildJson";
  }

  protected String getGetFiltered_FromJsonObjectTemplate() {
    return "umlp.commands.deser.GetFiltered_FromJsonObject";
  }
}
