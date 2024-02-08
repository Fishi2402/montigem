package gui.project;

import dtcockpit.Project;
import dtcockpit.test_infrastructure.DtcockpitMockTestSetup;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectCreateE2ETest extends DtcockpitMockTestSetup {

  public ProjectCreateE2ETest() {
    super(1);
  }

  @Test
  public void testCreate() {
    String title = "myTitle";
    String git = "myGit.git";

    getClients(0).activate();

    ProjectCreate pageUnderTest = new ProjectCreate();
    pageUnderTest.setCreatePTitleEntry(title);
    pageUnderTest.setCreatePGitEntry(git);
    pageUnderTest.gemButton3LeftClick(null);

    //project should be present in backend
    getBackend().activate();

    Optional<Project> optProject = getProjectByName(title);
    assertTrue(optProject.isPresent(), "project has not been created");

    Project project = optProject.get();

    assertEquals(title, project.getName()); //technically unnecessary
    assertEquals(git, project.getGit());
  }

  private Optional<dtcockpit.Project> getProjectByName(String title) {
    return dtcockpit.DtcockpitManager
        .getProjectList()
        .stream()
        .filter(p -> p.getName().equals(title))
        .findAny();
  }

  @Test
  public void testAbort() {
    String title = "myTitle";
    String git = "myGit.git";

    getClients(0).activate();
    assertTrue(getProjectByName(title).isEmpty(), "project already exists");

    ProjectCreate pageUnderTest = new ProjectCreate();
    pageUnderTest.setCreatePTitleEntry(title);
    pageUnderTest.setCreatePGitEntry(git);

    pageUnderTest.gemButton4LeftClick(null);

    assertTrue(getProjectByName(title).isEmpty(), "project has been created");
  }
}
