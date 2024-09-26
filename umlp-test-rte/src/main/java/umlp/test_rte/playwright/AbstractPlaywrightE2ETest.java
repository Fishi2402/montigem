package umlp.test_rte.playwright;

import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.test.annotation.DirtiesContext;
import umlp.backendrte.common.DatabaseAccessManager;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

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
public abstract class AbstractPlaywrightE2ETest implements PlaywrightUtils {

  static final int frontendServerPort = getFreePort();
  static final int backendServerPort = getFreePort();

  protected FrontendServer4Test frontendServer;

  @Override
  public String getUrlBase() {
    return "http://localhost:" + getFrontendServerPort();
  }

  public static int backendServerPort() {
    return backendServerPort;
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

  /**
   * Gets the directory to which the frontend app has been build to.
   *
   * <p>By default this directory is assumed to be {@code build/frontendGen/dist/client}
   *
   * @return The {@link File} denoting the frontendGen directory
   */
  protected File getFrontendDistDirectory() {
    return new File("build/frontendGen/dist/client");
  }

  /**
   * Starts the frontendServer with the {@link FrontendServer4Test}
   *
   * @throws IOException When an {@link IOException} occurs while starting the frontend
   */
  @BeforeAll
  protected void startFrontend() throws IOException {
    frontendServer = new FrontendServer4Test(
        getFrontendDistDirectory(),
        "http://localhost:" + getBackendServerPort(),
        getFrontendServerPort()
    );
    frontendServer.start();
  }

  /**
   * Stops the {@link #frontendServer} if it is present
   */
  @AfterAll
  protected void stopFrontend() {
    if(frontendServer != null) {
      frontendServer.stop();
    }
  }

  /**
   * Ensures that data gets wiped from the database between two test cases
   */
  @AfterEach
  protected void cleanupBackend() {
    DatabaseAccessManager.executeStatement("DROP ALL OBJECTS");
  }

}
