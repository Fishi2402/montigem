package umlp.cli.common;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Guarded operation, with an optional else operation
 *
 * @param <T>
 */
public class IfElseOp<T> implements IOperation<T> {
  protected final Predicate<T> guard;
  protected final IOperation<T> if_op;
  protected final Optional<IOperation<T>> else_op_opt;


  public IfElseOp(@Nonnull Predicate<T> guard, @Nonnull IOperation<T> if_op, @Nonnull IOperation<T> else_op) {
    this.guard = guard;
    this.if_op = if_op;
    this.else_op_opt = Optional.of(else_op);
  }

  public IfElseOp(@Nonnull Predicate<T> guard, @Nonnull IOperation<T> if_op) {
    this.guard = guard;
    this.if_op = if_op;
    this.else_op_opt = Optional.empty();
  }

  @Override
  public boolean doWork(T data) {
    if (this.guard.test(data)) {
      return if_op.doWork(data);
    } else {
      if (else_op_opt.isPresent())
        return else_op_opt.get().doWork(data);
      return true;
    }
  }
}
