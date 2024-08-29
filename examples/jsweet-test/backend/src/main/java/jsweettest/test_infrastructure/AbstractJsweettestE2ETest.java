package jsweettest.test_infrastructure;

import java.io.File;

public class AbstractJsweettestE2ETest extends AbstractJsweettestE2ETestTOP {
  /**
   * Since this MultiProject reuses the downloaded nodejs, the expected folder is several levels higher
   * than expected.
   * Not needed in normal projects
   */
  @Override
  protected File getNodeJSDirectory() {
    return new File("../../../.gradle/nodejs");
  }
}
