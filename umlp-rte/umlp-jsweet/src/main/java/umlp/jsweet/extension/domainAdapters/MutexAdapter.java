/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.extension.AnnotationManager;
import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.util.GUIAdapterUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * This class extends the PrinterAdapter to add support for the
 * "Synchronized" annotation. Methods with this annotation are
 * wrapped using a Mutex to ensure synchronized execution, leveraging
 * the async-mutex library.
 */
public class MutexAdapter extends PrinterAdapter {

  public static final String MUTEX_ANNOTATION = "umlp.jsweet.extension.annotation.Synchronized";

  private GUIAdapterUtils adapterUtils;

  public MutexAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    addAnnotationManager(getMutexManager());
    this.adapterUtils = new GUIAdapterUtils(this, types(), context, util());
  }

  @Override
  public void beforeTypeBody(TypeElement type) {
    if (type.getEnclosedElements().stream()
        .anyMatch(m -> context.hasAnnotationType(m, MUTEX_ANNOTATION))) {
      println().printIndent().print("mutex = new Mutex();").println().println();
      this.adapterUtils.importFromWithModule("Mutex", "async-mutex");
    }
  }

  /**
   * Creates and returns an AnnotationManager for managing the
   * "Synchronized" annotation. The original method body is
   * wrapped in a synchronized block that uses the async-mutex library.
   *
   * @return An AnnotationManager for the "Synchronized" annotation.
   */
  private AnnotationManager getMutexManager() {
    return new AnnotationManager() {
      @Override
      public <T> T getAnnotationValue(Element element, String annotationType, String propertyName,
          Class<T> propertyClass, T defaultValue) {
        if (JSweetConfig.ANNOTATION_REPLACE.equals(annotationType) && isMutex(element)) {
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
            JSweetConfig.ANNOTATION_REPLACE.equals(annotationType) && isMutex(element) ?
                Action.ADD :
                Action.VOID;
      }

      private boolean isMutex(Element element) {
        return element.getKind().toString().equals("METHOD") &&
            context.hasAnnotationType(element, MUTEX_ANNOTATION);
      }
    };
  }
}
