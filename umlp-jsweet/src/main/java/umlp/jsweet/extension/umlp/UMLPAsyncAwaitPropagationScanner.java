/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.umlp;

import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.OverloadScanner;
import org.jsweet.transpiler.util.AbstractTreeScanner;
import org.jsweet.transpiler.util.Util;
import standalone.com.sun.source.tree.ClassTree;
import standalone.com.sun.source.tree.CompilationUnitTree;
import standalone.com.sun.source.tree.EnhancedForLoopTree;
import standalone.com.sun.source.tree.ExpressionTree;
import standalone.com.sun.source.tree.LambdaExpressionTree;
import standalone.com.sun.source.tree.MemberSelectTree;
import standalone.com.sun.source.tree.MethodInvocationTree;
import standalone.com.sun.source.tree.MethodTree;
import standalone.com.sun.source.tree.Tree;
import standalone.com.sun.source.util.TreePath;
import standalone.com.sun.source.util.Trees;
import umlp.jsweet.extension.util.GUIAdapterUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class adds the async keyword to every method that targets
 * an asynchronous RTE method.
 */
public class UMLPAsyncAwaitPropagationScanner extends AbstractTreeScanner {

  private boolean stillWorking = false;

  private final GUIAdapterUtils adapterUtils;

  /**
   * Creates a new global scanner.
   */
  public UMLPAsyncAwaitPropagationScanner(JSweetContext context) {
    super(null, context, null);
    this.adapterUtils = new GUIAdapterUtils(types(), context, util());
  }

  @Override
  public Void visitClass(ClassTree node, Trees trees) {
    // Do not inspect any @Erased classes (and possible mark interfaces as async)
    if (context.hasAnnotationType(Util.getElement(node), JSweetConfig.ANNOTATION_ERASED)) {
      return null;
    }
    return super.visitClass(node, trees);
  }

