/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.commands;/* (c) https://github.com/MontiCore/monticore */

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.util.PathUtil;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.commands.CommandDecorator.DE_SER;

public class GetDecorator extends AbstractDeSerDecorator {

  public static final String SUFFIX = "_get";

  public GetDecorator(GlobalExtensionManagement glex) {
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

    origCD.getCDDefinition().getCDInterfacesList().stream().filter(c -> !c.getSymbol().isIsDerived())
        .forEach(c -> {
          astcdPackage.addCDElement(createClass(origCD, c, astcdPackage.getName()));
          astcdPackage.addCDElement(createDeSer(origCD, c));
        });
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, ASTCDType domainType, String packageName) {
    String classname = domainType.getName();
    String domainPackage = packageName.substring(0, packageName.length()-".commands".length());

    ASTCDClass ast = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Request<"+classname+">"))
      .setName(classname+SUFFIX)
      .build();
    ASTCDAttribute attr = addGemId(ast);

    addConstructor(ast, classname, packageName);
    getCD4C().addMethod(ast, getDoRunTemplate(), classname, domainPackage, getCorrectManager(domainType));
    getCD4C().addMethod(ast, getGetEffectedGemIdsTemplate(), Lists.newArrayList());
    getCD4C().addMethod(ast, getGetNewlySubscribedGemIdsTemplate());
    AccessorDecorator accessor = new AccessorDecorator(glex);
    ast.addAllCDMembers(accessor.decorate(attr));

    getCD4C().addImport(ast, PathUtil.getBackendRTERequestPath("Request"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("SimpleResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("LoadingState"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+classname);
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+getCorrectManager(domainType));

    return ast;
  }

  protected ASTCDAttribute addGemId(ASTCDClass ast) {
    return getCD4C().addAttribute(ast, "private final long gemId");
  }

  protected void addConstructor(ASTCDClass ast, String classname, String packageName) {
    getCD4C().addConstructor(ast, getConstructorTemplate(), classname,"", SUFFIX, packageName);
  }

  protected String getGetNewlySubscribedGemIdsTemplate() {
    return "umlp.commands.get.GetNewlySubscribedGemIds";
  }

  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.commands.GetEffectedGemIds";
  }

  protected String getDoRunTemplate() {
    return "umlp.commands.get.DoRun";
  }

  protected String getConstructorTemplate() {
    return "umlp.commands.get.Constructor";
  }

  protected ASTCDClass createDeSer(ASTCDCompilationUnit origCD, ASTCDType domainType) {
    String cmdName = domainType.getName() + SUFFIX;

    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("CommandDeSer<" + cmdName + ">"))
            .setName(cmdName+DE_SER)
            .build();

    getCD4C().addMethod(ast, getGet_FromJsonObjectTemplate(), cmdName);
    getCD4C().addMethod(ast, getGet_BuildJsonTemplate(),cmdName);

    addDeSerImport(origCD, ast, cmdName);

    addedDeSers.add(ast);
    return ast;
  }

  protected void addDeSerImport(ASTCDCompilationUnit origCD, ASTCDClass ast, String cmdName) {
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.JsonPrinter");
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.json.JsonObject");
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
  }

  protected String getGet_BuildJsonTemplate() {
    return "umlp.commands.deser.Get_BuildJson";
  }

  protected String getGet_FromJsonObjectTemplate() {
    return "umlp.commands.deser.Get_FromJsonObject";
  }
}
