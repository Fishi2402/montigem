package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclaration;
import umlp.generator.gui.GUIGeneratorUtil;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
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
        String qName = ast.getMCPackageDeclaration().getMCQualifiedName().getQName()
            + "." + clazz.getName();

        Optional<String> hwHtmlPath = util.getRelativeHandwrittenPath(
            hwPath, outPath, qName, "html");
        if (hwHtmlPath.isPresent()) {
          htmlPath = hwHtmlPath.get();
        }

        List<String> scssPaths = new ArrayList<>();

        Optional<String> hwScssPath = util.getRelativeHandwrittenPath(
            hwPath, outPath, qName, "scss");
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

}
