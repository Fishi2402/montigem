package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclaration;
import umlp.generator.gui.GUIGeneratorUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class GUIComponentAnnotationDecorator {

  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();
  protected final GlobalExtensionManagement glex;
  protected final MCPath hwPath;
  protected final Path outPath;

  public GUIComponentAnnotationDecorator(
      final GlobalExtensionManagement glex, MCPath hwPath, Path outPath
  ) {
    this.glex = glex;
    this.hwPath = hwPath;
    this.outPath = outPath;
  }

  public void transform(ASTCDCompilationUnit ast) {
    Collection<Path> hwFilePaths = new ArrayList<>();
    for (Path p : hwPath.getEntries()) {
      hwFilePaths.add(getFilePath(p, ast.getMCPackageDeclaration()));
    }
    for (ASTCDClass clazz : ast.getCDDefinition().getCDClassesList()) {
      if (util.isTop(clazz.getName())) {
        glex.replaceTemplate(
            GUI2ComponentTemplates.ANNOTATIONS,
            new TemplateHookPoint(GUI2ComponentTemplates.ANNOTATION_DIRECTIVE)
        );
        glex.bindStringHookPoint("ClassContent:Imports", getDirectiveImport());
      }
      else {
        String htmlFileName = clazz.getName() + ".html";
        String htmlPath = "./" + htmlFileName;

        Optional<String> hwHtmlPath = getRelativeHandwrittenPath(
            hwFilePaths, outPath, htmlFileName);
        if (hwHtmlPath.isPresent()) {
          htmlPath = hwHtmlPath.get();
        }

        String scssFileName = clazz.getName() + ".scss";
        List<String> scssPaths = new ArrayList<>();
        Optional<String> hwScssPath = getRelativeHandwrittenPath(
            hwFilePaths, outPath, scssFileName);
        if (hwScssPath.isPresent()) {
          scssPaths.add(hwScssPath.get());
        }

        glex.replaceTemplate(
            GUI2ComponentTemplates.ANNOTATIONS,
            new TemplateHookPoint(GUI2ComponentTemplates.ANNOTATION_COMPONENT, clazz, htmlPath, scssPaths)
        );
        glex.bindStringHookPoint("ClassContent:Imports", getComponentImport());
      }
    }
  }

  protected String getDirectiveImport() {
    return "import { Directive } from '@angular/core';";
  }

  protected String getComponentImport() {
    return "import { Component } from '@angular/core';";
  }

  public Optional<String> getRelativeHandwrittenPath(
      Collection<Path> handcodedPath,
      Path outputPath,
      String fileName
  ) {
    Iterator<Path> targetPathIt = handcodedPath.iterator();
    while (targetPathIt.hasNext()) {
      Path it = targetPathIt.next();
      Path hwFile = it.resolve(fileName);
      if (Files.exists(hwFile)) {
        String path = outputPath
            .getParent()
            .toAbsolutePath()
            .normalize()
            .relativize(hwFile.toAbsolutePath())
            .toString();
        path = path.replace("\\", "/");
        return Optional.of(path);
      }
    }
    return Optional.empty();
  }

  public Path getFilePath(Path input, ASTMCPackageDeclaration packageDecl) {
    String localPath = String.join("/", packageDecl.getMCQualifiedName().getPartsList());
    return Paths.get(input.toString(), localPath);
  }

}
