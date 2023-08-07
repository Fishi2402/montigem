/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.backendAdapters;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.util.Trees;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.OverloadScanner;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.util.AbstractTreeScanner;
import org.jsweet.transpiler.util.Util;
import umlp.jsweet.extension.UMLPAdapterUtils;
import umlp.jsweet.extension.util.RTEPathUtil;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Arrays;
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

      if (isUMLPRTEAsync(method, invocation)) {
        context.addAwaitInvocation(invocation);
        addAsyncKeywordToMethod();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return super.visitMethodInvocation(invocation, trees);
  }

  private boolean isUMLPRTEAsync(ExecutableElement method, MethodInvocationTree invocation) {
    TypeMirror targetType = ((MethodInvocationElement) createExtendedElement(
        invocation)).getTargetType();
    String methodSimpleName = method.getSimpleName().toString();
    String targetClassName = method.getEnclosingElement().toString();

    //
    switch (targetClassName) {
      case "umlp.backendrte.command.CommandManager":
        switch (methodSimpleName) {
          case "executeCommand":
          case "_executeCommand":
            return true;
        }
        break;
      case "umlp.backendrte.common.GemList":
      case "umlp.backendrte.common.GemListBuilder":
        switch (methodSimpleName) {
          case "build":
            return true;
        }
        break;
      case "umlp.backendrte.common.Gem":
        switch (methodSimpleName) {
          case "get":
            return true;
        }
        break;
    }

    // Lists
    try {
      if (types().isSubtype(types().erasure(targetType),
          types().erasure(util().getType(java.util.List.class)))) {
        if (stringStartsWith(methodSimpleName, "add", "addAll", "contains", "containsAll",
            "get", "indexOf", "lastIndexOf", "remove", "removeAll", "set", "toArray")) {
          return true;
        }
      }
    } catch (Exception e) {
      return false;
    }

    return false;
  }

  @Override
  public Void visitEnhancedForLoop(EnhancedForLoopTree node, Trees trees) {
    TypeMirror expressionType = Util.getType(node.getExpression());
    if (types().isSubtype(expressionType, types().erasure(util().getType(List.class)))
        || expressionType.toString().equals(RTEPathUtil.getBackendRTECommonPath("GemList"))) {
      addAsyncKeywordToMethod();
    }
    return super.visitEnhancedForLoop(node, trees);
  }

  @Override
  public Void visitNewClass(NewClassTree node, Trees trees) {
//    ExtendedElement extendedElement = createExtendedElement(node);
//    if (UMLPAdapterUtils.isSubTypeOf(extendedElement.getType(), RTEPathUtil.getBackendRTECommonPath("GemListBuilder"), util(), types(), context)) {
//      addAsyncKeywordToMethod();
//    }
    return super.visitNewClass(node, trees);
  }

  private void addAsyncKeywordToMethod() {
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

  private boolean stringStartsWith(String str, String... others) {
    return Arrays.stream(others).anyMatch(str::startsWith);
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
