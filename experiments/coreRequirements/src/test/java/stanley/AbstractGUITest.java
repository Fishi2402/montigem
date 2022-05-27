package stanley;

// HW-T

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.After;
import org.junit.Before;

/* Setup for the GUI (or later also E2E) tests
 */
public abstract class AbstractGUITest {

  protected boolean logFailQuick;

  // GUI never fails quick
  @Before
  public void setup() {
    logFailQuick = Log.isFailQuickEnabled();
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @After
  public void reset() {
    Log.enableFailQuick(logFailQuick);
  }

}
