package umlp.jsweet.extension.cocos;

import de.se_rwth.commons.logging.Log;
import jsweet.lang.Erased;
import jsweet.lang.Replace;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.TranspilationHandler;
import org.jsweet.transpiler.util.AbstractTreeScanner;
import standalone.com.sun.source.tree.ClassTree;
import standalone.com.sun.source.tree.CompilationUnitTree;
import standalone.com.sun.source.tree.MethodInvocationTree;
import standalone.com.sun.source.tree.MethodTree;
import standalone.com.sun.source.tree.VariableTree;
import standalone.com.sun.source.util.Trees;
import standalone.com.sun.tools.javac.code.Symbol;

import java.util.List;

/**
 * This class does not adapt anything but throws errors when finding Java Concepts that we cannot
 * transpile.
 */
public class JSweetCoCoChecker extends AbstractTreeScanner {

  public JSweetCoCoChecker(JSweetContext context) {
    super(null, context, null);
  }

  protected JSweetCoCoChecker(TranspilationHandler logHandler, JSweetContext context, CompilationUnitTree compilationUnit) {
    super(logHandler, context, compilationUnit);
  }

  protected boolean classIsBackendOnlyOrErased = false;
  protected boolean methodOrAttrIsBackendOnlyOrErased = false;

  @Override
  public Void visitClass(ClassTree classTree, Trees trees) {
    Symbol.ClassSymbol classSymbol = (Symbol.ClassSymbol) trees.getElement(
        trees.getPath(getCurrentPath().getCompilationUnit(), classTree));

    classIsBackendOnlyOrErased = classSymbol.getAnnotationMirrors().stream()
        .anyMatch(a -> a.getAnnotationType().toString()
            .equals("umlp.jsweet.extension.annotation.BackendOnly")
            || a.getAnnotationType().toString()
            .equals(Erased.class.getCanonicalName()));
    super.visitClass(classTree, trees);
    return null;
  }

  @Override
  public Void visitMethod(MethodTree methodTree, Trees trees) {
    Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) trees.getElement(
        trees.getPath(getCurrentPath().getCompilationUnit(), methodTree));

