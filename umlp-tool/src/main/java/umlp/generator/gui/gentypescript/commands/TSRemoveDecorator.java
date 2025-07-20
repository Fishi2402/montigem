/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.commands.RemoveDecorator;
import umlp.generator.gui.gentypescript.decorator.TSImportHelper;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.util.PathUtil;

import java.nio.file.Path;
import java.util.Set;

public class TSRemoveDecorator extends RemoveDecorator {

  protected final Path outPath;
  protected final MCPath hwPath;

  public TSRemoveDecorator(GlobalExtensionManagement glex, MCPath hwPath, Path outPath, Set<String> supportedDeSers) {
    super(glex, supportedDeSers);
    this.hwPath = hwPath;
    this.outPath = outPath;
  }

  @Override
  protected String extendedClass() {
    return "Command<void>";
  }

  @Override
  protected void addConstructor(ASTCDClass ast, String classname, String packageName,
      String attrName, String type) {
    glex.replaceTemplate(GUI2ComponentTemplates.CONSTRUCTOR, ast,
        new TemplateHookPoint(getConstructorTemplate(), classname, packageName, attrName, type));
  }

  @Override
  protected void addJavaImport(ASTCDType ast, SymTypeExpression type) {
    // don't
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.remove.TSConstructor";
  }

  @Override
  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.gui2ts.commands.TSGetEffectedGemIds";
  }

  @Override
  protected String getDoRunTemplate() {
    return "umlp.gui2ts.commands.remove.TSDoRun";
  }

  @Override
  protected String getAdd_BuildJsonTemplate() {
    return "umlp.gui2ts.commands.deser.TSAdd_BuildJson";
  }

  @Override
  protected String getAdd_FromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.deser.TSAdd_FromJsonObject";
  }

  @Override
  protected String getCheckAccessPolicyTemplate() {
    return "umlp.gui2ts.accesspolicy.TSGeneralCheckAccessPolicy";
  }

  @Override
  protected void addDeSerImport(ASTCDClass ast, ASTCDCompilationUnit origCD, boolean useDeSer,
      String baseClassDecorator, SymTypeExpression type, SymTypeExpression innerType, String cmdName) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    TSImportHelper importHelper = new TSImportHelper(glex, ast, hwPath, outPath);
    String importerQName = getPackageName(origCD, COMMAND_PACKAGE) + "." + ast.getName();

    importHelper.addImport(getPackageName(origCD, COMMAND_PACKAGE) + "." + cmdName, importerQName);
    if (useDeSer) {
      importHelper.addImport(getPackageName(origCD, DEFAULT_PACKAGE) + "." + baseClassDecorator, importerQName);
    }
    if (GenService.isCDEnum(innerType)) {
      importHelper.addImport(getPackageName(origCD, DEFAULT_PACKAGE) + "." + ((SymTypeOfGenerics)type).getArgument(0).print(), importerQName);
    }
  }

  @Override
  protected void addImports(ASTCDCompilationUnit origCD, ASTCDType domainClass, ASTCDClass ast) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("StatusResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(ast, PathUtil.getBackendRTEUtilPath("LogUtil"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("GeneralErrorCollector"));
    TSImportHelper importHelper = new TSImportHelper(glex, ast, hwPath, outPath);
    String importerQName = getPackageName(origCD, COMMAND_PACKAGE) + "." + ast.getName();

    importHelper.addImport(getPackageName(origCD, DEFAULT_PACKAGE) + "." + domainClass.getName(), importerQName);
    importHelper.addImport(getPackageName(origCD, DEFAULT_PACKAGE) + "." + getCorrectManager(domainClass), importerQName);
    importHelper.addImport(getPackageName(origCD, DEFAULT_PACKAGE)+ "." + domainClass.getName() + "AccessPolicy", importerQName);
  }
}
