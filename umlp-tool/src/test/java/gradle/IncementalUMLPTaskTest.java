package gradle;

import org.gradle.api.Project;
import org.gradle.internal.impldep.org.junit.Rule;
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.work.FileChange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;

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


  @BeforeEach
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
    Stream<FileChange> ch = Stream.empty();
    IncGenData lastRun = new IncGenData();

    assertFalse(lastRun.isUpToDate(ch));
  }

  @Test
  public void inputFileAdded() {
    Stream<FileChange> ch = Stream.empty();
    IncGenData lastRun = new IncGenData();


    assertFalse(lastRun.isUpToDate(ch));
  }

  @Test
  public void inputFileRemoved() {
    Stream<FileChange> ch = Stream.empty();
    IncGenData lastRun = new IncGenData();

    assertFalse(lastRun.isUpToDate(ch));
  }
}