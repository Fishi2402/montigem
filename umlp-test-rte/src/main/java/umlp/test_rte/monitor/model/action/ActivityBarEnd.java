package umlp.test_rte.monitor.model.action;

import java.util.Objects;

public final class ActivityBarEnd implements Action {

  private final String signature;

  public ActivityBarEnd(String signature) {
    this.signature = signature;
  }

  @Override
  public <T> T matchVariant(ActionVariantMatcher<T> matcher) {
    return matcher.whenActivityBarEnd(this);
  }

  @Deprecated
  public String methodName() {
    return signature.substring(0, signature.indexOf("("));
  }

  public String signature() {
    return signature;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || getClass() != object.getClass()) return false;
    ActivityBarEnd that = (ActivityBarEnd) object;
    return Objects.equals(signature, that.signature);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(signature);
  }

  @Override
  public String toString() {
    return "ActivityBarEnd{" + "signature='" + signature + '\'' + '}';
  }
}
