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

  public void transform(ASTCDCompilationUnit ast, ASTGUICompilationUnit astGui, GUIComponentsVisitor viewComponents) {
    String qName = ast.getMCPackageDeclaration().getMCQualifiedName().getQName();
    for (ASTCDClass clazz : ast.getCDDefinition().getCDClassesList()) {
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
        boolean generateAnnotation = !GUIGeneratorUtil.getInstance().existsHandwrittenClass(hwPath, qName);
        String htmlFileName = originalName + ".html";
        String htmlPath = "./" + htmlFileName;
        List<String> scssPaths = new ArrayList<>();

        if (generateAnnotation) {
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
        else if (!UMLPGeneratorConfig.getInstance().isUseJsweet()){
          Optional<String> hwHtmlPath = util.getRelativeImportPath(
              hwPath, outPath, qClassName, "html");
          if (!hwHtmlPath.isPresent()) {
            htmlPath = util.getRelativeImportPathForAnnotation(
                outPath.toString(), new ArrayList<>(hwPath.getEntries()).get(0).toString(), qClassName).get();
          }
          Optional<String> hwScssPath = util.getRelativeImportPath(
              hwPath, outPath, qClassName, "scss");
          if (hwScssPath.isPresent()) {
            scssPaths.add("./" + originalName + ".scss");
          }
        }
        else {
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
        HashSet<String> importedComponents = calculateImports(astGui.getComponentQName(), viewComponents.getComponents());
        glex.replaceTemplate(
            GUI2ComponentTemplates.ANNOTATIONS,
            clazz,
            new TemplateHookPoint(GUI2ComponentTemplates.ANNOTATION_STANDALONECONFIG_COMPONENT, htmlPath, scssPaths, selector, importedComponents, hwPath, generateAnnotation)
        );
      }
    }
  }

  protected HashSet<String> calculateImports(String componentQName, List<ASTGUIComponent> guiComponents) {
    HashSet<String> importedComponents = Sets.newHashSet();
    for (ASTGUIComponent component: guiComponents) {
      if (!component.isPresentNameSymbol()) {
        Log.error("Name symbol of " + component.getName() + " not present.");
      }

      // Avoid circular import dependencies for recursive component calls
      if (component.getNameSymbol().getName().equals(componentQName)) {
        continue;
      }
      importedComponents.add(component.getNameSymbol().getFullName());
    }
    return importedComponents;
  }
}
