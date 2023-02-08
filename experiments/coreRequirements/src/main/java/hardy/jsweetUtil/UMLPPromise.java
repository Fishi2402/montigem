/* (c) https://github.com/MontiCore/monticore */
package hardy.jsweetUtil;

import def.js.Promise;
import def.js.PromiseLike;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class UMLPPromise <T> extends Promise<T> {

  private final BiConsumer<Consumer<T>, Consumer<Object>> executor;

  public UMLPPromise(
      BiConsumer<Consumer<T>, Consumer<Object>> executor) {
    super(executor);
    this.executor = executor;
  }

  public BiConsumer<Consumer<T>, Consumer<Object>> getExecutor() {
    return executor;
  }
}
