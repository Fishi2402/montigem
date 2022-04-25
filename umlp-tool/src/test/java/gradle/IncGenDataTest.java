package gradle;

import org.gradle.work.ChangeType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class IncGenDataTest {
  protected IncGenData lastRun;

  @Rule
  public TemporaryFolder outputDir = new TemporaryFolder();

  protected File inputF = new File("models/InputFile.umlp").getCanonicalFile();
  protected File hwcF = new File("hwc/de/HWCFile.java").getCanonicalFile();

  protected File out1F = new File("target/generated-sources/Output1.java").getCanonicalFile();
  protected File out2F = new File("target/generated-sources/Output2.java").getCanonicalFile();
  protected File otherF = new File("something.txt").getCanonicalFile();


  public IncGenDataTest() throws IOException {
  }

  @Before
  public void setup(){
    File input = Paths.get("src/test/resources/gradle", "IncGenGradleCheck.txt").toFile();
    assertTrue(input.isFile());

    lastRun = new IncGenData(input);
    assertTrue(lastRun.reportExists);
  }

  @Test
  public void parseReport() throws IOException {
    {
      Set<File> genFiles = new HashSet<>();
      genFiles.add(out1F);
      genFiles.add(out2F);

      assertEquals(lastRun.generatedFiles, genFiles);
    }

    {
      Set<File> hwcFiles = new HashSet<>();
      hwcFiles.add(hwcF);

      assertEquals(lastRun.existenceCheckedFiles, hwcFiles);
    }

    {
      Set<File> inputFiles = new HashSet<>();
      inputFiles.add(inputF);

      assertEquals(lastRun.readFiles, inputFiles);
    }
  }

  @Test
  public void inputModified() {
    assertTrue(lastRun.readFiles.contains(inputF));
    assertFalse("if an input is modified, it is not UP-TO-DATE",
        lastRun.isUpToDate(
            Stream.of(new MockFileChange(inputF, ChangeType.MODIFIED))
        )
    );
  }

  @Test
  public void inputRemoved() {
    assertTrue(lastRun.readFiles.contains(inputF));
    assertFalse("if an input is removed, it is not UP-TO-DATE",
        lastRun.isUpToDate(
            Stream.of(new MockFileChange(inputF, ChangeType.REMOVED))
        )
    );
  }

  @Test
  public void inputAdded() {
    assertTrue(lastRun.readFiles.contains(inputF));
    assertFalse("if an input is added, it is not UP-TO-DATE",
        lastRun.isUpToDate(
            Stream.of(new MockFileChange(inputF, ChangeType.ADDED))
        )
    );
  }

  @Test
  public void hwcAdded() {
    assertTrue(lastRun.existenceCheckedFiles.contains(hwcF));
    assertFalse("if a hwc-file is added, it is not UP-TO-DATE",
        lastRun.isUpToDate(
            Stream.of(new MockFileChange(hwcF, ChangeType.ADDED))
        )
    );
  }

  @Test
  public void hwcRemoved() {
    assertTrue(lastRun.existenceCheckedFiles.contains(hwcF));
    assertFalse("if a hwc-file is removed, it is not UP-TO-DATE",
        lastRun.isUpToDate(
            Stream.of(new MockFileChange(hwcF, ChangeType.REMOVED))
        )
    );
  }

  @Test
  public void hwcModified() {
    assertTrue(lastRun.existenceCheckedFiles.contains(hwcF));
    assertTrue("if a hwc-file is modified, it is UP-TO-DATE!! Because HWC-Files are not read, only check if they exist",
        lastRun.isUpToDate(
            Stream.of(new MockFileChange(hwcF, ChangeType.MODIFIED))
        )
    );
  }


  @Test
  public void otherAdded() {
    assertTrue("if an other-file is added, it is UP-TO-DATE",
        lastRun.isUpToDate(
            Stream.of(new MockFileChange(otherF, ChangeType.ADDED))
        )
    );
  }

  @Test
  public void otherRemoved() {
    assertTrue("if an other-file is removed, it is UP-TO-DATE",
        lastRun.isUpToDate(
            Stream.of(new MockFileChange(otherF, ChangeType.REMOVED))
        )
    );
  }

  @Test
  public void otherModified() {
    assertTrue("if an other-file is modified, it is UP-TO-DATE",
        lastRun.isUpToDate(
            Stream.of(new MockFileChange(otherF, ChangeType.MODIFIED))
        )
    );
  }


  @Test
  public void deleteGenFiles() throws IOException {
    File tempOut1 = outputDir.newFile("SomeOutput.java");
    File tempOut2 = outputDir.newFile("MoreOutput.java");

    assertTrue(tempOut1.isFile());
    assertTrue(tempOut2.isFile());

    lastRun.generatedFiles = new HashSet<>();
    lastRun.generatedFiles.add(tempOut1);
    lastRun.generatedFiles.add(tempOut2);

    // Action
    lastRun.deleteGeneratedFiles();

    // Check
    assertFalse(tempOut1.isFile());
    assertFalse(tempOut2.isFile());
  }
}