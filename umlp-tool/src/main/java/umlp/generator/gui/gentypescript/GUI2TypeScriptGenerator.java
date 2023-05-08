/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.Names;
import umlp.generator.gui.GUIComponentsVisitor;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

public class GUI2TypeScriptGenerator {

  protected final GeneratorEngine generatorEngine;
  protected GeneratorSetup setup;
  protected GUIComponentsVisitor viewComponents;
  protected MCPath hwcPath;
  public final static String ROUTES_FILE = "Routing";
  public final static String MODULES_FILE = "Module";
  public final static String TS_FILE_EXTENSION = "ts";

  public GUI2TypeScriptGenerator(GeneratorSetup generatorSetup, MCPath hwcPath,
      GUIComponentsVisitor viewComponents) {
    this.generatorEngine = new GeneratorEngine(generatorSetup);
    this.setup = generatorSetup;
    this.hwcPath = hwcPath;
    this.viewComponents = viewComponents;
  }

  public void generate(ASTGUICompilationUnit modelAST, String configTemplate) {
    TemplateController tc = setup.getNewTemplateController(configTemplate);
    TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);

    hpp.processValue(tc, modelAST,
        Arrays.asList(modelAST, setup.getGlex(), setup.getHandcodedPath(), this, viewComponents));
  }

  public void generate(ASTGUICompilationUnit decoratedAst) {
    this.setup.setTracing(false);
    GeneratorEngine genEngine = new GeneratorEngine(setup);

    String pathName = String.join("/", decoratedAst.getMCPackageDeclaration().getMCQualifiedName().getPartsList());
    String pathToSearch = decoratedAst.getMCPackageDeclaration().getMCQualifiedName().getQName();
    boolean existHWCRoutes = existsHandwrittenFile(hwcPath, pathToSearch + "."
        + decoratedAst.getComponentName() + ROUTES_FILE + "." + TS_FILE_EXTENSION, TS_FILE_EXTENSION);

    Path generatedFiles = Paths.get(pathName, decoratedAst.getComponentName()
        + (existHWCRoutes ? "TOP" : "") + ROUTES_FILE + "." + TS_FILE_EXTENSION);
    genEngine.generate("umlp.gui2ts.Routes", generatedFiles,
        decoratedAst.getGUIComponentDeclaration(), "routes", existHWCRoutes);

    generatedFiles = Paths.get(pathName,
        decoratedAst.getComponentName() + MODULES_FILE + "." + TS_FILE_EXTENSION);
    genEngine.generate("umlp.gui2ts.Module", generatedFiles, decoratedAst,
        viewComponents.getModules());
    /*
    for (ASTMCImportStatement is : decoratedAst.getMCImportStatementList()) {
      is.getMCQualifiedName().getParts(is.getMCQualifiedName().sizeParts() - 1)
      is.getQName();
    }

     */
  }


  /**
   * Check whewther an handwritten version of this generated file already exists and shall be integrated
   * (e.g. to apply TOP mechanism)
   */
  public static boolean existsHandwrittenFile(MCPath targetPath, String qualifiedName, String fileExtension) {
    String hwFile = Names.getPathFromPackage(qualifiedName) + fileExtension;
    Optional<URL> hwFilePath = targetPath.find(hwFile);
    boolean result = hwFilePath.isPresent();
    if (result) {
      Optional<Path> hwPath = MCPath.toPath(hwFilePath.get());
      hwPath.ifPresent(path -> Reporting.reportUseHandwrittenCodeFile(path,
          Paths.get(hwFile)));
    }
    Reporting.reportHWCExistenceCheck(targetPath, Paths.get(hwFile), hwFilePath);
    return result;
  }

}
