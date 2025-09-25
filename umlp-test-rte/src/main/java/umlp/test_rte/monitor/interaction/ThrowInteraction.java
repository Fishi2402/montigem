package umlp.test_rte.monitor.interaction;

import java.util.Objects;

public final class ThrowInteraction implements Interaction {

  private final Object object;
  private final String signature;
  private final String errorTypeName;
  private final Throwable error;
  private final boolean endsCurrentActivityBar;

  public ThrowInteraction(Object object, String signature, String errorTypeName, Throwable error,
      boolean endsCurrentActivityBar) {
    this.object = object;
    this.signature = signature;
    this.errorTypeName = errorTypeName;
    this.error = error;
    this.endsCurrentActivityBar = endsCurrentActivityBar;
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

  public boolean endsCurrentActivityBar() {
    return endsCurrentActivityBar;
  }

  @Override
  public <T> T matchVariant(StackEventVariantMatcher<T> matcher) {
    return matcher.whenError(this);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ThrowInteraction that = (ThrowInteraction) o;
    return endsCurrentActivityBar == that.endsCurrentActivityBar &&
           Objects.equals(object, that.object) && Objects.equals(signature, that.signature) &&
           Objects.equals(errorTypeName, that.errorTypeName) && Objects.equals(error, that.error);
  }

  @Override
  public int hashCode() {
    return Objects.hash(object, signature, errorTypeName, error, endsCurrentActivityBar);
  }

  @Override
  public String toString() {
    return "ThrowInteraction{" + "object=" + object + ", signature='" + signature + '\'' +
           ", errorTypeName='" + errorTypeName + '\'' + ", error=" + error +
           ", endsCurrentActivityBar=" + endsCurrentActivityBar + '}';
  }
}