  @Override
  public Void visitMethodInvocation(MethodInvocationTree invocation, Trees trees) {
    try {
      ExecutableElement method = Util.getElement(invocation.getMethodSelect());
      if (method == null) {
        return null;
      }

      if (method.getKind() == ElementKind.CONSTRUCTOR) {
        return null;
      }

      boolean isAsyncFromSuper = false;
      // Methods overriding async methods from the RTE must be async again
      ExpressionTree select = invocation.getMethodSelect();
      Element receiverType = null;
      if (select instanceof MemberSelectTree mst) {
        TypeMirror typeMirror =
            trees.getTypeMirror(new TreePath(getCurrentPath(), mst.getExpression()));

        if (typeMirror instanceof DeclaredType declaredType) {
          receiverType = declaredType.asElement();
        }
      } else {
        // implicit receiver (this)
        receiverType = method.getEnclosingElement();
      }
      if (receiverType instanceof TypeElement) {
        TypeElement parentElement = (TypeElement) receiverType;
        ExecutableElement methodElement = Util.getElement(invocation.getMethodSelect());
        List<ExecutableElement> candidates = new ArrayList<>();
        util().findMethodDeclarationsInType(
            parentElement,
            Collections.singleton(methodElement.getSimpleName().toString()),
            Collections.emptySet(),
            candidates
        );
        findMethodsInInterfaces(parentElement, methodElement.getSimpleName().toString(), candidates);
        for (ExecutableElement candidate : candidates) {
          if (context.hasAnnotationType(candidate, JSweetConfig.ANNOTATION_ASYNC)) {
            isAsyncFromSuper = true;
            break;
          }
        }

        // TODO After refactoring the Async Detection this is needed to print .stream() on Lists as Async
        // in the Future when Sets are also async it will not be necessary anymore
        if(types().isSubtype(types().erasure(receiverType.asType()), types().erasure(util().getType(List.class)))) {
          if(methodElement.getSimpleName().toString().startsWith("stream")) {
            ExecutableElement setMethod = ((DeclaredType)util().getType(List.class)).asElement().getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .map(ExecutableElement.class::cast)
                .filter(m -> m.getSimpleName().contentEquals("set"))
                .filter(m -> m.getParameters().size() == 2)
                .findFirst()
                .orElseThrow();
            if(context.hasAnnotationType(setMethod, JSweetConfig.ANNOTATION_ASYNC)) {
              isAsyncFromSuper = true;
            }
          }
        }
      }

      boolean isAsync = false;

      if (isAsyncFromSuper || context.hasAnnotationType(method, JSweetConfig.ANNOTATION_ASYNC)) {
        context.addAwaitInvocation(invocation);
        addAsyncKeywordToMethod();
        isAsync = true;
      }

      else if (adapterUtils.isHWCComponentOrDirective(Util.getElement(getParent(ClassTree.class))) &&
          isInvocatedGemListAccessor(invocation, trees)) {
        context.addAwaitInvocation(invocation);
        addAsyncKeywordToMethod();
        isAsync = true;
      }

      if (isAsync) {
        LambdaExpressionTree lambdaParent = getParent(LambdaExpressionTree.class);
        if (lambdaParent != null) {
          context.addAsyncLambda(lambdaParent);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return super.visitMethodInvocation(invocation, trees);
  }


  private boolean isInvocatedGemListAccessor(MethodInvocationTree invocation, Trees trees) {
    Element invocatedType = types().asElement(Util.getType(invocation));
    if (invocatedType != null && invocatedType instanceof TypeElement &&
        adapterUtils.isSubTypeOf((TypeElement) invocatedType, "java.util.List")) {
      return true;
    }
    return false;
  }

  // For UMLP all List iterations are asynchronous, making methods using for each loops also asynchronous
  @Override
  public Void visitEnhancedForLoop(EnhancedForLoopTree node, Trees trees) {
    TypeMirror expressionType = Util.getType(node.getExpression());
    if (types().isSubtype(types().erasure(expressionType), types().erasure(util().getType(List.class)))) {
      addAsyncKeywordToMethod();
      addAsyncKeyWordToLambda();
    }
    return super.visitEnhancedForLoop(node, trees);
  }

  @Override
  public Void visitMethod(MethodTree node, Trees trees) {
    Element elementOfMethod = Util.getElement(node);
    // Constructors CAN NOT be async
    if (elementOfMethod.getKind() == ElementKind.CONSTRUCTOR) {
      return null;
    }

    // Do not check erased methods further -> this leads to very weird js code
    if (context.hasAnnotationType(elementOfMethod, JSweetConfig.ANNOTATION_ERASED)) {
      return null;
    }

    Element elementOfParent = Util.getElement(getParent());
    if (context.hasAnnotationType(elementOfMethod)) {
      return super.visitMethod(node, trees);
    }

    // Methods overriding async methods must be async again
    if (elementOfParent instanceof TypeElement) {
      TypeElement parentElement = (TypeElement) elementOfParent;
      List<ExecutableElement> candidates = new ArrayList<>();
      util().findMethodDeclarationsInType(
          parentElement,
          Collections.singleton(node.getName().toString()),
          Collections.emptySet(),
          candidates
      );
      findMethodsInInterfaces(parentElement, node.getName().toString(), candidates);
      for (ExecutableElement candidate : candidates) {
        if (context.hasAnnotationType(candidate, JSweetConfig.ANNOTATION_ASYNC)) {
          addAsyncKeywordToMethod(node);
          break;
        }
      }
    }

    // Also methods which are void and overriding any async methods must be async again
    if (elementOfMethod instanceof ExecutableElement && elementOfParent instanceof TypeElement) {
      TypeElement parentElement = (TypeElement) elementOfParent;
      ExecutableElement methodElement = (ExecutableElement) elementOfMethod;

      if ("void".equals(methodElement.getReturnType().toString())) {
        for (ExecutableElement superMethodElement : adapterUtils.listMatchingSuperMethods(
            parentElement, methodElement)) {
          if (context.hasAnnotationType(superMethodElement, JSweetConfig.ANNOTATION_ASYNC)) {
            addAsyncKeywordToMethod(node);
          }
        }
      }
    }
    return super.visitMethod(node, trees);
  }

  private void addAsyncKeywordToMethod() {
    MethodTree methodTree = getParent(MethodTree.class);
    addAsyncKeywordToMethod(methodTree);
  }

  private void addAsyncKeyWordToLambda() {
    LambdaExpressionTree lambdaParent = getParent(LambdaExpressionTree.class);
    if (lambdaParent != null) {
      context.addAsyncLambda(lambdaParent);
    }
  }

  // Adds the asynchronous keyword to the method, overloading methods, and other implementing classes
  // Copied from JSweet Core
  private void addAsyncKeywordToMethod(MethodTree methodTree) {
    if (methodTree != null) {
      ExecutableElement methodElement = Util.getElement(methodTree);
      if (!context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_ASYNC)) {
        context.addExtraAnnotationType(methodElement, JSweetConfig.ANNOTATION_ASYNC);

        if (context.isInvalidOverload(methodElement)) {
          OverloadScanner.Overload overload = context.getOverload(
              (TypeElement) methodElement.getEnclosingElement(), methodElement);

          for (OverloadScanner.OverloadMethodEntry overloadEntry : overload.getEntries()) {
            context.addExtraAnnotationType(overloadEntry.methodElement,
                JSweetConfig.ANNOTATION_ASYNC);
          }
        }
        List<ExecutableElement> candidates = new ArrayList<>();
        util().collectMatchingMethodDeclarationsInType(
            (TypeElement) methodElement.getEnclosingElement(), methodTree.getName().toString(),
            (ExecutableType) methodElement.asType(), true, candidates);
        for (ExecutableElement candidate : candidates) {
          context.addExtraAnnotationType(candidate, JSweetConfig.ANNOTATION_ASYNC);
        }
        stillWorking = true;
      }
    }
  }

  private void findMethodsInInterfaces(
      TypeElement typeElement,
      String methodName,
      List<ExecutableElement> result) {

    findMethodsInInterfaces(typeElement, methodName, result, new HashSet<>());
  }


  private void findMethodsInInterfaces(
      TypeElement typeElement,
      String methodName,
      List<ExecutableElement> result,
      Set<TypeElement> visited) {

    if (!visited.add(typeElement)) {
      return;
    } // Direct interfaces
    for (TypeMirror interfaceMirror : typeElement.getInterfaces()) {
      Element interfaceElement = types().asElement(interfaceMirror);
      if (interfaceElement instanceof TypeElement) {
        TypeElement interfaceTypeElement = (TypeElement) interfaceElement;
        for (Element element : interfaceTypeElement.getEnclosedElements()) {
          if (element instanceof ExecutableElement) {
            ExecutableElement execElement = (ExecutableElement) element;
            if (execElement.getSimpleName().toString().equals(methodName)) {
              result.add(execElement);
            }
          }
        }
        findMethodsInInterfaces(interfaceTypeElement, methodName, result);
      }
    }

    // Superclass chain
    TypeMirror superclass = typeElement.getSuperclass();
    if (superclass.getKind() != TypeKind.NONE) {
      Element superElement = types().asElement(superclass);

      if (superElement instanceof TypeElement superTypeElement) {
        findMethodsInInterfaces(
            superTypeElement,
            methodName,
            result,
            visited);
      }
    }
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
