/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.extension.AnnotationManager;
import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.util.GUIAdapterUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * This class represents an adapter that handles backend-only methods by substituting their body with specific logic.
 * It extends PrinterAdapter.
 */
public class SynchronizedAdapter extends PrinterAdapter {

  public static final String SYNCHRONIZED_ANNOTATION = "umlp.jsweet.extension.annotation.Synchronized";

  private GUIAdapterUtils adapterUtils;

  public SynchronizedAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    addAnnotationManager(getSynchronizedManager());
    this.adapterUtils = new GUIAdapterUtils(this, types(), context, util());
  }

  @Override
  public void beforeTypeBody(TypeElement type) {
    if (type.getEnclosedElements().stream()
        .anyMatch(m -> context.hasAnnotationType(m, SYNCHRONIZED_ANNOTATION))) {
      println().printIndent().print("mutex = new Mutex();").println().println();
      this.adapterUtils.importFromWithModule("Mutex", "async-mutex");
    }
  }

  private AnnotationManager getSynchronizedManager() {
    return new AnnotationManager() {
      @Override
      public <T> T getAnnotationValue(Element element, String annotationType, String propertyName,
          Class<T> propertyClass, T defaultValue) {
        if (JSweetConfig.ANNOTATION_REPLACE.equals(annotationType) && isSynchronized(element)) {
          return propertyClass.cast("return await this.mutex.runExclusive(async () => {\n"
              + "{{body}}\n"
              + "});");
        }
        else {
          return null;
        }
      }

      @Override
      public Action manageAnnotation(Element element, String annotationType) {
        return
            JSweetConfig.ANNOTATION_REPLACE.equals(annotationType) && isSynchronized(element) ?
                Action.ADD :
                Action.VOID;
      }

      private boolean isSynchronized(Element element) {
        return element.getKind().toString().equals("METHOD") &&
            context.hasAnnotationType(element, SYNCHRONIZED_ANNOTATION);
      }
    };
  }
}
