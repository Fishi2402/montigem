/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import umlp.generator.domain.decorator.commands.GetDecorator;
import umlp.generator.gui.gentypescript.decorator.TSImportHelper;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.util.PathUtil;

import java.nio.file.Path;

public class TSGetDecorator extends GetDecorator {

  protected final Path outPath;
  protected final MCPath hwPath;

  public TSGetDecorator(GlobalExtensionManagement glex, MCPath hwPath, Path outPath) {
    super(glex);
    this.hwPath = hwPath;
    this.outPath = outPath;
  }

  protected ASTCDAttribute addGemId(ASTCDClass ast) {
    return getCD4C().addAttribute(ast, "private long gemId");
  }

  @Override
  protected void addConstructor(ASTCDClass ast, String classname, String packageName) {
    glex.replaceTemplate(GUI2ComponentTemplates.CONSTRUCTOR, ast,
        new TemplateHookPoint(getConstructorTemplate(), classname, "", SUFFIX, packageName));
  }

  @Override
  protected String getGetNewlySubscribedGemIdsTemplate() {
    return "umlp.gui2ts.commands.get.TSGetNewlySubscribedGemIds";
  }

  @Override
  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.gui2ts.commands.TSGetEffectedGemIds";
  }

  @Override
  protected String getDoRunTemplate() {
    return "umlp.gui2ts.commands.get.TSDoRun";
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.get.TSConstructor";
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
  protected String getGeneralCheckAccessPolicyTemplate() {
    return "umlp.gui2ts.accesspolicy.TSGeneralCheckAccessPolicy";
  }

  @Override
  protected void addDeSerImport(ASTCDCompilationUnit origCD, ASTCDClass ast, String cmdName) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));

    TSImportHelper importHelper = new TSImportHelper(glex, ast, hwPath, outPath);
    String importerQName = getPackageName(origCD, COMMAND_PACKAGE) + "." + ast.getName();

    importHelper.addImport(getPackageName(origCD, COMMAND_PACKAGE) + "." + cmdName, importerQName);
  }

  @Override
  protected void addImports(ASTCDCompilationUnit origCD, ASTCDType domainType, ASTCDClass ast,
      String classname) {
    getCD4C().addImport(ast, PathUtil.getBackendRTERequestPath("Request"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("SimpleResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("LoadingState"));

    TSImportHelper importHelper = new TSImportHelper(glex, ast, hwPath, outPath);
    String importerQName = getPackageName(origCD, COMMAND_PACKAGE) + "." + ast.getName();

    importHelper.addImport(getPackageName(origCD, DEFAULT_PACKAGE)+"."+ classname, importerQName);
    importHelper.addImport(getPackageName(origCD, DEFAULT_PACKAGE)+ "." + classname + "AccessPolicy", importerQName);
    importHelper.addImport(getPackageName(origCD, DEFAULT_PACKAGE)+"."+getCorrectManager(domainType), importerQName);
  }
}
