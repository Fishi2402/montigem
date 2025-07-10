package umlp.generator.domain.visitor.model.action;

import java.util.List;
import java.util.Optional;

public class Call implements Action {

  private final String name;
  private final Optional<List<String>> argumentExpressions;

  public Call(String name, Optional<List<String>> argumentExpressions) {
    this.name = name;
    this.argumentExpressions = argumentExpressions;
  }

  @Override
  public <T> T matchVariant(ActionVariantMatcher<T> matcher) {
    return matcher.whenCall(this);
  }

  public String name() {
    return name;
  }

  public Optional<List<String>> argumentExpressions() {
    return argumentExpressions;
  }
}
