package umlp.test_rte.monitor.model.action;

import java.util.Objects;

public final class ActivityBarEnd implements Action {

  //TODO method signature would also come into play
  private final String methodName;

  public ActivityBarEnd(String methodName) {
    this.methodName = methodName;
  }

  @Override
  public <T> T matchVariant(ActionVariantMatcher<T> matcher) {
    return matcher.whenActivityBarEnd(this);
  }

  public String methodName() {
    return methodName;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || getClass() != object.getClass()) return false;
    ActivityBarEnd that = (ActivityBarEnd) object;
    return Objects.equals(methodName, that.methodName);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(methodName);
  }

  @Override
  public String toString() {
    return "ActivityBarEnd{" + "methodName='" + methodName + '\'' + '}';
  }
}
