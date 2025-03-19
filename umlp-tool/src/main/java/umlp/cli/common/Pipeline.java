package umlp.cli.common;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class Pipeline<T> implements IOperation<T> {
  protected final LinkedList<Named<IOperation<T>>> steps = new LinkedList<>();
  protected final List<Consumer<T>> finallySteps = new LinkedList<>();

  /**
   * Add a new operation step at the end of this pipeline
   */
  public void register(IOperation<T> newStep) {
    this.register(Named.getUnnamed(), newStep);
  }

  /**
   * Add a new operation step at the end of this pipeline
   */
  public void register(String name, IOperation<T> newStep) {
    this.steps.add(new Named<>(name, newStep));
  }

  public void registerFirst(IOperation<T> newStep) {
    this.registerFirst(Named.getUnnamed(), newStep);
  }


    /**
     * Add a new operation step at the front of this pipeline
     */
  public void registerFirst(String name, IOperation<T> newStep) {
    this.steps.add(0, new Named<>(name, newStep));
  }

  public void registerAfter(IOperation<T> position, IOperation<T> newStep) {
    this.registerAfter(position, Named.getUnnamed(), newStep);
  }


    /**
     * Insert another operation step after the position
     * Does not work recursively
     */
  public void registerAfter(IOperation<T> position, String name, IOperation<T> newStep) {
    int index = 0;
    int foundIndex = -1;
    for (var e : this.steps) {
      if (e.getValue().equals(position)) {
        foundIndex = index;
        break;
      }
      index++;
    }
    if (foundIndex == -1) {
      throw new IllegalArgumentException("Could no find step " + position);
    }
    this.steps.add(foundIndex, new Named<>(name, newStep));
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
      for (Named<IOperation<T>> step : this.steps) {
        if (!step.getValue().doWork(data)) {
          return false;
        }
      }
    } finally {
      // Finally, execute the finally steps
      this.finallySteps.forEach(tConsumer -> tConsumer.accept(data));
    }
    return true;
  }

  public LinkedList<Named<IOperation<T>>> getSteps() {
    return steps;
  }

  public List<Consumer<T>> getFinallySteps() {
    return finallySteps;
  }
}
