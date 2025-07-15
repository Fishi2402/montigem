package umlp.test_rte.monitor.model.action;

import java.util.Objects;

public final class ActivityBarEndAction implements Action {

  private final String signature;

  //TODO signature is not known at test compile time, only at runtime
  public ActivityBarEndAction(String signature) {
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
    ActivityBarEndAction that = (ActivityBarEndAction) object;
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
