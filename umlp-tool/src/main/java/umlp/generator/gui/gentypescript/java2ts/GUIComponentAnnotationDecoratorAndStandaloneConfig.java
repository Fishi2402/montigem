package umlp.generator.gui.gentypescript.java2ts;

import com.google.common.collect.Sets;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
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
    String qName = ast.getMCPackageDeclaration().getMCQualifiedName().getQName();
    for (ASTCDClass clazz : ast.getCDDefinition().getCDClassesList()) {
      GUIImportHelper importHelper = new GUIImportHelper(glex, clazz, gui.getPackage());
      String originalName = GUIGeneratorUtil.getInstance().getOriginalName(clazz);
      String qClassName = qName + "." + originalName;
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
              hwPath, outPath, qClassName, "html");
          if (hwHtmlPath.isPresent()) {
            htmlPath = hwHtmlPath.get();
          }
          Optional<String> hwScssPath = util.getRelativeImportPath(
              hwPath, outPath, qClassName, "scss");
          if (hwScssPath.isPresent()) {
            scssPaths.add(hwScssPath.get());
          }
        }
        else if (!UMLPGeneratorConfig.getInstance().isUseJsweet()) {
          importHelper.addImportString("import { Directive } from '@angular/core';");
          Optional<String> hwHtmlPath = util.getRelativeImportPath(
              hwPath, outPath, qClassName, "html");
          if (hwHtmlPath.isEmpty()) {
            htmlPath = util.getRelativeImportPathForAnnotation(
                outPath.toString(), new ArrayList<>(hwPath.getEntries()).get(0).toString(), qClassName);
          }
          Optional<String> hwScssPath = util.getRelativeImportPath(
              hwPath, outPath, qClassName, "scss");
          if (hwScssPath.isPresent()) {
            scssPaths.add("./" + originalName + ".scss");
          }
        }
        else {
          importHelper.addImportString("import { Component } from '@angular/core';");
          // TODO #3768
          Optional<String> hwHtmlPath = util.getRelativeImportPath(
              hwPath, outPath, qClassName, "html");
          if (hwHtmlPath.isPresent()) {
            htmlPath = hwHtmlPath.get();
          }
          Optional<String> hwScssPath = util.getRelativeImportPath(
              hwPath, outPath, qClassName, "scss");
          if (hwScssPath.isPresent()) {
            scssPaths.add("./" + originalName + ".scss");
          }
        }
        String selector = GUIGeneratorUtil.getInstance().pascal2KebabCase(originalName);
        HashSet<String> importedComponents = calculateImports(gui.getComponentQName(), viewComponents.getComponents(), importHelper);
        glex.replaceTemplate(
            GUI2ComponentTemplates.ANNOTATIONS,
            clazz,
            new TemplateHookPoint(GUI2ComponentTemplates.ANNOTATION_STANDALONECONFIG_COMPONENT, htmlPath, scssPaths, selector, importedComponents, hwPath, noHWC)
        );
      }
    }
  }

  protected HashSet<String> calculateImports(String componentQName, List<ASTGUIComponent> guiComponents, GUIImportHelper importHelper) {
    HashSet<String> importedComponents = Sets.newHashSet();
    for (ASTGUIComponent component: guiComponents) {
      if (!component.isPresentNameSymbol()) {
        Log.error("0x0008B Name symbol of " + component.getName() + " not present.");
      }

      // Avoid circular import dependencies for recursive component calls
      if (!component.getNameSymbol().getName().equals(componentQName)) {
        importHelper.addImport(component.getNameSymbol().getFullName());
        importedComponents.add(component.getNameSymbol().getFullName());
      }
    }
    return importedComponents;
  }
}
