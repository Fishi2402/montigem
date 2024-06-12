package umlp.cli.common;

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
}
