/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;
import def.js.Symbol;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.OverloadScanner;
import org.jsweet.transpiler.util.AbstractTreeScanner;
import org.jsweet.transpiler.util.Util;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import java.util.ArrayList;
import java.util.List;

public class UMLPAsyncAwaitPropagationScanner extends AbstractTreeScanner {

  private boolean stillWorking = false;

  /**
   * Creates a new global scanner.
   */
  public UMLPAsyncAwaitPropagationScanner(JSweetContext context) {
    super(null, context, null);
  }

  @Override
  public Void visitMethodInvocation(MethodInvocationTree invocation, Trees trees) {
    try {
      ExecutableElement method = Util.getElement(invocation.getMethodSelect());
      if (method == null) {
        return null;
      }

      if (isUMLPRTEAsync(method)) {
        context.addAwaitInvocation(invocation);
        MethodTree parent = getParent(MethodTree.class);
        if (parent != null) {
          ExecutableElement parentMethodElement = Util.getElement(parent);
          if (!context.hasAnnotationType(parentMethodElement, JSweetConfig.ANNOTATION_ASYNC)) {
            context.addExtraAnnotationType(parentMethodElement, JSweetConfig.ANNOTATION_ASYNC);

            if (context.isInvalidOverload(parentMethodElement)) {
              OverloadScanner.Overload overload = context.getOverload(
                  (TypeElement) parentMethodElement.getEnclosingElement(), parentMethodElement);

              for (OverloadScanner.OverloadMethodEntry overloadEntry : overload.getEntries()) {
                context.addExtraAnnotationType(overloadEntry.methodElement,
                    JSweetConfig.ANNOTATION_ASYNC);
              }
            }
            List<ExecutableElement> candidates = new ArrayList<>();
            util().collectMatchingMethodDeclarationsInType(
                (TypeElement) parentMethodElement.getEnclosingElement(), parent.getName().toString(),
                (ExecutableType) parentMethodElement.asType(), true, candidates);
            for (ExecutableElement candidate : candidates) {
              context.addExtraAnnotationType(candidate, JSweetConfig.ANNOTATION_ASYNC);
            }
            stillWorking = true;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return super.visitMethodInvocation(invocation, trees);
  }

  private boolean isUMLPRTEAsync(ExecutableElement method) {
    Element owner = method.getEnclosingElement();
    String ownerName = owner.toString();
    if (ownerName.startsWith(RTEPathUtil.getBackendRTEPath())) {
      if (ownerName.equals(RTEPathUtil.getBackendRTECommandPath("CommandManager"))) {
        if (method.getSimpleName().toString().equals("executeCommand")) {
          return true;
        }
      }
    }

    return false;
  }

  public void process(List<CompilationUnitTree> compilationUnits, Trees trees) {
    do {
      stillWorking = false;
      for (CompilationUnitTree compilationUnit : compilationUnits) {
        scan(compilationUnit, trees);
      }
    } while (stillWorking);
  }

}
