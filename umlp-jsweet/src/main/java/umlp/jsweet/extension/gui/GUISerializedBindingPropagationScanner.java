/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.gui;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.util.AbstractTreeScanner;
import org.jsweet.transpiler.util.Util;
import standalone.com.sun.source.tree.CompilationUnitTree;
import standalone.com.sun.source.tree.MethodTree;
import standalone.com.sun.source.util.Trees;
import umlp.jsweet.extension.util.GUIAdapterUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.List;

/**
 * Propagates the @Serialized annotation from a handwritten binding method to the
 * generated <method>ForAngular pendant in the TOP superclass, where the update
 * machinery is printed by {@link GUIComponentObserverPatternAdapter}.
 */
public class GUISerializedBindingPropagationScanner extends AbstractTreeScanner {

  protected GUISerializedBindingPropagationScanner(JSweetContext context) {
    super(null, context, null);
  }

  public void process(List<CompilationUnitTree> compilationUnits, Trees trees) {
    for (CompilationUnitTree compilationUnit : compilationUnits) {
      scan(compilationUnit, trees);
    }
  }

  @Override
  public Void visitMethod(MethodTree node, Trees trees) {
    try {
      ExecutableElement method = Util.getElement(node);
      if (method == null || method.getKind() == ElementKind.CONSTRUCTOR ||
          !context.hasAnnotationType(method, GUIAdapterUtils.SERIALIZED_ANNOTATION)) {
        return super.visitMethod(node, trees);
      }
      String bindingName = method.getSimpleName().toString() + "ForAngular";
      Element enclosing = method.getEnclosingElement();
      TypeMirror superclass = enclosing instanceof TypeElement ?
          ((TypeElement) enclosing).getSuperclass() : null;
      while (superclass != null && types().asElement(superclass) instanceof TypeElement) {
        TypeElement superType = (TypeElement) types().asElement(superclass);
        boolean found = false;
        for (ExecutableElement candidate : ElementFilter.methodsIn(superType.getEnclosedElements())) {
          if (candidate.getSimpleName().contentEquals(bindingName)) {
            context.addExtraAnnotationType(candidate, GUIAdapterUtils.SERIALIZED_ANNOTATION);
            found = true;
          }
        }
        if (found) {
          break;
        }
        superclass = superType.getSuperclass();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return super.visitMethod(node, trees);
  }
}
