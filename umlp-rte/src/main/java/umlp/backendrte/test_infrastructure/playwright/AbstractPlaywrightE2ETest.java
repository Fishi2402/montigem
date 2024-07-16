package umlp.backendrte.test_infrastructure.playwright;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.test.annotation.DirtiesContext;
import umlp.backendrte.common.DatabaseAccessManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * An abstract base for end-to-end tests which use playwright. When using this class it is
 * important that at the end of your constructor in {@code app.component.ts} there is the line
 * <pre>{@code (<any>window).websocket_connected = true;}</pre>
 * <p>
 * <strong>Debugging</strong>
 * <p>Playwright tests can be debugged by modifying the run configuration of the test to use the
 * environment variable {@code PWDEBUG=1}
 */
@UsePlaywright
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// restart backend for every method
public abstract class AbstractPlaywrightE2ETest {

  static final int frontendServerPort = getFreePort();
  static final int backendServerPort = getFreePort();
  protected Optional<Process> frontendProcess = Optional.empty();

  public static int backendServerPort() {
    return backendServerPort;
  }

  /**
   * Checks whether the current operating system is Windows
   *
   * @return {@code true} if the current OS is Windows, else {@code false}
   */
  public static boolean environmentIsWindows() {
    return System.getProperty("os.name").startsWith("Windows");
  }

