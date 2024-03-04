/* (c) https://github.com/MontiCore/monticore */
package gradle.common;

import gradle.MockFileChange;
import org.gradle.work.ChangeType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static umlp.AbstractUMLPTest.BUILD_FOLDER;

public class IncGenDataTest {
  protected IncGenData lastRun;

  @TempDir
  public File outputDir;

  protected File inputF = new File("models/InputFile.umlp").getCanonicalFile();
  protected File hwcF = new File("hwc/de/HWCFile.java").getCanonicalFile();

  protected File out1F = new File(BUILD_FOLDER + "/generated-sources/Output1.java").getCanonicalFile();
  protected File out2F = new File(BUILD_FOLDER + "/generated-sources/Output2.java").getCanonicalFile();
  protected File otherF = new File("something.txt").getCanonicalFile();


  public IncGenDataTest() throws IOException {
  }

  @BeforeEach
  public void setup(){
    File input = Paths.get("src/test/resources/gradle", "IncGenGradleCheck.txt").toFile();
    assertTrue(input.isFile());

    lastRun = new IncGenData(input, Paths.get("").toFile());
    assertTrue(lastRun.reportExists);
  }

  @Test
  @Disabled("Problems runing in Linux")
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
    assertFalse(lastRun.isUpToDate(
                Stream.of(new MockFileChange(inputF, ChangeType.MODIFIED))
            ),
        "if an input is modified, it is not UP-TO-DATE"
        );
  }

  @Test
  public void inputRemoved() {
    assertTrue(lastRun.readFiles.contains(inputF));
    assertFalse(lastRun.isUpToDate(
                Stream.of(new MockFileChange(inputF, ChangeType.REMOVED))
            ),
        "if an input is removed, it is not UP-TO-DATE"
        );
  }

  @Test
  public void inputAdded() {
    assertTrue(lastRun.readFiles.contains(inputF));
    assertFalse(lastRun.isUpToDate(
                Stream.of(new MockFileChange(inputF, ChangeType.ADDED))
            ),
        "if an input is added, it is not UP-TO-DATE"
        );
  }

  @Test
  public void hwcAdded() {
    assertTrue(lastRun.existenceCheckedFiles.contains(hwcF));
    assertFalse(lastRun.isUpToDate(
                Stream.of(new MockFileChange(hwcF, ChangeType.ADDED))
            ),
        "if a hwc-file is added, it is not UP-TO-DATE"
        );
  }

  @Test
  public void hwcRemoved() {
    assertTrue(lastRun.existenceCheckedFiles.contains(hwcF));
    assertFalse(lastRun.isUpToDate(
                Stream.of(new MockFileChange(hwcF, ChangeType.REMOVED))
            ),
        "if a hwc-file is removed, it is not UP-TO-DATE"
        );
  }

  @Test
  public void hwcModified() {
    assertTrue(lastRun.existenceCheckedFiles.contains(hwcF));
    assertTrue(lastRun.isUpToDate(
                Stream.of(new MockFileChange(hwcF, ChangeType.MODIFIED))
            ),
        "if a hwc-file is modified, it is UP-TO-DATE!! Because HWC-Files are not read, only check if they exist"
        );
  }


  @Test
  public void otherAdded() {
    assertTrue(lastRun.isUpToDate(
                Stream.of(new MockFileChange(otherF, ChangeType.ADDED))
            ),
        "if an other-file is added, it is UP-TO-DATE"
        );
  }

  @Test
  public void otherRemoved() {
    assertTrue(lastRun.isUpToDate(
                Stream.of(new MockFileChange(otherF, ChangeType.REMOVED))
            ),
        "if an other-file is removed, it is UP-TO-DATE"
        );
  }

  @Test
  public void otherModified() {
    assertTrue(lastRun.isUpToDate(
                Stream.of(new MockFileChange(otherF, ChangeType.MODIFIED))
            ),
        "if an other-file is modified, it is UP-TO-DATE"
        );
  }


  @Test
  public void deleteGenFiles() throws IOException {
    File tempOut1 = new File(outputDir, "SomeOutput.java");
    File tempOut2 = new File(outputDir, "MoreOutput.java");
    tempOut1.createNewFile();
    tempOut2.createNewFile();

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
