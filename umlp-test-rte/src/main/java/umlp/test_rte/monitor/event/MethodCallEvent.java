package umlp.test_rte.monitor.event;

import java.util.Arrays;
import java.util.Objects;

public final class MethodCallEvent implements StackEvent {

  private final Object object;
  private final String signature;
  private final String methodName;
  private final Object[] params;

  public MethodCallEvent(Object object, String signature, String methodName, Object... params) {
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
  public <T> T matchVariant(StackEventVariantMatcher<T> matcher) {
    return matcher.whenMethodCall(this);
  }

  @Override
  public boolean equals(Object object1) {
    if (object1 == null || getClass() != object1.getClass()) return false;
    MethodCallEvent methodCallEvent = (MethodCallEvent) object1;
    return Objects.equals(object, methodCallEvent.object) &&
           Objects.equals(signature, methodCallEvent.signature) &&
           Objects.deepEquals(params, methodCallEvent.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(object, signature, Arrays.hashCode(params));
  }

  @Override
  public String toString() {
    return "CallEvent{" + "object=" + object + ", signature='" + signature + '\'' + ", params=" +
           Arrays.toString(params) + '}';
  }
}
