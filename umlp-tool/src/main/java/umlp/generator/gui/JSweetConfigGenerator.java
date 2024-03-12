/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.MCPath;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class JSweetConfigGenerator {

  protected GeneratorSetup generatorSetup;
  protected GeneratorEngine generatorEngine;

  public JSweetConfigGenerator(GeneratorSetup generatorSetup) {
    this.generatorSetup = generatorSetup;
    this.generatorEngine = new GeneratorEngine(generatorSetup);
    generatorSetup.setTracing(false);
  }

  public void generate(String domainName) {

    File jsweetConfig = calcTOPFile("jsweetconfig", "json");
    generatorEngine.generateNoA(
        "umlp.jsweetconfig.JSweetConfigJSON",
        jsweetConfig.toPath(),
        domainName);

    File importAdapter = calcTOPFile(Paths.get("jsweet_extension", "umlp", "jsweet", "extension", "domainAdapters"), domainName + "ImportAdapter", "java");
    generatorEngine.generateNoA(
        "umlp.jsweetconfig.JSweetDomainImportAdapter",
        importAdapter.toPath(),
        importAdapter.getName(),
        domainName);
  }

  protected File calcTOPFile(String origName, String fileExt) {
    File f = new File(origName + "." + fileExt);

    if (existsHandwrittenFile(generatorSetup.getHandcodedPath(), f.getPath())) {
      f = new File(origName + "TOP" + "." + fileExt);
    }
    return f;
  }

  protected File calcTOPFile(Path basePath, String origName, String fileExt) {
    return calcTOPFile(basePath.toString() + File.separator + origName, fileExt);
  }

  protected boolean existsHandwrittenFile(MCPath targetPath, String hwFile) {
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
