/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.backendAdapters;

import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import umlp.jsweet.extension.UMLPAdapterUtils;

import javax.lang.model.type.TypeMirror;

/**
 * This adapter is responsible to use some additional {@link java.util} classes.
 */
public class UMLPJavaBasicsAdapter extends UMLPAbstractAdapter {

  public UMLPJavaBasicsAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    initTypeMappings();
  }

  private void initTypeMappings() {
    getTypeMirrorMappings().add(0, UMLPAdapterUtils.computeJavaOptionalTypeMapping(this, types()));
    getTypeMirrorMappings().add(0, UMLPAdapterUtils.computeDateTypeMapping(this, types()));
  }

  /**
   * Can be used to adapt method invocations like {@code Objects.requireNonNull(...)}
   */
  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    if (invocation.getTargetType() == null) {
      return super.substituteMethodInvocation(invocation);
    }
    if (UMLPAdapterUtils.substituteMethodInvocation_JavaUtilObjects(this, invocation)) {
      return true;
    } else if (UMLPAdapterUtils.substituteMethodInvocation_JavaLangString(this, invocation)) {
      return true;
    }
    return super.substituteMethodInvocation(invocation);
  }

  @Override
  public TypeMirror mapType(TypeMirror type) {
    return UMLPAdapterUtils.mapOptionalType(this, type);
  }

  @Override
  protected void initStaticLibrary() {
    addToStaticLibrary("java.util.concurrent.atomic.AtomicReference");
    addToStaticLibrary("java.util.Random");
    putToStaticLibrary("java.lang.String", "StringHelper");
  }


}
