package umlp.test_rte.monitor.model.expectedinteraction;

import umlp.test_rte.monitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ExpectedEndOfActivityBar implements ExpectedInteraction {

  private final List<OCLStatement> oclStatements = new ArrayList<>();

  @Override
  public List<OCLStatement> oclStatements() {
    return oclStatements;
  }

  @Override
  public <T, E extends Throwable> T matchVariant(ExpectedInteractionVariantMatcher<T, E> matcher)
  throws E {
    return matcher.whenEndOfActivityBar(this);
  }

  public ExpectedEndOfActivityBar withOCL(OCLStatement statement) {
    oclStatements.add(statement);
    return this;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || getClass() != object.getClass()) return false;
    ExpectedEndOfActivityBar that = (ExpectedEndOfActivityBar) object;
    return Objects.equals(oclStatements, that.oclStatements);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(oclStatements);
  }

  @Override
  public String toString() {
    return "ExpectedEndOfActivityBar{" + "oclStatements=" + oclStatements + '}';
  }
}
