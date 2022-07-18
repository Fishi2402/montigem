/* (c) https://github.com/MontiCore/monticore */

package umlp.rte;

import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractUMLPRTETest {

  @BeforeEach
  public void setup() {
    Log.initDEBUG();
    Log.enableFailQuick(false);
  }

  public static void assertNoErrors() {
    if (Log.getErrorCount() > 0) {
      System.err.println("Expected no errors, but got: ");
      for (Finding f : Log.getFindings()) {
        if (f.isError()) {
          System.err.println(f.toString());
        }
      }
      fail();
    }
  }

  public static void assertErrors(String... errorCodes) {
    assertEquals(errorCodes.length, Log.getErrorCount(), "Expected and actual number of errors differ!");

    for (String expectedErrorCode : errorCodes) {
      boolean foundError = false;
      for (Finding f : Log.getFindings()) {
        if (f.isError() && f.getMsg().startsWith(expectedErrorCode)) {
          foundError = true;
          break;
        }
      }
      if (!foundError) {
        fail("Expected to find an error with the error code '" +
            expectedErrorCode + "', but the error did not occur!");
      }
    }
  }

}
