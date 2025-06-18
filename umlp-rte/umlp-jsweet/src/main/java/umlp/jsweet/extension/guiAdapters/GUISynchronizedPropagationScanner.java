/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.guiAdapters;

import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.TranspilationHandler;
import org.jsweet.transpiler.util.AbstractTreeScanner;
import org.jsweet.transpiler.util.Util;
import standalone.com.sun.source.tree.CompilationUnitTree;
import standalone.com.sun.source.tree.MethodTree;
import standalone.com.sun.source.util.Trees;
import umlp.jsweet.extension.util.GUIAdapterUtils;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import java.util.ArrayList;
import java.util.List;

public class GUISynchronizedPropagationScanner extends AbstractTreeScanner {

  private final GUIAdapterUtils adapterUtils;

  private boolean stillWorking = false;

  protected GUISynchronizedPropagationScanner(JSweetContext context) {
    super(null, context, null);
    this.adapterUtils = new GUIAdapterUtils(types(), context, util());
  }

  public void process(List<CompilationUnitTree> compilationUnits, Trees trees) {
    do {
      stillWorking = false;
      for (CompilationUnitTree compilationUnit : compilationUnits) {
        scan(compilationUnit, trees);
      }
    } while (stillWorking);
  }

  @Override
  public Void visitMethod(MethodTree node, Trees trees) {
    try {
      ExecutableElement method = Util.getElement(node);
      if (method == null) {
        return null;
      }

      if (method.getKind() == ElementKind.CONSTRUCTOR) {
        return null;
      }

      if(adapterUtils.isSynchronized(method)) {
        List<ExecutableElement> candidates = new ArrayList<>();
        util().collectMatchingMethodDeclarationsInType(
            (TypeElement) method.getEnclosingElement(), node.getName().toString(),
            (ExecutableType) method.asType(), true, candidates);
        for (ExecutableElement candidate : candidates) {
          if (!adapterUtils.isSynchronized(candidate)) {
            context.addExtraAnnotationType(candidate, GUIAdapterUtils.SYNCRHONIZED_ANNOTATION);
            stillWorking = true;
          }
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return super.visitMethod(node, trees);
  }
}
