package gradle;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.work.InputChanges;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IncementalUMLPTaskTest {

  @Rule
  public TemporaryFolder hwcDir= new TemporaryFolder();

  @Rule
  public TemporaryFolder inputDir= new TemporaryFolder();

  @Rule
  public TemporaryFolder outputDir= new TemporaryFolder();

  @Rule
  public TemporaryFolder reportDir= new TemporaryFolder();

  public UMLPTask umlpTask;
  public File inputFile;


  @Before
  public void setupTest() throws IOException {
    Project project = ProjectBuilder.builder().build();
    umlpTask = project.getTasks().create("TEST" + UUID.randomUUID(), gradle.UMLPTask.class);
    umlpTask.getHwcDir().set(hwcDir.getRoot());
    umlpTask.getInputDir().set(inputDir.getRoot());
    umlpTask.getOutputDir().set(outputDir.getRoot());
    umlpTask.getReportDir().set(reportDir.getRoot());

    inputFile = inputDir.newFile("SomeInputFile.umlp");
  }

  @Test
  public void inputFileModified() {
    InputChanges ch = new MockInputChanges()
        .modified(inputFile);
    IncGenData lastRun = new IncGenData();


    assertFalse(lastRun.isUpToDate(ch));
  }

  @Test
  public void inputFileAdded() {
    InputChanges ch = new MockInputChanges()
        .added(inputFile);
    IncGenData lastRun = new IncGenData();


    assertFalse(lastRun.isUpToDate(ch));
  }

  @Test
  public void inputFileRemoved() {
    InputChanges ch = new MockInputChanges()
        .removed(inputFile);
    IncGenData lastRun = new IncGenData();

    assertFalse(lastRun.isUpToDate(ch));
  }
}