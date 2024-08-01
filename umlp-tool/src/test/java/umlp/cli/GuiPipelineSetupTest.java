package umlp.cli;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;
import umlp.generator.gui.AbstractGUITest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GuiPipelineSetupTest extends AbstractGUITest {

  @Test
  public void testCheckSameFilenames() {
    List<Path> paths = new ArrayList<>();
    paths.add(Paths.get("src/test/resources/cli/duplicated/Same.gui"));
    paths.add(Paths.get("src/test/resources/cli/duplicated/more/Same2.gui"));

    paths.add(Paths.get("src/test/resources/gui/duplicated/Same.gui"));
    paths.add(Paths.get("src/test/resources/gui/duplicated/again/Same.gui"));
    paths.add(Paths.get("src/test/resources/gui/duplicated/again/Same2.gui"));
    paths.add(Paths.get("src/test/resources/gui/duplicated/Unique.gui"));

    new GuiPipelineSetup().checkSameFilenames(paths);
    assertEquals(1, Log.getFindingsCount());
    assertTrue(Log.getFindings().get(0).isError());
    String msg = Log.getFindings().get(0).getMsg();
    // all except Unique must be reported
    for (Path p : paths) {
      if (!p.toString().contains("Unique")) {
        assertTrue(msg.contains(p.toString()));
      }
    }
  }

}
