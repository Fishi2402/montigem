/* (c) https://github.com/MontiCore/monticore */
package flensburg.gui;

// HW-T

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import hardy.flensburg.test_infrastructure.FlensburgMockTestSetup;
import umlp.test_rte.ClientSetup;

/* Setup for the GUI (or later also E2E) tests
 */
public abstract class AbstractGUITest extends FlensburgMockTestSetup {

  protected boolean logFailQuick;

  public AbstractGUITest(ClientSetup clientSetup) {
    super(clientSetup);
  }

  @Override
  public void _init() throws Exception {
    super._init();
    // GUI never fails quick
    logFailQuick = Log.isFailQuickEnabled();
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Override
  public void _cleanup() throws Exception {
    super._cleanup();
    Log.enableFailQuick(logFailQuick);
  }
}
