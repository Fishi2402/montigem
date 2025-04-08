/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Joiners;
import umlp.generator.domain.decorator.commands.GetFreeGemIdRequestDecorator;
import umlp.generator.gui.gentypescript.decorator.TSImportHelper;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.util.PathUtil;

import java.nio.file.Path;

import static umlp.generator.domain.decorator.ManagerDecorator.SUFFIX;

public class TSGetFreeGemIdRequestDecorator extends GetFreeGemIdRequestDecorator {

  protected final Path outPath;
  protected final MCPath hwPath;

  public TSGetFreeGemIdRequestDecorator(GlobalExtensionManagement glex, MCPath hwPath, Path outPath) {
    super(glex);
    this.hwPath = hwPath;
    this.outPath = outPath;
  }

  @Override
  protected String getDoRunTemplate() {
    return "umlp.gui2ts.commands.getfreegemid.TSDoRun";
  }

  @Override
  protected String extendedClass() {
    return "Request<number>";
  }

  @Override
  protected void addConstructor(ASTCDClass ast, ASTCDCompilationUnit origCD) {
    glex.replaceTemplate(GUI2ComponentTemplates.CONSTRUCTOR, ast,
        new TemplateHookPoint(getConstructorTemplate(), CLASS_NAME,
            getPackageName(origCD, DEFAULT_PACKAGE)));
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.getfreegemid.TSConstructor";
  }

  @Override
  protected String getFromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.getfreegemid.TSFromJsonObject";
  }

  @Override
  protected void addImports(ASTCDClass ast, ASTCDCompilationUnit origCD) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("SimpleResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTERequestPath("Request"));

    TSImportHelper importHelper = new TSImportHelper(glex, ast, hwPath, outPath);
    String importerQName = getPackageName(origCD, COMMAND_PACKAGE) + "." + ast.getName();

    importHelper.addImport(Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE),
        origCD.getCDDefinition().getName() + SUFFIX), importerQName);
  }

  @Override
  protected void addDeSerImport(ASTCDClass ast, ASTCDCompilationUnit origCD) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));

    TSImportHelper importHelper = new TSImportHelper(glex, ast, hwPath, outPath);
    String importerQName = getPackageName(origCD, COMMAND_PACKAGE) + "." + ast.getName();

    importHelper.addImport(getPackageName(origCD, COMMAND_PACKAGE) + "." + CLASS_NAME, importerQName);
  }

}
