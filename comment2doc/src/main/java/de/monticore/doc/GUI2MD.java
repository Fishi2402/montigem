package de.monticore.doc;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUI2MD {

  protected final GlobalExtensionManagement glex;
  protected final GeneratorEngine generatorEngine;
  protected final File outDir;

  public GUI2MD(File outDir) {
    this.outDir = outDir;
    GeneratorSetup genSetup = new GeneratorSetup();
    // set output directory
    genSetup.setOutputDirectory(outDir);
    glex = new GlobalExtensionManagement();
    genSetup.setGlex(glex);
    genSetup.setTracing(false);
    generatorEngine = new GeneratorEngine(genSetup);
  }

  public void generateMain(List<GUIDocArtifact> docs) {
    Map<String, List<GUIDocArtifact>> sublibs = new HashMap<>();
    for (GUIDocArtifact doc : docs) {
      String sublib = doc.getComponentLibraryName();
      if (!sublibs.containsKey(sublib)) {
        sublibs.put(sublib, new ArrayList<>());
      }
      sublibs.get(sublib).add(doc);
    }
    Path out = Paths.get("Frontend-RTE.md");
    generatorEngine.generateNoA("umlpdoc.GUI2MainMD", out,
        sublibs, outDir.toPath().resolve(out), new PrintUtil());
  }

  public void generate(GUIDocArtifact doc) {
    generatorEngine.generateNoA("umlpdoc.GUI2MD", doc.calcRelativeOutputPath(),
        doc, new PrintUtil());
  }

}
