/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.gui;

import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.util.Util;
import standalone.com.sun.source.tree.AssignmentTree;
import umlp.jsweet.extension.util.GUIAdapterUtils;

import javax.lang.model.element.ExecutableElement;

/**
 * Adapting generated GUI components for angular
 */
public class AnnotationToDecoratorAdapter extends PrinterAdapter {

  public AnnotationToDecoratorAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  public boolean substituteExecutable(ExecutableElement executable) {
    if (context.hasAnnotationType(executable, JSweetConfig.ANNOTATION_ERASED)) {
      return true;
    }
    var anotationTree = trees().getTree(executable);
    var annotations = anotationTree.getModifiers().getAnnotations();
    if (!annotations.isEmpty()) {
      for (var annotation : annotations) {
        var annotationTypeElem = Util.getElement(annotation.getAnnotationType());
        if (context.hasAnnotationType(annotationTypeElem, GUIAdapterUtils.DECORATOR_NO_OBJ_ANNOTATION)) {
          print("@").print(annotationTypeElem.getSimpleName());
          print("(");
          if (!annotation.getArguments().isEmpty()) {
            // getElementValues() returns a LinkedHashMap -> ordering is correct
            for (var a : annotation.getArguments()) {
              System.err.println("a " + a.getClass());
              print(((AssignmentTree) a).getExpression());
              print(", ");
            }
            removeLastChars(2);
          }
          print(")");
          println().printIndent();
        }
      }

    }
    return super.substituteExecutable(executable);
  }
}
