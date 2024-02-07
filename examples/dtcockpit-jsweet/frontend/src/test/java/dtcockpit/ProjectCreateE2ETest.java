package dtcockpit;

import dtcockpit.test_infrastructure.DtcockpitMockTestSetup;
import gui.project.ProjectCreate;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectCreateE2ETest extends DtcockpitMockTestSetup {

  public ProjectCreateE2ETest() {
    super(1);
  }

  @Test
  public void testCreate() {
    String title = "myTitle";
    String git = "myGit.git";

    getClients(0).activate();

    final ProjectCreate pageUnderTest = new ProjectCreate();
    pageUnderTest.setCreatePTitleEntry(title);
    pageUnderTest.setCreatePGitEntry(git);
    //TODO createProject vs gemButton3LeftClick?
    //TODO what kind of event is expected?
    pageUnderTest.createProject(null);

    //project should be present in backend
    getBackend().activate();
    //TODO better way to get project by name?
    final Optional<Project> optProject = dtcockpit.DtcockpitManager.getProjectList().stream().filter(p -> p.getName().equals(title)).findAny();
    assertTrue(optProject.isPresent(), "project has not been created");

    final Project project = optProject.get();

    assertEquals(title, project.getName()); //technically unnecessary
    assertEquals(git, project.getGit());
  }
}
