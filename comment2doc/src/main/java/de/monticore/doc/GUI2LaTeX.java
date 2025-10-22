package de.monticore.doc;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._parser.GUIDSLParser;
import de.monticore.guidsl._prettyprint.GUIDSLExamplePrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.NoSuchElementException;

import static de.monticore.doc.PrintUtilForLaTex.removeAllComments;

public class GUI2LaTeX {

  protected final GlobalExtensionManagement glex;
  protected final GeneratorEngine generatorEngine;
  protected final File outDir;

  public GUI2LaTeX(File outDir) {
    GeneratorSetup genSetup = new GeneratorSetup();
    // set output directory
    this.outDir = outDir;
    genSetup.setOutputDirectory(outDir);
    glex = new GlobalExtensionManagement();
    genSetup.setGlex(glex);
    genSetup.setTracing(false);
    generatorEngine = new GeneratorEngine(genSetup);
  }

  /**
   * Generates LaTeX documentation for a GUI component.
   */
  public void generate(List<GUIDocArtifact> docs, boolean isPreProc) {
    Path mainTexPath = Paths.get("main.tex");
    generatorEngine.generateNoA("umlpdoc.GUI2MainLaTeX", mainTexPath, docs);

    for (GUIDocArtifact doc : docs) {
      Path texPath = Paths.get(doc.getComponentAST().getComponentName() + ".tex");
      generatorEngine.generateNoA("umlpdoc.GUI2PartLaTeX", texPath, doc, new CDDocUtilForLaTeX());

      if (!doc.getExampleList().isEmpty()) {
        String exampleGui = doc.getExampleList().iterator().next();
        Path exampleGuiPath = outDir.toPath().resolve(Paths.get(exampleGui + ".gui"));
        generateExamples(isPreProc, doc, exampleGui, exampleGuiPath);
      }

      if (!doc.getImageList().isEmpty()) {
        String exampleImage = doc.getImageList().iterator().next();
        Path exampleImagePath = outDir.toPath().resolve(Paths.get(
            doc.getExampleImage(exampleImage).getFileName().toString()
        ));
        generateImages(doc, exampleImage, exampleImagePath);
      }
    }
  }

  private void generateImages(GUIDocArtifact doc, String image, Path imagePath) {
    try {
      Files.createDirectories(imagePath.getParent());
      if (Files.notExists(imagePath)) {
        Files.copy(doc.getExampleImage(image), imagePath);
      }
    } catch (IOException e) {
      Log.error("0xD0010 Unable to create file " + imagePath + ": " + e.getMessage());
    }

  }

  private void generateExamples(boolean isPreProc, GUIDocArtifact doc, String example, Path examplePath) {
    try {
      Files.createDirectories(examplePath.getParent());
      if (Files.notExists(examplePath)) {
        Files.createFile(examplePath);
      }

      if (!isPreProc) {
        // Write content without preprocessing - remove comments except the GitHub reference
        String originalContent = doc.getExampleModel(example);
        String processedContent = removeAllComments(originalContent);

        Files.write(examplePath,
            processedContent.getBytes(),
            StandardOpenOption.TRUNCATE_EXISTING
        );
      }
      else {
        GUIDSLParser parser = new GUIDSLParser();
        ASTGUICompilationUnit ast = parser.parse_String(doc.getExampleModel(example)).orElseThrow();

        GUIDSLExamplePrettyPrinter printer = new GUIDSLExamplePrettyPrinter(new IndentPrinter(), false);
        String printed = printer.prettyprint(ast);

        // Write content with preprocessing
        Files.write(examplePath, printed.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
      }
    } catch (IOException | NoSuchElementException e) {
      Log.error("0xD000F Unable to create file " + examplePath + ": " + e.getMessage());
    }
  }
}
