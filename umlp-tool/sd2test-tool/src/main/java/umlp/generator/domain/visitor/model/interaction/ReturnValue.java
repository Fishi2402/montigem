package umlp.generator.domain.visitor.model.interaction;

import java.util.Optional;

public final class ReturnValue {
  private final Optional<String> expression;

  public ReturnValue(Optional<String> expression) {
    this.expression = expression;
  }

  public boolean isComplete() {
    return expression.isPresent();
  }

  public Optional<String> expression() {
    return expression;
  }

  @Override
  public String toString() {
    return "ReturnValue{" + "expression=" + expression + '}';
  }
}
