package gui.test;

import def.dom.HTMLInputElement;
import umlp.jsweet.extension.annotation.Component;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static def.dom.Globals.document;

@Component
public class FutureAdapterTest extends FutureAdapterTestTOP {

  private void setResultFieldEntry(String value) {
    HTMLInputElement el = (HTMLInputElement) document.getElementById("resultIdInput");
    if (el != null) {
      el.value = value;
    }
  }

  private String getRawInput() {
    HTMLInputElement el = (HTMLInputElement) document.getElementById("inputFieldIdInput");
    if (el != null && el.value != null && !el.value.trim().isEmpty()) {
      return el.value.trim();
    }
    return null;
  }

  @Override
  public Consumer<Void> testCompletedFuture() {
    return (Void) -> {
      String input = getRawInput();
      CompletableFuture.completedFuture(input)
          .thenAccept(res -> setResultFieldEntry("completedFuture: " + res));
    };
  }

  @Override
  public Consumer<Void> testSupplyAsync() {
    return (Void) -> {
      String input = getRawInput();
      CompletableFuture.supplyAsync(() -> input + "Async")
          .thenAccept(res -> setResultFieldEntry("supplyAsync: " + res));
    };
  }

  @Override
  public Consumer<Void> testRunAsync() {
    return (Void) -> {
      String input = getRawInput();
      CompletableFuture.runAsync(() -> {
            // side effect
          })
          .thenAccept(res -> setResultFieldEntry("runAsync: " + input));
    };
  }

  @Override
  public Consumer<Void> testFailedFuture() {
    return (Void) -> {
      CompletableFuture.failedFuture(new RuntimeException("failed"))
          .exceptionally(ex -> "caught")
          .thenAccept(res -> setResultFieldEntry("failedFuture: " + res));
    };
  }

  @Override
  public Consumer<Void> testThenApply() {
    return (Void) -> {
      String input = getRawInput();
      CompletableFuture.completedFuture(input)
          .thenApply(res -> res + "Applied")
          .thenAccept(res -> setResultFieldEntry("thenApply: " + res));
    };
  }

  @Override
  public Consumer<Void> testThenApplyAsync() {
    return (Void) -> {
      String input = getRawInput();
      CompletableFuture.completedFuture(input)
          .thenApplyAsync(res -> res + "AppliedAsync")
          .thenAccept(res -> setResultFieldEntry("thenApplyAsync: " + res));
    };
  }

  @Override
  public Consumer<Void> testThenAccept() {
    return (Void) -> {
      String input = getRawInput();
      CompletableFuture.completedFuture(input)
          .thenAccept(res -> setResultFieldEntry("thenAccept: " + res));
    };
  }

  @Override
  public Consumer<Void> testThenAcceptAsync() {
    return (Void) -> {
      String input = getRawInput();
      CompletableFuture.completedFuture(input)
          .thenAcceptAsync(res -> setResultFieldEntry("thenAcceptAsync: " + res));
    };
  }

  @Override
  public Consumer<Void> testThenRun() {
    return (Void) -> {
      String input = getRawInput();
      CompletableFuture.completedFuture(input)
          .thenRun(() -> setResultFieldEntry("thenRun: " + input));
    };
  }

  @Override
  public Consumer<Void> testThenRunAsync() {
    return (Void) -> {
      String input = getRawInput();
      CompletableFuture.completedFuture(input)
          .thenRunAsync(() -> setResultFieldEntry("thenRunAsync: " + input));
    };
  }

  @Override
  public Consumer<Void> testThenCompose() {
    return (Void) -> {
      String input = getRawInput();
      CompletableFuture.completedFuture(input)
          .thenCompose(res -> CompletableFuture.completedFuture(res + "Composed"))
          .thenAccept(res -> setResultFieldEntry("thenCompose: " + res));
    };
  }

  @Override
  public Consumer<Void> testThenComposeAsync() {
    return (Void) -> {
      String input = getRawInput();
      CompletableFuture.completedFuture(input)
          .thenComposeAsync(res -> CompletableFuture.completedFuture(res + "ComposedAsync"))
          .thenAccept(res -> setResultFieldEntry("thenComposeAsync: " + res));
    };
  }

  @Override
  public Consumer<Void> testExceptionally() {
    return (Void) -> {
      String input = getRawInput();
      CompletableFuture.<String>failedFuture(new RuntimeException("error"))
          .exceptionally(ex -> input + "Exception")
          .thenAccept(res -> setResultFieldEntry("exceptionally: " + res));
    };
  }

  @Override
  public Consumer<Void> testWhenComplete() {
    return (Void) -> {
      String input = getRawInput();
      CompletableFuture.completedFuture(input)
          .whenComplete((res, ex) -> {
            if (ex == null) {
              setResultFieldEntry("whenComplete: " + res);
            }
          });
    };
  }

  @Override
  public Consumer<Void> testWhenCompleteAsync() {
    return (Void) -> {
      String input = getRawInput();
      CompletableFuture.completedFuture(input)
          .whenCompleteAsync((res, ex) -> {
            if (ex == null) {
              setResultFieldEntry("whenCompleteAsync: " + res);
            }
          });
    };
  }

  @Override
  public Consumer<Void> testHandle() {
    return (Void) -> {
      String input = getRawInput();
      CompletableFuture.<String>failedFuture(new RuntimeException("error"))
          .handle((res, ex) -> ex != null ? input + "Handled" : res)
          .thenAccept(res -> setResultFieldEntry("handle: " + res));
    };
  }

  @Override
  public Consumer<Void> testHandleAsync() {
    return (Void) -> {
      String input = getRawInput();
      CompletableFuture.<String>failedFuture(new RuntimeException("error"))
          .handleAsync((res, ex) -> ex != null ? input + "HandledAsync" : res)
          .thenAccept(res -> setResultFieldEntry("handleAsync: " + res));
    };
  }

  @Override
  public Consumer<Void> testJoin() {
    return (Void) -> {
      String input = getRawInput();
      String res = CompletableFuture.completedFuture(input).join();
      setResultFieldEntry("join: " + res);
    };
  }

  @Override
  public Consumer<Void> testGet() {
    return (Void) -> {
      String input = getRawInput();
      try {
        String res = CompletableFuture.completedFuture(input).get();
        setResultFieldEntry("get: " + res);
      } catch (Exception e) {
        setResultFieldEntry("get: failed");
      }
    };
  }
}
