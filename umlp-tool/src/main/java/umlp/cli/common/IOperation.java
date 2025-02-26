package umlp.cli.common;

import java.util.List;

public interface IOperation<T> {
  /**
   * Execute a piece of work
   * @param data passed data
   * @return true, if the pipeline should continue
   */
  boolean doWork(T data);

  // simple operation, always returning true
  interface ISimpleOperation<T> extends IOperation<T> {
    void doSimpleWork(T data);

    @Override
    default boolean doWork(T data) {
      this.doSimpleWork(data);
      return true;
    }
  }

  // IOperation without data, useable for lambdas
  interface ISimpleOperation2<T> extends IOperation<T> {
    void doSimpleWork();

    @Override
    default boolean doWork(T data) {
      this.doSimpleWork();
      return true;
    }
  }

  /**
   * A utility for the reporter
   * @param <T>
   */
  interface IOperationWithDependantPipelines<T> extends IOperation<T> {
    List<Pipeline<?>> innerPipelinesForReporting();
  }

  static <T> IOperation<T> withDependantPipelines(final IOperation<T> operation, final List<Pipeline<?>> pipelines) {
    return new IOperationWithDependantPipelines<T>() {
      @Override
      public List<Pipeline<?>> innerPipelinesForReporting() {
        return pipelines;
      }

      @Override
      public boolean doWork(T data) {
        return operation.doWork(data);
      }
    };
  }


}
