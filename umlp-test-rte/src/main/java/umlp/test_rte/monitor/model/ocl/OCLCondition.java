package umlp.test_rte.monitor.model.ocl;

import java.util.function.Supplier;

public final class OCLCondition implements OCLStatement {

  private final Supplier<Boolean> check;
  private final String checkStringRepresentation;

  public OCLCondition(Supplier<Boolean> check, String checkStringRepresentation) {
    this.check = check;
    this.checkStringRepresentation = checkStringRepresentation;
  }

  @Override
  public <T> T matchVariant(OCLStatementVariantMatcher<T> matcher) {
    return matcher.whenCondition(this);
  }

  public boolean check() {
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
