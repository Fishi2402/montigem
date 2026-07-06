/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.common;

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

  private final GUIAdapterUtils adapterUtils;

  public MutexAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    addAnnotationManager(getMutexManager());
    this.adapterUtils = new GUIAdapterUtils(this, types(), context, util());
  }

  @Override
  public void beforeTypeBody(TypeElement type) {
    boolean hasMutex = false;
    boolean hasKeyedMutex = false;

    for (Element m : type.getEnclosedElements()) {
      if (m.getKind().toString().equals("METHOD") && context.hasAnnotationType(m, MUTEX_ANNOTATION)) {
        hasMutex = true;
        if (getKeyFromAnnotation(m) != null) {
          hasKeyedMutex = true;
        }
      }
    }

    if (hasMutex) {
      println().printIndent().print("mutex = new Mutex();").println().println();
      if (hasKeyedMutex) {
        println().printIndent().print("private keyedMutexes = new Map<any, Mutex>();").println();
        println().printIndent().print("private getMutex(key: any): Mutex {").println();
        println().printIndent().printIndent().print("if (!this.keyedMutexes.has(key)) {").println();
        println().printIndent().printIndent().printIndent().print("this.keyedMutexes.set(key, new Mutex());").println();
        println().printIndent().printIndent().print("}").println();
        println().printIndent().printIndent().print("return this.keyedMutexes.get(key)!;").println();
        println().printIndent().print("}").println().println();
      }
      this.adapterUtils.importFromWithModule("Mutex", "async-mutex");
    }
    super.beforeTypeBody(type);
  }

  private String getKeyFromAnnotation(Element element) {
    String val = context.getAnnotationValue(element, MUTEX_ANNOTATION, "key", String.class, null);
    return val != null && !val.isEmpty() ? val : null;
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
          String key = getKeyFromAnnotation(element);
          if (key != null) {
            return propertyClass.cast(
                "const idMutex = this.getMutex(" + key + ");\n" +
                "try {\n" +
                "    return await idMutex.runExclusive(async () => {\n" +
                "        {{body}}\n" +
                "    });\n" +
                "} finally {\n" +
                "    if (!idMutex.isLocked()) {\n" +
                "        this.keyedMutexes.delete(" + key + ");\n" +
                "    }\n" +
                "}"
            );
          } else {
            return propertyClass.cast("return await this.mutex.runExclusive(async () => {\n"
                + "{{body}}\n"
                + "});");
          }
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
