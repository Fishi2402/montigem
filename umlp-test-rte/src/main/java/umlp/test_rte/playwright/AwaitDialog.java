package umlp.test_rte.playwright;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Page;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * A configurable utility class which can be used to await a {@link Dialog} on a {@link Page}
 *
 * <p><strong>Example</strong>
 * <pre>{@code
 * Dialog dialog = new AwaitDialog(page, () -> showAlert(page))
 *   .withTimeout(5, TimeUnit.Seconds)
 *   .get()
 *   .orElseThrow();
 * assertEquals("alert", dialog.type());
 * assertEquals("Foo Bar", dialog.message());
 * }</pre>
 */
public class AwaitDialog {

  private final Page page;
  private Runnable dialogTrigger;
  private long timeout = 100;
  private TimeUnit timeoutUnit = TimeUnit.MILLISECONDS;
  private boolean automaticallyDismissDialog = true;

  public AwaitDialog(Page page, Runnable trigger) {
    this.page = page;
    this.dialogTrigger = trigger;
  }

  public AwaitDialog withTrigger(Runnable trigger) {
    this.dialogTrigger = trigger;
    return this;
  }

  /**
   * Sets how long this will wait for a {@link Dialog} to occur. Default is 100ms.
   *
   * @param timeout How long to wait
   * @param unit    {@link TimeUnit} of timeout
   * @return this
   */
  public AwaitDialog withTimeOut(long timeout, TimeUnit unit) {
    this.timeout = timeout;
    this.timeoutUnit = unit;
    return this;
  }

  /**
   * Set whether {@link Dialog#dismiss()} will be called directly after it has occurred. Default value is {@code true}.
   * <p><strong>Attention:</strong>
   * Setting this to {@code false} requires you to call {@link Dialog#dismiss()} or {@link Dialog#accept()}, otherwise
   * the {@link Page} might stop responding!
   *
   * @param automaticallyDismissDialog Whether to automatically dismiss dialogs
   * @return this
   */
  public AwaitDialog automaticallyDismiss(boolean automaticallyDismissDialog) {
    this.automaticallyDismissDialog = automaticallyDismissDialog;
    return this;
  }

  /**
   * Waits for a {@link Dialog} to occur within the set timeout
   *
   * @return The {@link Dialog} that occurred, if any
   * @throws InterruptedException If this thread gets interrupted while waiting
   * @see #withTimeOut(long, TimeUnit)
   */
  public Optional<Dialog> get() throws InterruptedException {
    BlockingQueue<Dialog> channel = new ArrayBlockingQueue<>(1);
    Consumer<Dialog> handler = dialog -> {
      channel.add(dialog);
      if (automaticallyDismissDialog) dialog.dismiss();
    };
    page.onDialog(handler);
    dialogTrigger.run();
    page.offDialog(handler);
    return Optional.ofNullable(channel.poll(timeout, timeoutUnit));
  }

  /**
   * Waits for a {@link Dialog} to occur within the set timeout and passes it to the provided {@link Consumer}.
   * {@link Dialog#accept()} or {@link Dialog#dismiss()} must be called within the {@link Consumer}, since otherwise the
   * website might hang up.
   * <p>This function will not return unless the {@link Consumer} has returned, or the timeout has expired and no
   * {@link Dialog} occurred.
   *
   * <p>If you only need to run some assertions on the {@link Dialog}, consider using {@link #get()} instead.
   *
   * @param onDialog Will be called when a {@link Dialog} occurs
   * @throws InterruptedException If this thread gets interrupted while waiting
   * @throws TimeoutException When the timeout expires and no dialog occurred
   * @see #withTimeOut(long, TimeUnit)
   */
  public void and(Consumer<Dialog> onDialog) throws InterruptedException, TimeoutException {
    var occurrenceLatch = new CountDownLatch(1);
    var processingLatch = new CountDownLatch(1);
    Consumer<Dialog> handler = dialog -> {
      occurrenceLatch.countDown();
      if (automaticallyDismissDialog) dialog.dismiss();
      onDialog.accept(dialog);
      processingLatch.countDown();
    };
    page.onDialog(handler);
    dialogTrigger.run();
    if (occurrenceLatch.await(timeout, timeoutUnit)) {
      //dialog occurred -> wait until it has been processed
      processingLatch.await();
      return;
    }

    throw new TimeoutException("Timeout expired and no dialog occurred");
  }
}
