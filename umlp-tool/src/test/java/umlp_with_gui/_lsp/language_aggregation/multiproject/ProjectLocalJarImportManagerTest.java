package umlp_with_gui._lsp.language_aggregation.multiproject;

import de.mclsg.lsp.modelpath.multiproject.MultiProjectLayout;
import de.mclsg.lsp.modelpath.multiproject.ProjectLayout;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectLocalJarImportManagerTest {
  @TempDir
  Path tempDir;

  @Test
  void copiesProjectLocalJarsAndKeepsOtherPathsUntouched() throws IOException {
    Path root = Files.createDirectories(tempDir.resolve("root"));
    Path copyDir = Files.createDirectories(tempDir.resolve("copies"));
    Path localSymbolJar = root.resolve("build/libs/model-symbols.jar");
    Path localResourceJar = root.resolve("build/libs/model-resources.jar");
    Path localModelDirJar = root.resolve("build/libs/model-dirs.jar");
    Path externalJar = tempDir.resolve("external.jar");
    Path modelDir = root.resolve("src/main/models");

    Files.createDirectories(localSymbolJar.getParent());
    Files.writeString(localSymbolJar, "local-symbol");
    Files.writeString(localResourceJar, "local-resource");
    Files.writeString(localModelDirJar, "local-model-dir");
    Files.writeString(externalJar, "external");

    ProjectLayout subproject = new ProjectLayout();
    subproject.name = "test";
    subproject.projectpath = Optional.of(root.toString());
    subproject.resources = new String[]{modelDir.toString(), localResourceJar.toString(), externalJar.toString()};
    subproject.modelDirs = new String[]{modelDir.toString(), localModelDirJar.toString(), externalJar.toString()};
    subproject.symbolPath = new String[]{localSymbolJar.toString(), externalJar.toString()};
    subproject.ignoredDirs = new String[]{"build"};

    MultiProjectLayout layout = new MultiProjectLayout();
    layout.subprojects = new ProjectLayout[]{subproject};
    layout.ignoredDirectories = new String[]{".gradle"};

    ProjectLocalJarImportManager manager = new ProjectLocalJarImportManager(copyDir);
    MultiProjectLayout result = manager.useTempCopiesForProjectLocalJars(layout, root);

    String copiedJar = result.subprojects[0].symbolPath[0];
    assertNotEquals(localSymbolJar.toString(), copiedJar);
    assertTrue(Path.of(copiedJar).startsWith(copyDir));
    assertEquals("local-symbol", Files.readString(Path.of(copiedJar)));
    assertEquals(externalJar.toString(), result.subprojects[0].symbolPath[1]);
    assertEquals(modelDir.toString(), result.subprojects[0].resources[0]);
    assertNotEquals(localResourceJar.toString(), result.subprojects[0].resources[1]);
    assertTrue(Path.of(result.subprojects[0].resources[1]).startsWith(copyDir));
    assertEquals("local-resource", Files.readString(Path.of(result.subprojects[0].resources[1])));
    assertEquals(externalJar.toString(), result.subprojects[0].resources[2]);
    assertEquals(modelDir.toString(), result.subprojects[0].modelDirs[0]);
    assertNotEquals(localModelDirJar.toString(), result.subprojects[0].modelDirs[1]);
    assertTrue(Path.of(result.subprojects[0].modelDirs[1]).startsWith(copyDir));
    assertEquals("local-model-dir", Files.readString(Path.of(result.subprojects[0].modelDirs[1])));
    assertEquals(externalJar.toString(), result.subprojects[0].modelDirs[2]);
    assertArrayEquals(subproject.ignoredDirs, result.subprojects[0].ignoredDirs);
    assertArrayEquals(layout.ignoredDirectories, result.ignoredDirectories);
  }

  @Test
  void copiesProjectLocalResourceJarsWithoutSymbolPath() throws IOException {
    Path root = Files.createDirectories(tempDir.resolve("root"));
    Path copyDir = Files.createDirectories(tempDir.resolve("copies"));
    Path localResourceJar = root.resolve("build/libs/model-resources.jar");
    Files.createDirectories(localResourceJar.getParent());
    Files.writeString(localResourceJar, "local-resource");

    ProjectLayout subproject = new ProjectLayout();
    subproject.resources = new String[]{localResourceJar.toString()};
    subproject.symbolPath = null;

    MultiProjectLayout layout = new MultiProjectLayout();
    layout.subprojects = new ProjectLayout[]{subproject};

    ProjectLocalJarImportManager manager = new ProjectLocalJarImportManager(copyDir);
    MultiProjectLayout result = manager.useTempCopiesForProjectLocalJars(layout, root);

    assertNotEquals(localResourceJar.toString(), result.subprojects[0].resources[0]);
    assertTrue(Path.of(result.subprojects[0].resources[0]).startsWith(copyDir));
    assertEquals("local-resource", Files.readString(Path.of(result.subprojects[0].resources[0])));
  }

  @Test
  void copiesProjectLocalModelDirJarsWithoutResourcesOrSymbolPath() throws IOException {
    Path root = Files.createDirectories(tempDir.resolve("root"));
    Path copyDir = Files.createDirectories(tempDir.resolve("copies"));
    Path localModelDirJar = root.resolve("build/libs/model-dirs.jar");
    Files.createDirectories(localModelDirJar.getParent());
    Files.writeString(localModelDirJar, "local-model-dir");

    ProjectLayout subproject = new ProjectLayout();
    subproject.resources = null;
    subproject.modelDirs = new String[]{localModelDirJar.toString()};
    subproject.symbolPath = null;

    MultiProjectLayout layout = new MultiProjectLayout();
    layout.subprojects = new ProjectLayout[]{subproject};

    ProjectLocalJarImportManager manager = new ProjectLocalJarImportManager(copyDir);
    MultiProjectLayout result = manager.useTempCopiesForProjectLocalJars(layout, root);

    assertNotEquals(localModelDirJar.toString(), result.subprojects[0].modelDirs[0]);
    assertTrue(Path.of(result.subprojects[0].modelDirs[0]).startsWith(copyDir));
    assertEquals("local-model-dir", Files.readString(Path.of(result.subprojects[0].modelDirs[0])));
  }

  @Test
  void overwritesTempJarWhenProjectLocalJarIsUpdated() throws IOException, InterruptedException {
    Path root = Files.createDirectories(tempDir.resolve("root"));
    Path copyDir = Files.createDirectories(tempDir.resolve("copies"));
    Path localJar = root.resolve("build/libs/model-symbols.jar");
    Files.createDirectories(localJar.getParent());
    Files.writeString(localJar, "old");

    MultiProjectLayout layout = layoutWithSymbolPath(localJar);
    ProjectLocalJarImportManager manager = new ProjectLocalJarImportManager(copyDir);

    Path firstCopy = Path.of(manager.useTempCopiesForProjectLocalJars(layout, root).subprojects[0].symbolPath[0]);
    Thread.sleep(5);
    Files.writeString(localJar, "new-content");
    Path secondCopy = Path.of(manager.useTempCopiesForProjectLocalJars(layout, root).subprojects[0].symbolPath[0]);

    assertEquals(firstCopy, secondCopy);
    assertEquals("new-content", Files.readString(secondCopy));
  }

  @Test
  void refreshChangedJarsOverwritesTempJarWithoutLayoutRefresh() throws IOException, InterruptedException {
    Path root = Files.createDirectories(tempDir.resolve("root"));
    Path copyDir = Files.createDirectories(tempDir.resolve("copies"));
    Path localJar = root.resolve("build/libs/model-symbols.jar");
    Files.createDirectories(localJar.getParent());
    Files.writeString(localJar, "old");

    ProjectLocalJarImportManager manager = new ProjectLocalJarImportManager(copyDir);
    Path copiedJar = Path.of(manager.useTempCopiesForProjectLocalJars(layoutWithSymbolPath(localJar), root).subprojects[0].symbolPath[0]);

    Thread.sleep(5);
    Files.writeString(localJar, "new-content");

    assertTrue(manager.refreshChangedJars());
    assertEquals("new-content", Files.readString(copiedJar));
    assertFalse(manager.refreshChangedJars());
  }

  @Test
  void pollingChangedJarsTriggersCallback() throws IOException, InterruptedException {
    Path root = Files.createDirectories(tempDir.resolve("root"));
    Path copyDir = Files.createDirectories(tempDir.resolve("copies"));
    Path localJar = root.resolve("build/libs/model-symbols.jar");
    AtomicInteger callbackCount = new AtomicInteger(0);
    Files.createDirectories(localJar.getParent());
    Files.writeString(localJar, "old");

    ProjectLocalJarImportManager manager = new ProjectLocalJarImportManager(copyDir, callbackCount::incrementAndGet);
    Path copiedJar = Path.of(manager.useTempCopiesForProjectLocalJars(layoutWithSymbolPath(localJar), root).subprojects[0].symbolPath[0]);

    Thread.sleep(5);
    Files.writeString(localJar, "new-content");

    long deadline = System.currentTimeMillis() + 5_000;
    while (callbackCount.get() == 0 && System.currentTimeMillis() < deadline) {
      Thread.sleep(50);
    }

    assertTrue(callbackCount.get() > 0);
    assertEquals("new-content", Files.readString(copiedJar));
    manager.close();
  }

  @Test
  void closeRemovesCreatedTempJars() throws IOException {
    Path root = Files.createDirectories(tempDir.resolve("root"));
    Path copyDir = Files.createDirectories(tempDir.resolve("copies"));
    Path localJar = root.resolve("build/libs/model-symbols.jar");
    Files.createDirectories(localJar.getParent());
    Files.writeString(localJar, "local");

    ProjectLocalJarImportManager manager = new ProjectLocalJarImportManager(copyDir);
    Path copiedJar = Path.of(manager.useTempCopiesForProjectLocalJars(layoutWithSymbolPath(localJar), root).subprojects[0].symbolPath[0]);
    assertTrue(Files.exists(copiedJar));

    manager.close();

    assertFalse(Files.exists(copyDir));
  }

  protected MultiProjectLayout layoutWithSymbolPath(Path symbolPath) {
    ProjectLayout subproject = new ProjectLayout();
    subproject.symbolPath = new String[]{symbolPath.toString()};

    MultiProjectLayout layout = new MultiProjectLayout();
    layout.subprojects = new ProjectLayout[]{subproject};
    return layout;
  }
}
