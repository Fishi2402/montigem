package umlp.generator.gui.gentypescript.java2ts;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.io.paths.MCPath;
import org.checkerframework.checker.nullness.qual.Nullable;
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
    for (ASTCDClass clazz : ast.getCDDefinition().getCDClassesList()) {
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
        boolean generateAnnotation = !GUIGeneratorUtil.getInstance().existsHandwrittenClass(hwPath, qName);
        String htmlFileName = originalName + ".html";
        String htmlPath = "./" + htmlFileName;
        List<String> scssPaths = new ArrayList<>();

        if (generateAnnotation) {
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
        else if (!UMLPGeneratorConfig.getInstance().isUseJsweet()){
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
        for (ASTGUIComponent component: viewComponents.getComponents()) {
          importedCompoments.add(component.getNameSymbol().getFullName());
        }
        glex.replaceTemplate(
            GUI2ComponentTemplates.ANNOTATIONS,
            clazz,
            new TemplateHookPoint(GUI2ComponentTemplates.ANNOTATION_STANDALONECONFIG_COMPONENT, htmlPath, scssPaths, selector, importedCompoments, hwPath, generateAnnotation)
        );
      }
    }
  }

}
