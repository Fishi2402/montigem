package domain.e2e;

import com.microsoft.playwright.Page;
import domain.test_infrastructure.AbstractDomainE2ETest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("SlowTest")
public class ExampleE2ETest extends AbstractDomainE2ETest {

  private UndertowFrontendHost frontendHost = new UndertowFrontendHost(
      new File("./build/frontendGen/dist/client/"),
      "http://localhost:" + getBackendServerPort(), getFrontendServerPort()
  );

  @BeforeAll
  @Override
  protected void startFrontend() throws IOException {
      try {
        System.out.println("Starting frontend on " + getFrontendServerPort() + " and backend on " + getBackendServerPort());
          frontendHost.start();
      } catch (Exception e) {
          throw new IOException(e);
      }
  }

  @AfterAll
  @Override
  protected void stopFrontend() {
      try {
        System.out.println("Stopping frontend");
          frontendHost.stop();
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
  }

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/person/Overview"); //go to page we want to test
  }

  @Test
  void testOverview(Page page) {
    assertTrue(page.getByText("Alice").first().isVisible());
  }
}
