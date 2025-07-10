package umlp.generator.domain.visitor.model.action;

import java.util.List;
import java.util.Optional;

public final class Call implements Action {

  private final String methodName;
  private final Optional<List<String>> argumentExpressions;

  public Call(String methodName, Optional<List<String>> argumentExpressions) {
    this.methodName = methodName;
    this.argumentExpressions = argumentExpressions;
  }

  @Override
  public <T> T matchVariant(ActionVariantMatcher<T> matcher) {
    return matcher.whenCall(this);
  }

  public String name() {
    return methodName;
  }

  public Optional<List<String>> argumentExpressions() {
    return argumentExpressions;
  }
}
