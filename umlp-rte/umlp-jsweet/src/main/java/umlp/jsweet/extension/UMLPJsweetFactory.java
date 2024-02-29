/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import standalone.com.sun.source.tree.CompilationUnitTree;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.Java2TypeScriptTranslator;
import org.jsweet.transpiler.TranspilationHandler;
import org.jsweet.transpiler.extension.PrinterAdapter;

public class UMLPJsweetFactory extends JSweetFactory {
  @Override
  public UMLPJsweetTranslator createTranslator(PrinterAdapter adapter, TranspilationHandler transpilationHandler,
      JSweetContext context, CompilationUnitTree compilationUnit, boolean fillSourceMap) {
    return new UMLPJsweetTranslator(adapter, transpilationHandler, context, compilationUnit, fillSourceMap);
  }
}
