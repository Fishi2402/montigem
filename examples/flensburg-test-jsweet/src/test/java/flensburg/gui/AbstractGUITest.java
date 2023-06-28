/* (c) https://github.com/MontiCore/monticore */
package flensburg.gui;

// HW-T

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/* Setup for the GUI (or later also E2E) tests
 */
public abstract class AbstractGUITest {

  protected boolean logFailQuick;

  // GUI never fails quick
  @BeforeEach
  public void setup() {
    logFailQuick = Log.isFailQuickEnabled();
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @AfterEach
  public void reset() {
    Log.enableFailQuick(logFailQuick);
  }

}