    methodOrAttrIsBackendOnlyOrErased = methodSymbol.getAnnotationMirrors().stream()
        .anyMatch(a -> a.getAnnotationType().toString()
            .equals("umlp.jsweet.extension.annotation.BackendOnly")
            || a.getAnnotationType().toString()
            .equals(Erased.class.getCanonicalName())
            || a.getAnnotationType().toString().equals(Replace.class.getCanonicalName()));
    super.visitMethod(methodTree, trees);
    return null;
  }

  @Override
  public Void visitVariable(VariableTree variableTree, Trees trees) {
    Symbol.VarSymbol varSymbol = (Symbol.VarSymbol) trees.getElement(
        trees.getPath(getCurrentPath().getCompilationUnit(), variableTree));
    if(varSymbol.enclClass() == null || !(varSymbol.owner instanceof Symbol.ClassSymbol)) {
      // Not a global field variable
      return null;
    }
    methodOrAttrIsBackendOnlyOrErased = varSymbol.getAnnotationMirrors().stream()
        .anyMatch(a -> a.getAnnotationType().toString()
            .equals("umlp.jsweet.extension.annotation.BackendOnly")
            || a.getAnnotationType().toString()
            .equals(Erased.class.getCanonicalName()));
    super.visitVariable(variableTree, trees);
    return null;
  }

  /**
   * Delegates method invocations to the unsupported APIs checker.
   * * @param invocation
   * @param trees
   * @return
   */
  @Override
  public Void visitMethodInvocation(MethodInvocationTree invocation, Trees trees) {
    if (!surroundingExcludedFromTranspilation()) {
      checkUnsupportedApis(invocation, trees);
    }
    super.visitMethodInvocation(invocation, trees);
    return null;
  }

  /**
   * Checks for usages of Java APIs, die laut User Guide nicht oder nur teilweise unterstützt werden,
   * und gibt entsprechende Warnungen aus.
   */
  protected void checkUnsupportedApis(MethodInvocationTree invocation, Trees trees) {
    Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) trees.getElement(
        trees.getPath(getCurrentPath().getCompilationUnit(), invocation.getMethodSelect()));

    if (methodSymbol == null) {
      return;
    }

    String methodName = methodSymbol.getSimpleName().toString();
    String ownerFqn = methodSymbol.owner != null
        ? methodSymbol.owner.getQualifiedName().toString()
        : "";

    // 1) Java 9+ Factory-Methoden wie List.of / Map.of / Set.of
    if (methodName.equals("of")
        && ("java.util.List".equals(ownerFqn)
        || "java.util.Map".equals(ownerFqn)
        || "java.util.Set".equals(ownerFqn))) {
      Log.warn("0xEE005: at "
          + getStartSourcePosition(invocation)
          + " " + ownerFqn + ".of(...) is not supported by our transpilation. "
          + "Use Collections.* helpers (e.g., Collections.emptyList/singletonList) "
          + "or Guava's Lists.newArrayList instead.");
      return;
    }

    // 2) Collections-Hilfsmethoden, die wir NICHT explizit unterstützen
    if ("java.util.Collections".equals(ownerFqn)) {
      switch (methodName) {
        case "emptySet":
        case "emptyMap":
        case "singleton":
        case "singletonMap":
        case "unmodifiableMap":
        case "unmodifiableSet":
        case "unmodifiableSortedSet":
        case "sort":
        case "reverse":
        case "shuffle":
        case "binarySearch":
        case "copy":
        case "fill":
        case "rotate":
        case "swap":
        case "min":
        case "max":
        case "checkedCollection":
        case "checkedList":
        case "checkedMap":
        case "checkedSet":
        case "checkedSortedSet":
        case "synchronizedCollection":
        case "synchronizedList":
        case "synchronizedMap":
        case "synchronizedSet":
        case "synchronizedSortedSet":
          Log.warn("0xEE006: at "
              + getStartSourcePosition(invocation)
              + " java.util.Collections." + methodName + "(...) is not supported by our transpilation. "
              + "Use simple list/map operations or custom helpers instead.");
          return;
        default:
          // emptyList/singletonList/unmodifiableList/unmodifiableCollection/nCopies sind explizit unterstützt
      }
    }

    // 3) Kritische Objects-Methoden, die wir nicht explizit unterstützen
    if ("java.util.Objects".equals(ownerFqn)) {
      switch (methodName) {
        case "hash":
        case "hashCode":
        case "deepEquals":
        case "compare":
        case "compareUnsigned":
        case "requireNonNullElse":
        case "requireNonNullElseGet":
          Log.warn("0xEE008: at "
              + getStartSourcePosition(invocation)
              + " java.util.Objects." + methodName + "(...) is not explicitly supported "
              + "and may not behave as expected in transpiled code. "
              + "Prefer requireNonNull/equals or implement comparison/hash logic explicitly.");
          return;
        default:
          // requireNonNull / equals sind explizit unterstützt
      }
    }

    // 4) Andere java.time-Typen als LocalDateTime/LocalDate/java.util.Date
    if (ownerFqn.startsWith("java.time.")
        && !ownerFqn.equals("java.time.LocalDateTime")
        && !ownerFqn.equals("java.time.LocalDate")) {
      Log.warn("0xEE009: at "
          + getStartSourcePosition(invocation)
          + " Calls to " + ownerFqn + "." + methodName + "(...) are not explicitly supported. "
          + "Only java.time.LocalDateTime, java.time.LocalDate and java.util.Date "
          + "with their standard methods are supported in transpiled code.");
      return;
    }

    // 5) Pakete, die grundsätzlich nicht für transpilierten Code unterstützt werden
    if (ownerFqn.startsWith("java.io.")
        || ownerFqn.startsWith("java.net.")
        || ownerFqn.startsWith("java.sql.")
        || ownerFqn.startsWith("java.security.")
        || ownerFqn.startsWith("java.lang.reflect.")) {
      Log.warn("0xEE003: at "
          + getStartSourcePosition(invocation)
          + " Calls to " + ownerFqn + "." + methodName + " are not supported in transpiled code. "
          + "Move this logic to backend-only code or mark it with @BackendOnly/@Erased.");
      return;
    }

    // 6) java.util.concurrent – nur sehr eingeschränkt erlaubt
    if (ownerFqn.startsWith("java.util.concurrent.")) {
      // Whitelist: AtomicReference (Typ ist bekannt, aber ohne echte Concurrency-Semantik)
      if (!ownerFqn.equals("java.util.concurrent.atomic.AtomicReference")) {
        Log.warn("0xEE004: at "
            + getStartSourcePosition(invocation)
            + " java.util.concurrent APIs are not supported in transpiled code. "
            + "Use async/await and @Synchronized instead of Java threads or executors.");
      }
    }
  }

  public String getStartSourcePosition(standalone.com.sun.source.tree.Tree tree) {
    long startSP = getContext().trees.getSourcePositions()
        .getStartPosition(getCurrentPath().getCompilationUnit(), tree);
    CompilationUnitTree cu = getCurrentPath().getCompilationUnit();
    String sourcePosition = "";
    if(cu != null &&
        cu.getLineMap() != null &&
        cu.getSourceFile() != null) {
      sourcePosition =
          "file://" +cu.getSourceFile().toUri().getPath()+":"+
              cu.getLineMap().getLineNumber(startSP) + ":"+
              cu.getLineMap().getColumnNumber(startSP);
    }
    return sourcePosition;
  }

  public boolean surroundingExcludedFromTranspilation() {
    return classIsBackendOnlyOrErased || methodOrAttrIsBackendOnlyOrErased;
  }

}
