package umlp.generator.domain.decorator.commands;

import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.DeSerDecorator;
import umlp.generator.util.PathUtil;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.commands.CommandDecorator.DE_SER;


// TODO: ts
public class ManagerSubscriptionDecorator extends AbstractDeSerDecorator {
  public static final String SUFFIX = "_subscribeManagerEvents";

  public ManagerSubscriptionDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    addedDeSers.clear();
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, COMMAND_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream()
        .filter(c -> !c.getSymbol().isIsDerived())
        .filter(c -> !c.getSymbol().isIsAbstract())
        .forEach(c -> {
          astcdPackage.addCDElement(withOrigin(createClass(origCD, c, astcdPackage.getName())));
          astcdPackage.addCDElement(withOrigin(createDeSer(origCD, c)));
        });
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, ASTCDClass domainClass, String packageName) {
    String classname = domainClass.getName();

    ASTCDClass ast = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(extendedClass()))
        .setName(classname+SUFFIX)
        .build();

    addConstructor(ast, classname, packageName);
    getCD4C().addMethod(ast, getDoRunTemplate(), getCorrectManager(domainClass), domainClass.getName() + "Observer", classname);
    getCD4C().addMethod(ast, getGetEffectedGemIdsTemplate(), Lists.newArrayList());

    addImports(origCD, domainClass, ast, classname);
    return ast;
  }

  protected void addImports(ASTCDCompilationUnit origCD, ASTCDClass domainClass, ASTCDClass ast,
                            String classname) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("StatusResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandManager"));
    getCD4C().addImport(ast, "com.google.common.collect.Sets");
  }

  protected String extendedClass() {
    return "Command<Void>";
  }

  protected void addConstructor(ASTCDClass ast, String classname, String packageName) {
    getCD4C().addConstructor(ast, getConstructorTemplate(), classname + SUFFIX, packageName);
  }

  protected String getConstructorTemplate() {
    return "umlp.commands.subscribeManagerEvents.Constructor";
  }

  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.commands.GetEffectedGemIds";
  }

  protected String getDoRunTemplate() {
    return "umlp.commands.subscribeManagerEvents.DoRun";
  }

  protected ASTCDClass createDeSer(ASTCDCompilationUnit origCD, ASTCDClass domainClass) {
    String cmdName = domainClass.getName() + SUFFIX;

    ASTCDClass ast = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("CommandDeSer<" + cmdName + ">"))
        .setName(cmdName+DE_SER)
        .build();

    getCD4C().addMethod(ast, getSubscribeManagerEvent_FromJsonObjectTemplate(), cmdName);
    getCD4C().addMethod(ast, getSubscribeManagerEvent_BuildJsonTemplate(), cmdName);

    addDeSerImport(ast, origCD, domainClass, cmdName);

    addedDeSers.add(ast);
    return ast;
  }

  protected void addDeSerImport(ASTCDClass ast, ASTCDCompilationUnit origCD, ASTCDClass domainClass, String cmdName) {
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.JsonPrinter");
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.json.JsonObject");
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+domainClass.getName());
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+domainClass.getName()+ DeSerDecorator.SUFFIX);
  }

  protected String getSubscribeManagerEvent_BuildJsonTemplate() {
    return "umlp.commands.deser.SubscribeManagerEvent_BuildJson";
  }

  protected String getSubscribeManagerEvent_FromJsonObjectTemplate() {
    return "umlp.commands.deser.SubscribeManagerEvent_FromJsonObject";
  }
}
