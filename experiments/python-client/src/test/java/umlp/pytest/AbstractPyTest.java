package umlp.pytest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umlp.test_rte.playwright.AbstractPlaywrightE2ETest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Timeout(60) // in case we don't catch an error from python
public class AbstractPyTest extends AbstractE2ETest {

  private static final Logger log = LoggerFactory.getLogger(AbstractPyTest.class);
  File workingDir;
  private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase()
      .contains("windows");

  protected Process process;
  protected BufferedWriter writer;

  @BeforeEach
  public void setup() throws Exception {
    workingDir = new File(new File("build"), "venv");
    if (!workingDir.exists()) {
      workingDir.mkdirs();
    }

    System.err.println("[Test-Init]: Initialize python venv");
    int i;
    try {
      i = new ProcessBuilder("python", "-m", "venv", "venv").directory(workingDir).inheritIO()
          .start().waitFor();
      Assertions.assertEquals(0, i, "Failed to initialize python venv");
    } catch (IOException e) {
      e.printStackTrace();
      Assumptions.abort("Failed to run python command: Is python installed?");
    }

    System.err.println("[Test-Init]: Installing pip dependencies");
    i = processBuilder(processName("pip"), "install", "pydantic==2.10.1", "websockets").start()
        .waitFor();
    Assertions.assertEquals(0, i, "Failed to install pip dependencies");

    System.err.println("[Test-Init]: Launching main entry");
    var pb = processBuilder(processName("python"), "-u", "../testmain.py",
                            "" + AbstractPlaywrightE2ETest.backendServerPort());
    pb.directory(workingDir.getAbsoluteFile().getParentFile());

    // Add build output & runtime package to the path
    pb.environment().put("PYTHONPATH", new File("build/py_out").getAbsolutePath()
        + File.pathSeparator + new File("runtime-package/src").getAbsolutePath());

    pb.redirectInput(ProcessBuilder.Redirect.PIPE);
    pb.redirectOutput(ProcessBuilder.Redirect.PIPE);

    pb.redirectErrorStream(false); // keep stderr for debug info
    pb.redirectError(ProcessBuilder.Redirect.PIPE);

    this.waitForBackend();

    this.process = pb.start();


    final LinkedList<PyExecTest.Pair> queue = new LinkedList<>();
    CompletableFuture<String>[] waitFor = new CompletableFuture[1];
    AtomicBoolean ready = new AtomicBoolean(false);

    writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

    Thread stderr = new Thread(() -> {
      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(process.getErrorStream()))) {

        String line;
        while ((line = reader.readLine()) != null) {
          System.err.println("[PYERR] " + line);
          if (line.contains("was never awaited")) {
            Assertions.fail("Await missing in last command. \n" + line);
          } else if (line.contains("Traceback (most recent call last")) {
            var fail = new Thread(() -> {
              try {
                Thread.sleep(2000); // wait 2 seconds, such that the log can be fully collected
              } catch (InterruptedException e) {
                throw new RuntimeException(e);
              }
              // Then fail
              Assertions.fail("Python script failed");
            });
            fail.setUncaughtExceptionHandler((t, e) -> {
              Assertions.fail(e);
            });
            fail.start();
          }

        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    stderr.start();

    Thread stdout = new Thread(() -> {
      List<String> buffer = new ArrayList<>();
      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(process.getInputStream()))) {

        String line;
        while ((line = reader.readLine()) != null) {
          System.out.println("[PYOUT] " + line);
          if ("---".equals(line)) {
            Assertions.assertNotNull(waitFor[0]);
            waitFor[0].complete(String.join("\n", buffer));
            waitFor[0] = null;
            buffer.clear();
            if (queue.isEmpty()) {
              ready.set(true);
            } else {
              var p = queue.pop();
              waitFor[0] = p.future;
              writer.write(p.command);
              writer.write('\n');
              writer.flush();
            }
          } else {
            buffer.add(line);
          }
          if (line.equals("sleep done")) {
            if (!queue.isEmpty()) {
              buffer.clear();
              var p = queue.pop();
              waitFor[0] = p.future;
              writer.write(p.command);
              writer.write('\n');
              writer.flush();
            } else {
              ready.set(true);
            }
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });


    stdout.start();

    this.handler = c -> {
      final String command = c.replace("\n", "###");
      CompletableFuture<String> ret = new CompletableFuture<>();
      if (ready.get()) {
        ready.set(false);
        waitFor[0] = ret;
        writer.write(command);
        writer.write('\n');
        writer.flush();
      } else {
        queue.add(new PyExecTest.Pair(command, ret));
      }
      return ret;
    };

  }

  @AfterEach
  public void destroy() throws IOException {
    // In case of a failed test setup, they may be null
    if (writer != null) {
      writer.close();
    }
    if (process != null) {
      process.destroy();
    }
  }


  protected PyExecTest.Handler handler;

  protected String processName(String name) {
    File venv = new File(workingDir, "venv");
    if (IS_WINDOWS) {
      return new File(new File(venv, "Scripts"), name + ".exe").getAbsolutePath();
    } else {
      return new File(new File(venv, "bin"), name).getAbsolutePath();
    }
  }

  protected ProcessBuilder processBuilder(String... command) {
    File venv = new File(workingDir, "venv");
    var pb = new ProcessBuilder(command);
    pb.directory(workingDir);
    pb.environment().put("PYTHONHOME", "");
    pb.environment().put("VIRTUAL_ENV", venv.getAbsolutePath());
    pb.environment().put("VIRTUAL_ENV_PROMPT", "venv");
    pb.environment().put("PATH", new File(venv, "Scripts").getAbsolutePath());
    return pb;
  }

  protected interface Handler {
    CompletableFuture<String> sendCommand(String command) throws IOException;
  }


  static class Pair {
    String command;
    CompletableFuture<String> future;

    public Pair(String command, CompletableFuture<String> future) {
      this.command = command;
      this.future = future;
    }
  }
}
