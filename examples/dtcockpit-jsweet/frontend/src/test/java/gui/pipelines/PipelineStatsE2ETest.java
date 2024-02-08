package gui.pipelines;

import dtcockpit.DtcockpitManager;
import dtcockpit.test_infrastructure.DtcockpitMockTestSetup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PipelineStatsE2ETest extends DtcockpitMockTestSetup {

  public PipelineStatsE2ETest() {
    super(1);
  }

  @Test
  public void testGemCardTitle() {
    // Create dummy data
    long id = 123;
    String projectName = "SomeProject";
    DtcockpitManager.projectBuilderFromId(id)
        .name(projectName)
        .git("http://example.com/git.git")
        .build();

    getClients(0).activate();
    dtcockpit.Project projectInClient = DtcockpitManager.getProjectSure(id);

    PipelineStats pageUnderTest = new PipelineStats(projectInClient);

    assertEquals("Pipeline dashboard for " + projectName, pageUnderTest.getGemCard0Title());
  }
}
