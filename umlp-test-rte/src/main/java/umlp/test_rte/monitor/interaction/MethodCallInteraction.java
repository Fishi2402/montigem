package umlp.test_rte.monitor.interaction;

import java.util.Arrays;
import java.util.Objects;

public final class MethodCallInteraction implements Interaction {

  private final Object object;
  private final String signature;
  private final String methodName;
  private final Object[] params;

  public MethodCallInteraction(Object object,
      String signature,
      String methodName,
      Object... params) {
    this.object = object;
    this.signature = signature;
    this.methodName = methodName;
    this.params = params;
  }

  @Override
  public Object object() {
    return object;
  }

  @Override
  public String signature() {
    return signature;
  }

  public String methodName() {
    return methodName;
  }

  public Object[] params() {
    return params;
  }

  @Override
  public <T, E extends Throwable> T matchVariant(InteractionVariantMatcher<T, E> matcher) throws E {
    return matcher.whenMethodCall(this);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    MethodCallInteraction that = (MethodCallInteraction) o;
    return Objects.equals(object, that.object) && Objects.equals(signature, that.signature) &&
           Objects.equals(methodName, that.methodName) && Objects.deepEquals(params, that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(object, signature, methodName, Arrays.hashCode(params));
  }

  @Override
  public String toString() {
    return "MethodCallInteraction{" + "object=" + object + ", signature='" + signature + '\'' +
           ", methodName='" + methodName + '\'' + ", params=" + Arrays.toString(params) + '}';
  }
}
