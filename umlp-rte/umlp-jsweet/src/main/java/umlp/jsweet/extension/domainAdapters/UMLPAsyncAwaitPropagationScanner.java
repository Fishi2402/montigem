/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import standalone.com.sun.source.tree.ClassTree;
import standalone.com.sun.source.tree.CompilationUnitTree;
import standalone.com.sun.source.tree.EnhancedForLoopTree;
import standalone.com.sun.source.tree.MethodInvocationTree;
import standalone.com.sun.source.tree.MethodTree;
import standalone.com.sun.source.tree.Tree;
import standalone.com.sun.source.util.Trees;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.OverloadScanner;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.util.AbstractTreeScanner;
import org.jsweet.transpiler.util.Util;
import umlp.jsweet.extension.util.UMLPAdapterUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class adds the async keyword to every method that targets
 *  an asynchronous RTE method.
 */
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

  @Override
  public Void visitClass(ClassTree node, Trees trees) {
    // Do not inspect any @Erased classes (and possible mark interfaces as async)
    if (context.hasAnnotationType(Util.getElement(node), JSweetConfig.ANNOTATION_ERASED )){
      return null;
    }
    return super.visitClass(node, trees);
  }


  // Returns true for asynchronous method calls
  private boolean isUMLPRTEAsync(ExecutableElement method, MethodInvocationTree invocation) {
    TypeMirror targetType = ((MethodInvocationElement) createExtendedElement(
        invocation)).getTargetType();
    String methodSimpleName = method.getSimpleName().toString();
    String targetClassName = method.getEnclosingElement().toString();

    if (isRTEAsyncMethod(targetClassName, methodSimpleName))
      return true;

    // Lists
    try {
      if (targetType != null && types().isSubtype(types().erasure(targetType),
          types().erasure(util().getType(java.util.List.class)))) {
        if (stringStartsWith(methodSimpleName,
            "add",
            "addAll",
            "contains",
            "containsAll",
            "get",
            "indexOf",
            "lastIndexOf",
            "map",
            "remove",
            "removeElement",
            "removeIndex",
            "removeAll",
            "set",
            "toArray")) {
          return true;
        }
      }
    } catch (Exception e) {
      return false;
    }

    return false;
  }

  private static boolean isRTEAsyncMethod(String targetClassName, String methodSimpleName) {
    switch (targetClassName) {
      case "umlp.backendrte.command.CommandManager":
      case "umlp.backendrte.command.ClientCommandCommunication":
        switch (methodSimpleName) {
          case "executeCommand":
          case "_executeCommand":
          case "register":
          case "_register":
          case "deregister":
          case "_deregister":
          case "sendCommandToSubscribers":
          case "_sendCommandToSubscribers":
            return true;
        }
        break;
      case "umlp.backendrte.command.ClientCommandWebsocketCommunication":
        switch (methodSimpleName) {
          case "sendCommandToServerAndReceiveResult":
          case "handleTextMessage":
            return true;
        }
      case "umlp.backendrte.common.GemList":
      case "umlp.backendrte.common.GemListBuilder":
        switch (methodSimpleName) {
          case "build":
          case "loadIfRequired":
          case "_internal_add":
            return true;
        }
        break;
      case "umlp.backendrte.common.Gem":
        switch (methodSimpleName) {
          case "get":
            return true;
        }
        break;
      case "umlp.backendrte.command.Command":
      case "umlp.backendrte.command.SyncError":
      case "umlp.backendrte.command.ServerError":
        switch (methodSimpleName) {
          case "doRun":
          case "doAction":
          case "checkAccessPolicy":
          case "checkContract":
            return true;
        }
        break;
      case "umlp.backendrte.command.CommandResult":
      case "umlp.backendrte.command.SimpleResult":
      case "umlp.backendrte.command.ListResult":
        switch (methodSimpleName) {
          case "isSuccessful":
            return true;
        }
      case "umlp.backendrte.command.StatusResult":
        switch (methodSimpleName) {
          case "wasSuccessful":
          case "isSuccessful":
            return true;
        }
      case "umlp.backendrte.common.AggregatedCommandDeSer":
      case "umlp.backendrte.common.BooleanDeSer":
      case "umlp.backendrte.common.ByteDeSer":
      case "umlp.backendrte.common.CharacterDeSer":
      case "umlp.backendrte.common.DateDeSer":
      case "umlp.backendrte.common.DoubleDeSer":
      case "umlp.backendrte.common.FloatDeSer":
      case "umlp.backendrte.common.IntegerDeSer":
      case "umlp.backendrte.common.LongDeSer":
      case "umlp.backendrte.common.SerializableDeSer":
      case "umlp.backendrte.common.ShortDeSer":
      case "umlp.backendrte.common.StringDeSer":
      case "umlp.backendrte.command.CommandDeSer":
      case "umlp.backendrte.command.CommandObjectMapper":
      case "umlp.backendrte.command.JsonDeSer":
      case "umlp.backendrte.command.DeSer":
        switch (methodSimpleName) {
          case "fromJson":
          case "fromJsonString":
          case "fromJsonObject":
          case "serialize":
          case "deserialize":
          case "_serialize":
          case "_deserialize":
          case "serializeCommand":
          case "deserializeCommand":
          case "_serializeCommand":
          case "_deserializeCommand":
          case "serializeResponse":
          case "deserializeResponse":
          case "_serializeResponse":
          case "_deserializeResponse":
            return true;
        }
        break;
      case "umlp.backendrte.command.Participant":
        switch (methodSimpleName) {
          case "subscribe":
            return true;
        }
      case "umlp.backendrte.common.IDOrBuilder":
        switch (methodSimpleName) {
          case "getIdAndMaybeBuild":
            return true;
        }
      case "umlp.backendrte.common.SimpleIdStorage":
        switch (methodSimpleName) {
          case "addAll":
          case "remove":
            return true;
        }
    }
    return false;
  }

  // For UMLP all List iterations are asynchronous, making methods using for each loops also asynchronous
  @Override
  public Void visitEnhancedForLoop(EnhancedForLoopTree node, Trees trees) {
    TypeMirror expressionType = Util.getType(node.getExpression());
    if (types().isSubtype(types().erasure(expressionType), types().erasure(util().getType(List.class)))) {
      addAsyncKeywordToMethod();
    }
    return super.visitEnhancedForLoop(node, trees);
  }

  // Methods overriding async methods from the RTE must be async again
  @Override
  public Void visitMethod(MethodTree node, Trees trees) {
    Element element = Util.getElement(getParent());
    if (element instanceof TypeElement) {
      TypeElement parentElement = (TypeElement) element;
      for (String superType : UMLPAdapterUtils.listSuperTypes(parentElement, util(), types())) {
        if (isRTEAsyncMethod(superType, node.getName().toString())) {
          addAsyncKeywordToMethod(node);
          break;
        }
      }
    }
    return super.visitMethod(node, trees);
  }

  private void addAsyncKeywordToMethod() {
    MethodTree methodTree = getParent(MethodTree.class);
    addAsyncKeywordToMethod(methodTree);
  }

  // Adds the asynchronous keyword to the method, overloading methods, and other implementing classes
  // Copied from JSweet Core
  private void addAsyncKeywordToMethod(MethodTree methodTree) {
    if (methodTree != null) {
      ExecutableElement parentMethodElement = Util.getElement(methodTree);
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
            (TypeElement) parentMethodElement.getEnclosingElement(), methodTree.getName().toString(),
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
