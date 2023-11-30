package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import umlp.generator.gui.GUIGeneratorUtil;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GUIComponentAnnotationDecorator {

  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

  protected final GlobalExtensionManagement glex;
  protected final MCPath hwPath;
  protected final Path outPath;

  public GUIComponentAnnotationDecorator(
      final GlobalExtensionManagement glex, MCPath hwPath, Path outPath) {
    this.glex = glex;
    this.hwPath = hwPath;
    this.outPath = outPath;
  }

  public void transform(ASTCDCompilationUnit ast) {
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
        String htmlFileName = originalName + ".html";
        String htmlPath = "./" + htmlFileName;

        Optional<String> hwHtmlPath = util.getRelativeImportPath(
            hwPath, outPath, qName, "html");
        if (hwHtmlPath.isPresent()) {
          htmlPath = hwHtmlPath.get();
        }

        List<String> scssPaths = new ArrayList<>();

        Optional<String> hwScssPath = util.getRelativeImportPath(
            hwPath, outPath, qName, "scss");
        if (hwScssPath.isPresent()) {
          scssPaths.add(hwScssPath.get());
        }

        String selector = GUIGeneratorUtil.getInstance().getComponentNameFromCamelCaseName(originalName);
        if (!GUIGeneratorUtil.getInstance().existsHandwrittenClass(hwPath, qName)) {
          glex.replaceTemplate(
            GUI2ComponentTemplates.ANNOTATIONS,
            clazz,
            new TemplateHookPoint(GUI2ComponentTemplates.ANNOTATION_COMPONENT, htmlPath, scssPaths, selector)
          );
        } else {
          glex.replaceTemplate(
            GUI2ComponentTemplates.ANNOTATIONS,
            clazz,
            new TemplateHookPoint(GUI2ComponentTemplates.ANNOTATION_DIRECTIVE)
          );
        }
      }
    }
  }

}
