package umlp.generator.gui.gentypescript.java2ts;

import com.google.common.collect.Sets;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.io.paths.MCPath;
import umlp.generator.domain.UMLPGeneratorConfig;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.gentypescript.decorator.GUIImportHelper;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class GUIComponentAnnotationDecoratorAndStandaloneConfig {

  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

  protected final GlobalExtensionManagement glex;
  protected final MCPath hwPath;
  protected final Path outPath;

  public GUIComponentAnnotationDecoratorAndStandaloneConfig(
      final GlobalExtensionManagement glex, MCPath hwPath, Path outPath) {
    this.glex = glex;
    this.hwPath = hwPath;
    this.outPath = outPath;
  }

  public void transform(ASTCDCompilationUnit ast, ASTGUICompilationUnit gui, GUIComponentsVisitor viewComponents) {
    for (ASTCDClass clazz : ast.getCDDefinition().getCDClassesList()) {
      GUIImportHelper importHelper = new GUIImportHelper(glex, clazz, gui.getPackage());
      String originalName = GUIGeneratorUtil.getInstance().getOriginalName(clazz);
      String qName = ast.getMCPackageDeclaration().getMCQualifiedName().getQName()
          + "." + originalName;
      if (!GUIGeneratorUtil.getInstance().isComponent(clazz)) {
        glex.replaceTemplate(
            GUI2ComponentTemplates.ANNOTATIONS,
            clazz,
            new TemplateHookPoint(GUI2ComponentTemplates.ANNOTATION_DIRECTIVE)
        );
      }
      else {
        boolean noHWC = !GUIGeneratorUtil.getInstance().existsHandwrittenClass(hwPath, qName);
        String htmlFileName = originalName + ".html";
        String htmlPath = "./" + htmlFileName;
        List<String> scssPaths = new ArrayList<>();

        if (noHWC) {
          importHelper.addImportString("import { Component } from '@angular/core';");
          Optional<String> hwHtmlPath = util.getRelativeImportPath(
              hwPath, outPath, qName, "html");
          if (hwHtmlPath.isPresent()) {
            htmlPath = hwHtmlPath.get();
          }
          Optional<String> hwScssPath = util.getRelativeImportPath(
              hwPath, outPath, qName, "scss");
          if (hwScssPath.isPresent()) {
            scssPaths.add(hwScssPath.get());
          }
        }
        else if (!UMLPGeneratorConfig.getInstance().isUseJsweet()) {
          importHelper.addImportString("import { Directive } from '@angular/core';");
          Optional<String> hwHtmlPath = util.getRelativeImportPath(
              hwPath, outPath, qName, "html");
          if (!hwHtmlPath.isPresent()) {
            htmlPath = util.getRelativeImportPathForAnnotation(
                outPath.toString(), new ArrayList<>(hwPath.getEntries()).get(0).toString(), qName).get();
          }
          Optional<String> hwScssPath = util.getRelativeImportPath(
              hwPath, outPath, qName, "scss");
          if (hwScssPath.isPresent()) {
            scssPaths.add("./" + originalName + ".scss");
          }
        }
        else {
          importHelper.addImportString("import { Component } from '@angular/core';");
          // TODO #3768
          Optional<String> hwHtmlPath = util.getRelativeImportPath(
              hwPath, outPath, qName, "html");
          if (hwHtmlPath.isPresent()) {
            htmlPath = hwHtmlPath.get();
          }
          Optional<String> hwScssPath = util.getRelativeImportPath(
              hwPath, outPath, qName, "scss");
          if (hwScssPath.isPresent()) {
            scssPaths.add("./" + originalName + ".scss");
          }
        }
        String selector = GUIGeneratorUtil.getInstance().pascal2KebabCase(originalName);
        HashSet<String> importedCompoments = Sets.newHashSet();
        for (ASTGUIComponent component : viewComponents.getComponents()) {
          importHelper.addImport(component.getNameSymbol().getFullName());
          importedCompoments.add(component.getNameSymbol().getFullName());
        }
        glex.replaceTemplate(
            GUI2ComponentTemplates.ANNOTATIONS,
            clazz,
            new TemplateHookPoint(GUI2ComponentTemplates.ANNOTATION_STANDALONECONFIG_COMPONENT, htmlPath, scssPaths, selector, importedCompoments, hwPath, noHWC)
        );
      }
    }
  }

}
