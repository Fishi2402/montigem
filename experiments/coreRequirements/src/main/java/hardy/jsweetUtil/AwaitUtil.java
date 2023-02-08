/* (c) https://github.com/MontiCore/monticore */
package hardy.jsweetUtil;

import def.js.Promise;
import jsweet.lang.Async;
import jsweet.lang.Replace;
import jsweet.util.Lang;

import java.util.concurrent.atomic.AtomicReference;

public class AwaitUtil {

  @Replace("return AwaitUtil.awaitJsweet(promise);")
  public static <T> T await(UMLPPromise<T> promise) {
    if (true) {
      AtomicReference<T> t = new AtomicReference<>();
      promise.getExecutor().accept(resolve -> {
        t.set(resolve);
      }, reject -> {
        t.set(null);
      });
      return t.get();
    } else {
      // Needed to make this async
       return awaitJsweet(promise);
    }
  }

  @Async
  private static <T> T awaitJsweet(Promise<T> promise) {
    return Lang.await(promise);
  }

}
