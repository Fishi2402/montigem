package umlp.test_rte.monitor.interaction;

import java.util.Arrays;
import java.util.Objects;

public final class ConstructorCallInteraction implements Interaction {

  private final Object object;
  private final String signature;
  private final String className;
  private final Object[] params;

  public ConstructorCallInteraction(Object object, String signature, String className, Object... params) {
    this.object = object;
    this.signature = signature;
    this.className = className;
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

  public String className() {
    return className;
  }

  public Object[] params() {
    return params;
  }

  @Override
  public <T> T matchVariant(StackEventVariantMatcher<T> matcher) {
    return matcher.whenConstructorCall(this);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ConstructorCallInteraction that = (ConstructorCallInteraction) o;
    return Objects.equals(object, that.object) && Objects.equals(signature, that.signature) &&
           Objects.equals(className, that.className) && Objects.deepEquals(params, that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(object, signature, className, Arrays.hashCode(params));
  }

  @Override
  public String toString() {
    return "ConstructorCallInteraction{" + "object=" + object + ", signature='" + signature + '\'' +
           ", className='" + className + '\'' + ", params=" + Arrays.toString(params) + '}';
  }
}
