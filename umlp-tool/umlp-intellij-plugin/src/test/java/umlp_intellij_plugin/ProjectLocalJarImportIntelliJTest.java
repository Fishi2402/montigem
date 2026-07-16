package umlp_intellij_plugin;

import com.redhat.devtools.lsp4ij.LanguageServerItem;
import com.redhat.devtools.lsp4ij.LanguageServerManager;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectLocalJarImportIntelliJTest extends AbstractIntelliJPluginTest {
  protected static final String LANGUAGE_SERVER_ID = "umlpwithgui._lsp.plugin.UMLPWithGUILanguageServerFactory";
  protected static final String GET_PROJECT_LAYOUT_COMMAND = "umlpwithgui.multiproject.getProjectLayout";

  @Test
  public void testProjectLocalGradleJarIsImportedViaTempCopy() throws Exception {
    createJar(projectSetupDir.resolve("build/lsp-local-symbols.jar"), "initial");
    addLocalJarToGuiLib();

    openFile("models/Mydomain.umlp");
    waitForLspIndexing();

    Path localJar = Path.of(codeInsightFixture.getProject().getBasePath())
        .resolve("build/lsp-local-symbols.jar")
        .toAbsolutePath()
        .normalize();
    createJar(localJar, "updated");

    String projectLayout = getMultiProjectLayout();
    String originalJarPath = localJar.toString();

    assertFalse(
        projectLayout.contains(originalJarPath),
        "The language server layout still contains the project-local jar: " + originalJarPath
    );
    assertTrue(
        projectLayout.contains("umlp-lsp-symbol-jars-") && projectLayout.contains("lsp-local-symbols.jar"),
        "The language server layout does not contain a temp-copy jar. Layout: " + projectLayout
    );
  }

  protected void addLocalJarToGuiLib() throws IOException {
    Path rootBuildFile = projectSetupDir.resolve("build.gradle");
    Files.writeString(
        rootBuildFile,
        "\nproject(':frontend') {\n"
            + "  configurations.maybeCreate('guiLib')\n"
            + "  dependencies {\n"
            + "    guiLib files(rootProject.file('build/lsp-local-symbols.jar'))\n"
            + "  }\n"
            + "}\n",
        StandardCharsets.UTF_8,
        java.nio.file.StandardOpenOption.APPEND
    );
  }

  protected String getMultiProjectLayout() throws Exception {
    LanguageServerItem languageServer = LanguageServerManager.getInstance(codeInsightFixture.getProject())
        .getLanguageServer(LANGUAGE_SERVER_ID)
        .get(30, TimeUnit.SECONDS);

    Object commandResult = languageServer.getWorkspaceService()
        .executeCommand(new ExecuteCommandParams(GET_PROJECT_LAYOUT_COMMAND, List.of()))
        .get(30, TimeUnit.SECONDS);

    List<?> resultParts = assertInstanceOf(List.class, commandResult);
    assertFalse(resultParts.isEmpty(), "The project-layout command returned no result");
    return assertInstanceOf(String.class, resultParts.getFirst());
  }

  protected void createJar(Path jarPath, String content) throws IOException {
    Files.createDirectories(jarPath.getParent());
    try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath))) {
      JarEntry entry = new JarEntry("META-INF/lsp-local-symbols.txt");
      jar.putNextEntry(entry);
      jar.write(content.getBytes(StandardCharsets.UTF_8));
      jar.closeEntry();
    }
  }
}
