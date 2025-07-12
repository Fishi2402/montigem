package umlp.generator.domain.visitor.model.action;

import java.util.List;
import java.util.Optional;

public final class ThrowAction implements Action {

  private final String throwableTypeName;
  private final Optional<List<String>> arguments;

  public ThrowAction(String throwableTypeName, Optional<List<String>> arguments) {
    this.throwableTypeName = throwableTypeName;
    this.arguments = arguments;
  }

  @Override
  public <T> T matchVariant(ActionVariantMatcher<T> matcher) {
    return matcher.whenThrow(this);
  }

  public String throwableTypeName() {
    return throwableTypeName;
  }

  public Optional<List<String>> arguments() {
    return arguments;
  }

  @Override
  public String toString() {
    return "ThrowAction{" + "throwableTypeName='" + throwableTypeName + '\'' + ", arguments=" +
           arguments + '}';
  }
}
