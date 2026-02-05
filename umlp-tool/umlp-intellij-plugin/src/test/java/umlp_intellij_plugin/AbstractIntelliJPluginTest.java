package umlp_intellij_plugin;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzerSettings;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.application.ReadAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.fixtures.*;
import com.intellij.testFramework.fixtures.impl.TempDirTestFixtureImpl;
import com.redhat.devtools.lsp4ij.features.navigation.LSPGotoDeclarationHandler;
import com.redhat.devtools.lsp4ij.features.semanticTokens.viewProvider.LSPSemanticTokenPsiElement;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import umlpwithgui._lsp.plugin.UMLPWithGUILanguageServerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractIntelliJPluginTest {
  protected CodeInsightTestFixture codeInsightFixture;
  protected IdeaProjectTestFixture projectFixture;
  protected boolean inSetupStage = false;
  protected Path projectSetupDir;
  protected boolean debugLanguageServer = false;

  @BeforeEach
  void setUp() throws Exception {
    inSetupStage = true;
    projectSetupDir = Files.createTempDirectory("intellij-lsp-test");

    // Trace messages to tmp file
    Path msgLogFile = Files.createTempFile("lsp-messages", ".log");
    System.setProperty("LSP_OPTS", "-t '" + msgLogFile + "'");
    System.setProperty("LSP_JAVA_OPTS", "-DLSPLOG_LEVEL=DEBUG" + (debugLanguageServer ? " -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005" : ""));
    System.out.println("Logging lsp msgs to " + msgLogFile);

    TestFixtureBuilder<IdeaProjectTestFixture> fixtureBuilder =
        IdeaTestFixtureFactory.getFixtureFactory().createFixtureBuilder("MyTestProject");
    projectFixture = fixtureBuilder.getFixture();

    codeInsightFixture = IdeaTestFixtureFactory.getFixtureFactory()
        .createCodeInsightFixture(projectFixture, new TempDirTestFixtureImpl() {
          @Override
          protected @NotNull Path doCreateTempDirectory() {
            try {
              return Files.createDirectories(Path.of(projectFixture.getProject().getBasePath()));
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }
        });

    codeInsightFixture.setTestDataPath(projectSetupDir.toString());
    codeInsightFixture.setUp();

    // Custom setup logic
    setupUmlpProjectFiles();
  }

  @AfterEach
  void tearDown() throws Exception {
    if (codeInsightFixture != null) {
      codeInsightFixture.tearDown();
    }
    FileUtils.deleteDirectory(projectSetupDir.toFile());
  }

  protected void copyExternalFileToProject(String resourcePath, String relativeTargetPath) throws IOException {
    if (!inSetupStage) {
      throw new IllegalStateException("copyExternalFileToProject can only be used during project setup");
    }

    Path t = projectSetupDir.resolve(relativeTargetPath);
    System.out.println("Manual copying " + resourcePath + " to " + t);
    t.getParent().toFile().mkdirs();
    FileUtils.copyFile(new File(resourcePath), t.toFile());
  }

  protected PsiFile openFile(String relativePathInProject) {
    if (inSetupStage) {
      finalizeProjectSetup();
    }

    return EdtTestUtil.runInEdtAndGet(() -> {
      DaemonCodeAnalyzerSettings.getInstance().setAutoReparseDelay(0);
      return codeInsightFixture.configureByFile(relativePathInProject);
    });
  }

  protected void finalizeProjectSetup() {
    if (!inSetupStage) {
      throw new IllegalStateException("finalizeProjectSetup can only be used during project setup");
    }

    inSetupStage = false;
    Path basePath = Paths.get(codeInsightFixture.getTestDataPath());
    try (var walker = Files.walk(basePath)) {
      walker.filter(Files::isRegularFile).forEach(p -> {
        Path rp = basePath.relativize(p);
        System.out.println("Copying to project " + rp);
        codeInsightFixture.copyFileToProject(rp.toString());
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected List<HighlightInfo> getHighlights() {
    AtomicReference<List<HighlightInfo>> result = new AtomicReference<>();
    EdtTestUtil.runInEdtAndWait(() -> result.set(codeInsightFixture.doHighlighting()));
    return result.get();
  }

  protected LookupElement[] getCompletion() {
    AtomicReference<LookupElement[]> result = new AtomicReference<>();
    EdtTestUtil.runInEdtAndWait(() -> result.set(codeInsightFixture.completeBasic()));
    return result.get();
  }

  protected PsiElement getElementReferencedAtCaret() {
    AtomicReference<PsiElement> result = new AtomicReference<>();
    EdtTestUtil.runInEdtAndWait(() -> {
      result.set(codeInsightFixture.getElementAtCaret());
    });
    return result.get();
  }

  protected void waitForLsp() {
    EdtTestUtil.runInEdtAndWait(() -> {
      try {
        PlatformTestUtil.waitWithEventsDispatching(
            "LSP Initialization timed out",
            () -> UMLPWithGUILanguageServerFactory.initDone.get(),
            2 * 60 // seconds timeout
        );
      } catch (Exception e) {
        fail("Failed waiting for LSP: " + e.getMessage());
      }
    });
  }

  protected void waitForLspIndexing() {
    waitForLsp();
    EdtTestUtil.runInEdtAndWait(() -> {
      try {
        PlatformTestUtil.waitWithEventsDispatching(
            "LSP Initialization timed out",
            () -> UMLPWithGUILanguageServerFactory.finishedIndexing.contains(codeInsightFixture.getProject().getBasePath()),
            2 * 60 // seconds timeout
        );
      } catch (Exception e) {
        fail("Failed waiting for LSP: " + e.getMessage());
      }
    });
  }

  protected void setupUmlpProjectFiles() {
    Path basePath = Paths.get("src/test/montigem-template");
    if (!Files.exists(basePath)) {
      assertTrue(Files.exists(basePath), "Can not find src/test/montigem-template. " +
          "Please clone https://git.rwth-aachen.de/monticore/umlp-anwendungen/montigem-templates/montigem-template into this directory.");
    }

    try {
      Process p = Runtime.getRuntime().exec(
          new String[]{"git", "ls-files", "--others", "--exclude-standard", "--cached"},
          new String[]{},
          basePath.toFile()
      );

      try (var r = p.inputReader()) {
        r.lines().forEach(l -> {
          try {
            Path target = projectSetupDir.resolve(l.trim());
            target.getParent().toFile().mkdirs();
            File source = basePath.resolve(l.trim()).toFile();
            System.out.println("Copying " + source + " to " + target);
            FileUtils.copyFile(source, target.toFile());
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to copy project files", e);
    }
  }

  protected PsiElement[] getDeclarations() {
    getHighlights();
    PsiElement elementAtCaret = getElementReferencedAtCaret();
    assertInstanceOf(LSPSemanticTokenPsiElement.class, elementAtCaret);
    GotoDeclarationHandler handler = GotoDeclarationHandler.EP_NAME.getExtensionList().stream().filter(it -> it instanceof LSPGotoDeclarationHandler).findFirst().orElseThrow();
    int offset = ReadAction.compute(() -> codeInsightFixture.getCaretOffset());
    PsiElement[] result = handler.getGotoDeclarationTargets(elementAtCaret, offset, codeInsightFixture.getEditor());
    return result;
  }
}
