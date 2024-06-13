package umlp.cli.common;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class Pipeline<T> implements IOperation<T> {
  protected final LinkedList<IOperation<T>> steps = new LinkedList<>();
  protected final List<Consumer<T>> finallySteps = new LinkedList<>();

  /**
   * Add a new operation step at the end of this pipeline
   */
  public void register(IOperation<T> newStep) {
    this.steps.add(newStep);
  }

  /**
   * Insert another operation step after the position
   * Does not work recursively
   */
  public void registerAfter(IOperation<T> position, IOperation<T> newStep) {
    this.steps.add(this.steps.indexOf(position), newStep);
  }

  /**
   * Adds a consumer, which is always executed after this pipeline.
   */
  public void addFinally(Consumer<T> finalStep) {
    this.finallySteps.add(finalStep);
  }

  @Override
  public boolean doWork(T data) {
    try {
      for (IOperation<T> step : this.steps) {
        if (!step.doWork(data)) {
          return false;
        }
      }
    } finally {
      // Finally, execute the finally steps
      this.finallySteps.forEach(tConsumer -> tConsumer.accept(data));
    }
    return true;
  }

}
