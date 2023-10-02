package umlp.generator.gui.gentypescript.commands;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.commands.CalculateDecorator;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.gui.gentypescript.printer.TypeScriptTypes;
import umlp.generator.util.PathUtil;

import java.util.Set;

public class TSCalculateDecorator extends CalculateDecorator {

  public TSCalculateDecorator(GlobalExtensionManagement glex, Set<String> supportedDeSers) {
    super(glex, supportedDeSers);
  }

  @Override
  protected String getType(SymTypeExpression type) {
    String t = null;
    if (type.isPrimitive()) {
      t = TypeScriptTypes.toTSType(type.print());
    }
    else if (type.isGenericType()) {
      var genType = (SymTypeOfGenerics) type;

      if (GenService.isList(type)) {
        SymTypeExpression inner = genType.getArgument(0);
        if (inner.isPrimitive()) {
          t = TypeScriptTypes.toTSType(TypeScriptTypes.toTSType(inner.print()));
        }
        else {
          t = TypeScriptTypes.toTSType(inner.print());
        }
      }
      else {
        Log.error("0x3F9F77 Can not generate DeSer for generic type " + type.print());
      }

    }
    else {
      t = TypeScriptTypes.toTSType(type.print());
    }
    return t;
  }

  @Override
  protected void addConstructor(ASTCDClass ast, String classname, String packageName) {
    glex.replaceTemplate(GUI2ComponentTemplates.CONSTRUCTOR, ast,
        new TemplateHookPoint(getConstructorTemplate(), classname, "", "", packageName));
  }

  @Override
  protected void addJavaImport(ASTCDClass ast, SymTypeExpression type) {
    if (!TypeScriptTypes.isDefault(type.print())) {
      getCD4C().addImport(ast, type.print());
    }
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.get.TSConstructor";
  }

  @Override
  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.gui2ts.commands.TSGetEffectedGemIds";
  }

  @Override
  protected String getDoRunSimpleTemplate() {
    return "umlp.gui2ts.commands.calculate.TSDoRunSimple";
  }

  @Override
  protected String getDoRunListTemplate() {
    return "umlp.gui2ts.commands.calculate.TSDoRunList";
  }

  @Override
  protected String getGet_BuildJsonTemplate() {
    return "umlp.gui2ts.commands.deser.TSGet_BuildJson";
  }

  @Override
  protected String getGet_FromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.deser.TSGet_FromJsonObject";
  }

  @Override
  protected void addDeSerImport(ASTCDClass ast, ASTCDCompilationUnit origCD, boolean useDeSer,
      boolean isCDEnum, String baseClassDecorator, String typeName, String cmdName) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE) + "." + cmdName);
    if (isCDEnum) {
      getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + Names.getSimpleName(typeName));
    }
    if (useDeSer) {
      getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + baseClassDecorator);
    }
  }

  @Override
  protected void addImports(ASTCDCompilationUnit origCD, ASTCDClass domainClass, ASTCDClass ast) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("LoadingState"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("ListResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("SimpleResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+ domainClass.getName());
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+getCorrectManager(domainClass));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+ "." + domainClass.getName() + "AccessPolicy");
  }
}
