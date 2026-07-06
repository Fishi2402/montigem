/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.common;

import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.OverloadScanner;
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

// TODO @MH in unseren JSweet Fork umziehen
public class AdditionalAsyncAwaitPropagationScanner extends AbstractTreeScanner {

  private final GUIAdapterUtils adapterUtils;

  private int iteration;

  public AdditionalAsyncAwaitPropagationScanner(JSweetContext context) {
    super(null, context, null);
    this.adapterUtils = new GUIAdapterUtils(types(), context, util());
    this.iteration = 1;
  }

  @Override
  public Void visitMethod(MethodTree methodTree, Trees trees) {
    ExecutableElement methodElement = Util.getElement(methodTree);
    String methodName = methodTree.getName().toString();


    if (methodElement.getKind() == ElementKind.CONSTRUCTOR) {
      return super.visitMethod(methodTree, trees);
    }

    if (iteration == 1) {
      if (context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_ASYNC)) {
        addAsyncKeywordToMethodAndOverloads(methodTree, methodElement);

        TypeElement enclosingElement = (TypeElement) methodElement.getEnclosingElement();
        while (enclosingElement != null && enclosingElement.getSuperclass() != null && !enclosingElement.getQualifiedName().toString().equals("java.lang.Object")) {
          List<ExecutableElement> candidates = new ArrayList<>();
          util().collectMatchingMethodDeclarationsInType(enclosingElement, methodName,
              (ExecutableType) methodElement.asType(), true, candidates);
          for (ExecutableElement candidate : candidates) {
            if (candidate.getKind() != ElementKind.CONSTRUCTOR) {
              addAsyncKeywordToMethodAndOverloads(trees.getTree(candidate), candidate);
            }
          }
          types().asElement(enclosingElement.getSuperclass());
          enclosingElement = (TypeElement) types().asElement(enclosingElement.getSuperclass());
        }
      }
    }

    else if (iteration == 2) {
      if (!context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_ASYNC)) {
        boolean hasAsyncAnnotationFromSuper = false;
        TypeElement enclosingElement = (TypeElement) methodElement.getEnclosingElement();
        while (enclosingElement != null && enclosingElement.getSuperclass() != null && !enclosingElement.getQualifiedName().toString().equals("java.lang.Object")) {
          List<ExecutableElement> candidates = new ArrayList<>();
          util().collectMatchingMethodDeclarationsInType(enclosingElement, methodName,
              (ExecutableType) methodElement.asType(), true, candidates);
          for (ExecutableElement candidate : candidates) {
            if (context.hasAnnotationType(candidate, JSweetConfig.ANNOTATION_ASYNC)) {
              hasAsyncAnnotationFromSuper = true;
              break;
            }
          }
          if (hasAsyncAnnotationFromSuper) {
            break;
          }
          types().asElement(enclosingElement.getSuperclass());
          enclosingElement = (TypeElement) types().asElement(enclosingElement.getSuperclass());
        }

        if (hasAsyncAnnotationFromSuper) {
          addAsyncKeywordToMethodAndOverloads(methodTree, methodElement);
        }
      }
    }

    return super.visitMethod(methodTree, trees);
  }

  private void addAsyncKeywordToMethodAndOverloads(MethodTree methodTree,
      ExecutableElement methodElement) {

    addAsyncKeywordToMethod(methodElement);

    if (context.isInvalidOverload(methodElement)) {
      OverloadScanner.Overload overload = context.getOverload(
          (TypeElement) methodElement.getEnclosingElement(), methodElement);
      for (OverloadScanner.OverloadMethodEntry overloadEntry : overload.getEntries()) {
        addAsyncKeywordToMethod(overloadEntry.methodElement);
      }
    }
  }

  private void addAsyncKeywordToMethod(ExecutableElement methodElement) {
    if (methodElement.getKind() == ElementKind.CONSTRUCTOR) {
      return;
    }
    if (!context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_ASYNC)) {
      context.addExtraAnnotationType(methodElement, JSweetConfig.ANNOTATION_ASYNC);
    }
  }

  public void process(List<CompilationUnitTree> compilationUnits, Trees trees) {
    this.iteration = 1;
    for (CompilationUnitTree compilationUnit : compilationUnits) {
      scan(compilationUnit, trees);
    }
    this.iteration = 2;
    for (CompilationUnitTree compilationUnit : compilationUnits) {
      scan(compilationUnit, trees);
    }
  }
}
