/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.util.AbstractTreeScanner;
import org.jsweet.transpiler.util.Util;
import standalone.com.sun.source.tree.ClassTree;
import standalone.com.sun.source.tree.CompilationUnitTree;
import standalone.com.sun.source.util.Trees;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;

// TODO: Could probably be replaced by "JSweet-way" Annotation Manager?
public class TypeElementAnnotationScanner extends AbstractTreeScanner {
  protected boolean hasAnnotation = false;
  protected String annotation;

  public TypeElementAnnotationScanner(CompilationUnitTree compilationUnit, JSweetContext context) {
    super(null, context, compilationUnit);
  }

  public boolean find(String annotation) {
    this.annotation = annotation;
    scan(compilationUnit, getContext().trees);
    return hasAnnotation;
  }

  @Override
  public Void visitClass(ClassTree node, Trees trees) {
    TypeElement classElement = Util.getElement(node);
    if (getContext().hasAnnotationType(classElement, annotation)) {
      hasAnnotation = true;
    }
    return super.visitClass(node, trees);
  }
}
