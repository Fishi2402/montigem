/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.gui;

import org.jsweet.transpiler.JSweetContext;
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

/** Dieser PropagationScanner scheint genau das Verhalten zu haben was bereits in
{@link umlp.jsweet.extension.util.GUIAdapterUtils#elementOrRelatedHasAnnotation}
 codiert ist? TODO @MH
* */
public class GUIObserverRegistrationParamsPropagationScanner extends AbstractTreeScanner {

  private final GUIAdapterUtils adapterUtils;

  private boolean stillWorking = false;

  protected GUIObserverRegistrationParamsPropagationScanner(JSweetContext context) {
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

      if (adapterUtils.isObserverRegistrationParamsAnnotated(method)) {
        List<ExecutableElement> candidates = new ArrayList<>();
        util().collectMatchingMethodDeclarationsInType(
            (TypeElement) method.getEnclosingElement(), node.getName().toString(),
            (ExecutableType) method.asType(), true, candidates);
        for (ExecutableElement candidate : candidates) {
          if (!adapterUtils.isObserverRegistrationParamsAnnotated(method)) {
            context.addExtraAnnotationType(candidate, GUIAdapterUtils.NEEDS_OBSERVER_REGISTRATION_PARAMS_ANNOTATION);
            stillWorking = true;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return super.visitMethod(node, trees);
  }
}
