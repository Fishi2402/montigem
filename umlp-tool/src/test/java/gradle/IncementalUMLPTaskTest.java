package gradle;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.work.FileChange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class IncementalUMLPTaskTest {

  @TempDir
  public File hwcDir;

  @TempDir
  public File inputDir;

  @TempDir
  public File outputDir;

  @TempDir
  public File reportDir;

  public UMLPTask umlpTask;
  public File inputFile;


  @BeforeEach
  public void setupTest() throws IOException {
    Project project = ProjectBuilder.builder().build();
    umlpTask = project.getTasks().create("TEST" + UUID.randomUUID(), gradle.UMLPTask.class);
    umlpTask.getHwcDir().set(hwcDir);
    umlpTask.getInput().from(inputDir);
    umlpTask.getOutputDir().set(outputDir);
    umlpTask.getReportDir().set(reportDir);

//    inputFile = inputDir.newFile("SomeInputFile.umlp");
    inputFile = new File(inputDir, "SomeInputFile.umlp");
    inputFile.createNewFile();
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