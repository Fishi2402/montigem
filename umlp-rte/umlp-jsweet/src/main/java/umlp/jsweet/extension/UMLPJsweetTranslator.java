/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import standalone.com.sun.source.tree.CompilationUnitTree;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.Java2TypeScriptTranslator;
import org.jsweet.transpiler.TranspilationHandler;
import org.jsweet.transpiler.extension.PrinterAdapter;

public class UMLPJsweetTranslator extends Java2TypeScriptTranslator {
  /**
   * Creates a new TypeScript translator.
   *
   * @param adapter         an object that can tune various aspects of the
   *                        TypeScript code generation
   * @param logHandler      the handler for logging and error reporting
   * @param context         the AST scanning context
   * @param compilationUnit the compilation unit to be translated
   * @param fillSourceMap   if true, the printer generates the source maps, for
   *                        debugging purpose
   */
  public UMLPJsweetTranslator(PrinterAdapter adapter,
      TranspilationHandler logHandler,
      JSweetContext context,
      CompilationUnitTree compilationUnit, boolean fillSourceMap) {
    super(adapter, logHandler, context, compilationUnit, fillSourceMap);
  }

  public ClassScope revealScope() {
    return getScope();
  }

}