  /**
   * Gets an unbound port from the os
   *
   * @return The free port
   * @throws RuntimeException If no free port was found for whatever reason
   */
  protected static int getFreePort() {
    try (ServerSocket socket = new ServerSocket(0)) {
      return socket.getLocalPort();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected int getFrontendServerPort() {
    return frontendServerPort;
  }

  protected int getBackendServerPort() {
    return backendServerPort;
  }

  protected void waitForAngular(Page page) {
    //@formatter:off
    page.evaluate("async () => {" +
                  "  if (window.getAllAngularTestabilities) {\n" +
                  "    // @ts-expect-error\n" +
                  "    await Promise.all(window.getAllAngularTestabilities().map(whenStable));\n" +
                  "    \n" +
                  "    // @ts-expect-error\n" +
                  "    async function whenStable(testability) {\n" +
                  "      return new Promise(res => testability.whenStable(res));\n" +
                  "    }\n" +
                  "  }\n" +
                  "}");
    //@formatter:on
  }

  /**
   * Waits until the web socket of the application has connected.
   * <p>
   * <strong>ATTENTION: </strong>
   * <p>Calling this method requires the {@code app.component.ts} to
   * have the following line of code at the end of the constructor:
   * <pre>{@code
   * (<any>window).websocket_connected = true;
   * }</pre>
   * Otherwise, this method can potentially never return!
   *
   * @param page The current {@link Page}
   */
  protected void waitForWebsocket(Page page) {
    long start = System.currentTimeMillis();

    Locator.EvaluateOptions options = new Locator.EvaluateOptions();
    options.setTimeout(30_000);
    page.locator("body").evaluate(
        //@formatter:off
        "new Promise(resolve => {\n" +
        "  const checkReady = () => {\n" +
        "    if (window.websocket_connected != undefined) {\n" +
        "      resolve(window.websocket_connected);\n" +
        "    } else {\n" +
        "      setTimeout(checkReady, 100);\n" +
        "    }\n" +
        "  };\n" +
        "  checkReady();\n" +
        "});"
        //@formatter:on
        , null, options);

    System.out.println("Waiting for websocket took " + (System.currentTimeMillis() - start) + "ms");
  }

  /**
   * Waits until the page is fully loaded. Has the same requirements as {@link #waitForWebsocket(Page)}
   *
   * @param page The {@link Page}
   */
  protected void waitRdy(Page page) {
    waitForAngular(page);
    waitForWebsocket(page);
  }

  /**
   * Waits for a {@link Dialog} to occur on the provided {@link Page} while the trigger is run
   *
   * @param page    {@link Page} where the {@link Dialog} should occur
   * @param trigger {@link Runnable} which should trigger the {@link Dialog}
   * @return A {@link AwaitDialog} instance
   */
  protected AwaitDialog awaitDialog(Page page, Runnable trigger) {
    return new AwaitDialog(page, trigger);
  }

  protected String getUrlBase() {
    return "http://localhost:" + getFrontendServerPort();
  }

  protected String getSaveUrlBase() {
    return getUrlBase().endsWith("/") ? getUrlBase() : getUrlBase() + "/";
  }

  /**
   * Navigates to a new route and waits for the website to reload.
   * <p>
   * Has the same requirements as {@link #waitRdy(Page)}!
   * <p>
   * <strong>Example</strong>
   * <pre>{@code
   * navAndWait(page, "/person/Overview");
   * }</pre>
   *
   * @param page  The {@link Page}
   * @param route Where to navigate
   */
  protected void navAndWait(Page page, String route) {
    page.navigate(getSaveUrlBase() + route.replaceAll("^/*", ""));
    waitRdy(page);
  }

  /**
   * Gets the first {@link Locator} that matches the given selector from a {@link Page} and from that gets the
   * underlying html element of the respective GuiDSL component.
   *
   * <p>Should be preferred over statements like {@code page.locator(".foo > button")} since the locator of the
   * underlying html element may change
   *
   * <p><strong>Example</strong>
   * <pre>{@code
   * locator(page, ".foo", ComponentKind.GemButton).click();
   * }</pre>
   *
   * @param page     The {@link Page} to get the {@link Locator} from
   * @param selector Selector for the {@link Locator} to get
   * @param kind     What kind of component the selector denounces
   * @return The respective {@link Locator}
   */
  protected Locator locator(Page page, String selector, ComponentKindLike kind) {
    return locator(new PageAdapter(page), selector, kind);
  }

  /**
   * Gets the first {@link Locator} that matches the given selector inside another {@link Locator} and from that gets the
   * underlying html element of the respective GuiDSL component.
   *
   * <p>Should be preferred over statements like {@code locator.locator(".foo > button")} since the locator of the
   * underlying html element may change
   *
   * <p><strong>Example</strong>
   * <pre>{@code
   * locator(locator, ".foo", ComponentKind.GemButton).click();
   * }</pre>
   *
   * @param locator  The {@link Locator} to get the next {@link Locator} from
   * @param selector Selector for the {@link Locator} to get
   * @param kind     What kind of component the selector denounces
   * @return The respective {@link Locator}
   */
  protected Locator locator(Locator locator, String selector, ComponentKindLike kind) {
    return locator(new LocatorAdapter(locator), selector, kind);
  }

  private Locator locator(LocatorHolder locatorHolder, String selector, ComponentKindLike kind) {
    return locatorHolder.locator(selector).first().locator(kind.selector());
  }

  /**
   * Gets the folder where the gradle node plugin installed node
   *
   * @return A {@link File} denoting the nodejs folder
   */
  protected File getNodeJSDirectory() {
    return new File(".gradle/nodejs");
  }

  /**
   * Gets the path where the npm binary/executable is located.
   *
   * <p>By default this method attempts to find the executable in the {@code nodejs} folder
   *
   * @return The found path string
   * @throws IOException            When the mentioned directories can't be searched
   * @throws NoSuchElementException When the npm executable was not found
   * @see #getNodeJSDirectory()
   */
  protected String getNpmPath() throws IOException, NoSuchElementException {
    try (var stream = Files.walk(getNodeJSDirectory().toPath())) {
      return stream.filter(p -> p.toFile()
              .getName()
              .equals(environmentIsWindows() ? "npm.cmd" : "npm"))
          .map(Path::toString)
          .min(Comparator.comparing(String::length))
          .orElseThrow();
    }
  }

  /**
   * Gets the directory to which the frontend app has been generated to.
   *
   * <p>By default this directory is assumed to be {@code build/frontendGen}
   *
   * @return The {@link File} denoting the frontendGen directory
   */
  protected File getFrontendGenDirectory() {
    return new File("build/frontendGen");
  }

  /**
   * Starts npm in a new {@link Process}. Fetches all relevant information from the following
   * methods:
   * <ul>
   *   <li>{@link #getNpmPath()}</li>
   *   <li>{@link #getFrontendServerPort()}</li>
   *   <li>{@link #getBackendServerPort()}</li>
   *   <li>{@link #getFrontendGenDirectory()}</li>
   * </ul>
   *
   * @return The created {@link Process}
   * @throws IOException If an {@link IOException} occurs while fetching information or starting the
   *                     process
   */
  protected Process startNpmProcess() throws IOException {
    String npmPath = getNpmPath();
    int frontendServerPort = getFrontendServerPort();
    int backendServerPort = getBackendServerPort();
    File frontendGenDirectory = getFrontendGenDirectory();

    Path nodeDir = Paths.get(npmPath).getParent();

    System.out.println("Starting npm from " + npmPath + " on port " + frontendServerPort);
    ProcessBuilder pb = new ProcessBuilder(npmPath,
                                           "run",
                                           "start",
                                           "--",
                                           "--port",
                                           "" + frontendServerPort);
    pb.environment()
        .put("PATH", nodeDir.toString() + File.pathSeparator + pb.environment().get("PATH"));
    pb.environment().put("BACKEND_PROXY_ADDRESS", "http://localhost:" + backendServerPort);
    return pb.directory(frontendGenDirectory).start();
  }

  /**
   * Monitors the start of the frontend {@link Process} and blocks until the frontend has been
   * started successfully or fails with an error.
   *
   * <p>Also propagates {@code stdout} and {@code stderr} of the npm {@link Process}
   *
   * @param frontendProcess The npm {@link Process}
   * @throws IOException           If the input can't be propagated for any reason
   * @throws IllegalStateException When the start of the frontend {@link Process} failed
   */
  protected void monitorFrontendStart(Process frontendProcess) throws IOException, IllegalStateException {
    boolean available = false;

    long start = System.currentTimeMillis();
    List<String> lines = new ArrayList<>();
    BufferedReader stdOutreader = new BufferedReader(new InputStreamReader(frontendProcess.getInputStream()));
    BufferedReader stderrReader = new BufferedReader(new InputStreamReader(frontendProcess.getErrorStream()));
    while (frontendProcess.isAlive()) {
      if ((System.currentTimeMillis() - start > 1000 * 60)) {
        System.out.println("Frontend not available after 60 seconds. Aborting.");
        break;
      }

      String line = stdOutreader.readLine();
      lines.add(line);
      System.out.println("[FRONTEND SERVER] " + line);
      if (line != null && line.contains("localhost:" + frontendServerPort)) {
        available = true;
        break;
      } else if (line != null && line.contains("already in use")) {
        break;
      }
    }

    if (!available) {
      String line;
      System.out.println("NPM stdout was:");
      for (String s : lines) {
        System.out.println(s);
      }
      while ((line = stdOutreader.readLine()) != null) {
        System.out.println(line);
      }

      System.out.println("NPM stderr was:");
      while ((line = stderrReader.readLine()) != null) {
        System.out.println(line);
      }

      throw new IllegalStateException("Frontend server can not be started");
    }

    System.out.println("Frontend started successfully");
  }

  /**
   * Starts the frontend process and stores it in the {@link #frontendProcess} attribute.
   *
   * <p>Avoid overriding this method, use {@link #startNpmProcess()} and
   * {@link #monitorFrontendStart(Process)} instead
   *
   * @throws IOException When an {@link IOException} occurs while starting the frontend
   */
  @BeforeAll
  protected void startFrontend() throws IOException {
    Process process = startNpmProcess();
    frontendProcess = Optional.of(process);
    monitorFrontendStart(process);
  }

  /**
   * Stops the {@link #frontendProcess} if it is present
   */
  @AfterAll
  protected void stopFrontend() {
    frontendProcess.filter(Process::isAlive).ifPresentOrElse(process -> {
      System.out.println("Stopping frontend process");
      process.descendants().forEach(ProcessHandle::destroy);
      process.destroy();
    }, () -> System.out.println("Can not stop frontend process"));
  }

  /**
   * Ensures that data gets wiped from the database between two test cases
   */
  @AfterEach
  protected void cleanupBackend() {
    DatabaseAccessManager.executeStatement("DROP ALL OBJECTS");
  }

  //Page and Locator each define the locator function themselves
  private interface LocatorHolder {
    Locator locator(String selector);
  }

  private static class PageAdapter implements LocatorHolder {

    private final Page page;

    public PageAdapter(Page page) {
      this.page = page;
    }

    @Override
    public Locator locator(String selector) {
      return page.locator(selector);
    }
  }

  private static class LocatorAdapter implements LocatorHolder {
    private final Locator locator;

    public LocatorAdapter(Locator locator) {
      this.locator = locator;
    }

    @Override
    public Locator locator(String selector) {
      return locator.locator(selector);
    }
  }
}
