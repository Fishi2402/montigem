/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import de.se_rwth.commons.logging.Log;
import org.jsweet.transpiler.Java2TypeScriptTranslator;
import org.jsweet.transpiler.extension.PrinterAdapter;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Optional;

/**
 * This class represents an adapter that handles backend-only methods by substituting their body with specific logic.
 * It extends PrinterAdapter.
 */
public class BackendOnlyAdapter extends PrinterAdapter {

  public static final String BACKEND_ONLY_ANNOTATION = "umlp.jsweet.extension.annotation.BackendOnly";

  public BackendOnlyAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  public boolean substituteMethodBody(TypeElement parentTypeElement, ExecutableElement method) {
    if (isBackendOnly(method)) {
      String val = Optional.ofNullable(
              getAnnotationValue(method, BACKEND_ONLY_ANNOTATION, VariableElement.class, null)
          )
          .map(VariableElement::getSimpleName).map(Object::toString).orElse(null);
      if ("STATUS_OK".equals(val)) {
        print("return StatusResult.ok(this.getId());\n");
      } else if ("STATUS_ERROR".equals(val)) {
        print("return StatusResult.error(this.getId());\n");
      } else {
        print("throw new Error('This method can only be called in the backend!');\n");
      }
      return true;
    } else {
      return super.substituteMethodBody(parentTypeElement, method);
    }
  }

  private boolean isBackendOnly(ExecutableElement method) {
    return context.hasAnnotationType(method, BACKEND_ONLY_ANNOTATION);
  }
}
