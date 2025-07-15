package umlp.test_rte.monitor.event;

import java.util.Objects;

public final class ErrorEvent implements StackEvent {

  private final Object object;
  private final String signature;
  private final String errorTypeName;
  private final Throwable error;

  public ErrorEvent(Object object, String signature, String errorTypeName, Throwable error) {
    this.object = object;
    this.signature = signature;
    this.errorTypeName = errorTypeName;
    this.error = error;
  }


  @Override
  public Object object() {
    return object;
  }

  @Override
  public String signature() {
    return signature;
  }

  public String errorTypeName() {
    return errorTypeName;
  }

  public Throwable error() {
    return error;
  }

  @Override
  public <T> T matchVariant(StackEventVariantMatcher<T> matcher) {
    return matcher.whenError(this);
  }

  @Override
  public boolean equals(Object object1) {
    if (object1 == null || getClass() != object1.getClass()) return false;
    ErrorEvent that = (ErrorEvent) object1;
    return Objects.equals(object, that.object) && Objects.equals(signature, that.signature) &&
           Objects.equals(error, that.error);
  }

  @Override
  public int hashCode() {
    return Objects.hash(object, signature, error);
  }

  @Override
  public String toString() {
    return "ErrorEvent{" + "object=" + object + ", signature='" + signature + '\'' +
           ", exception=" + error + '}';
  }
}
