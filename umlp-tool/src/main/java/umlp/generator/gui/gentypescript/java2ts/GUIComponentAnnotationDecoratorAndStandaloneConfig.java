package umlp.generator.gui.gentypescript.java2ts;

import com.google.common.collect.Sets;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.UMLPGeneratorConfig;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.gentypescript.decorator.TSImportHelper;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static de.monticore.generating.GeneratorEngine.existsHandwrittenClass;

public class GUIComponentAnnotationDecoratorAndStandaloneConfig {

  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

  protected final GlobalExtensionManagement glex;
  protected final MCPath hwPath;
  protected final Path outPath;
  protected final MCPath importsPath;

  public GUIComponentAnnotationDecoratorAndStandaloneConfig(
      final GlobalExtensionManagement glex, MCPath hwPath, Path outPath, MCPath importsPath) {
    this.glex = glex;
    this.hwPath = hwPath;
    this.outPath = outPath;
    this.importsPath = importsPath;
  }

  public void transform(ASTCDCompilationUnit ast, ASTGUICompilationUnit gui, GUIComponentsVisitor viewComponents) {
    String qName = ast.getMCPackageDeclaration().getMCQualifiedName().getQName();
    for (ASTCDClass clazz : ast.getCDDefinition().getCDClassesList()) {
      TSImportHelper importHelper = new TSImportHelper(glex, clazz, hwPath, outPath, importsPath);
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
        boolean noHWC = !GUIGeneratorUtil.getInstance().existsHandwrittenClass(hwPath, qClassName);
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
          if (hwScssPath.isPresent() && !existsTSHandwrittenFile(hwPath, gui.getComponentQName())) {
            scssPaths.add(hwScssPath.get());
          } else if (hwScssPath.isPresent()){
            scssPaths.add("./" + originalName + ".scss");
          }
        }
        else {
          importHelper.addImportString("import { Directive } from '@angular/core';");
          // TODO #3768
          Optional<String> hwHtmlPath = util.getRelativeImportPath(
              hwPath, outPath, qClassName, "html");
          if (hwHtmlPath.isPresent()) {
            htmlPath = hwHtmlPath.get();
          }
          Optional<String> hwScssPath = util.getRelativeImportPath(
              hwPath, outPath, qClassName, "scss");
          if (hwScssPath.isPresent() && !existsTSHandwrittenFile(hwPath, gui.getComponentQName())) {
            scssPaths.add(hwScssPath.get());
          } else if (hwScssPath.isPresent()){
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

  public boolean existsTSHandwrittenFile(MCPath targetPath, String qualifiedName) {
    String hwFile = Names.getPathFromPackage(qualifiedName)+ ".ts";
    Optional<URL> hwFilePath = targetPath.find(hwFile);
    return hwFilePath.isPresent();
  }

  protected HashSet<String> calculateImports(String componentQName, List<ASTGUIComponent> guiComponents, TSImportHelper importHelper) {
    HashSet<String> importedComponents = Sets.newHashSet();
    for (ASTGUIComponent component : guiComponents) {
      if (!component.isPresentNameSymbol()) {
        Log.error("0x0008B Name symbol of " + component.getName() + " not present.");
      }

      // Avoid circular import dependencies for recursive component calls
      if (!component.getNameSymbol().getFullName().equals(componentQName)) {
        String importedQName = component.getNameSymbol().getFullName();
        importHelper.addImport(importedQName, Optional.empty(), true, componentQName);
        importedComponents.add(importedQName);
      }
    }
    return importedComponents;
  }
}
