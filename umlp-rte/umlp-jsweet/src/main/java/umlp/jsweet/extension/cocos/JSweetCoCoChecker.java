package umlp.jsweet.extension.cocos;

import de.se_rwth.commons.logging.Log;
import jsweet.lang.Erased;
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
            .equals(Erased.class.getCanonicalName()));
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
   * Throw error when finding .stream() usage.
   * @param invocation
   * @param trees
   * @return
   */
  @Override
  public Void visitMethodInvocation(MethodInvocationTree invocation, Trees trees) {
    if(!surroundingExcludedFromTranspilation() && invocation.toString().contains("stream()")) {
      Log.warn("0xEE0001: at "
          +getStartSourcePosition(invocation) +
          " Java streams are currently not supported by our transpilation. " +
          "Try using a forEach instead or use the BackendOnly or Erased annotation.");
    }
    super.visitMethodInvocation(invocation, trees);
    return null;
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
