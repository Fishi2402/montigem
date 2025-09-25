package umlp.test_rte.monitor.model.ocl;

import java.util.function.Supplier;

public final class OCLCondition implements OCLStatement {

  private final Supplier<Boolean> check;
  private final String checkStringRepresentation;

  private OCLCondition(Supplier<Boolean> check, String checkStringRepresentation) {
    this.check = check;
    this.checkStringRepresentation = checkStringRepresentation;
  }

  public static OCLCondition from(Supplier<Boolean> check, String checkStringRepresentation) {
    return new OCLCondition(check, checkStringRepresentation);
  }

  @Override
  public <T> T matchVariant(OCLStatementVariantMatcher<T> matcher) {
    return matcher.whenCondition(this);
  }

  public boolean evaluate() {
    return check.get();
  }

  public String asString() {
    return checkStringRepresentation;
  }

  @Override
  public String toString() {
    return "OCLCondition{" + "check=" + check + ", checkStringRepresentation='" +
           checkStringRepresentation + '\'' + '}';
  }
}
