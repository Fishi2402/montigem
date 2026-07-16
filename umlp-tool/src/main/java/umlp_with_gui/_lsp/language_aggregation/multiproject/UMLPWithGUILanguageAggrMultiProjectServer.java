package umlp_with_gui._lsp.language_aggregation.multiproject;

import de.mclsg.lsp.modelpath.ModelPathCache;
import de.mclsg.lsp.modelpath.ModelPathResolver;
import de.mclsg.lsp.modelpath.multiproject.MultiProjectLSPConfigModelPathCache;
import de.mclsg.lsp.modelpath.multiproject.MultiProjectLanguageServerGradleModelPathResolver;
import de.mclsg.lsp.modelpath.multiproject.MultiProjectLayout;
import de.monticore.io.paths.MCPath;
import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.SignatureHelpOptions;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class UMLPWithGUILanguageAggrMultiProjectServer extends UMLPWithGUILanguageAggrMultiProjectServerTOP {
  protected final ProjectLocalJarImportManager projectLocalJarImportManager = new ProjectLocalJarImportManager(this::refreshAfterTrackedJarUpdate);
  protected final AtomicBoolean jarTriggeredRefreshRunning = new AtomicBoolean(false);
  protected MultiProjectLayout latestGradleLayout;

  public UMLPWithGUILanguageAggrMultiProjectServer(MCPath modelPath) {
    super(modelPath);
    getWorkspaceService().registerCommand(
        "umlpwithgui.multiproject.getProjectLayout",
        executeCommandParams -> multiProjectLayout == null ? "{}" : multiProjectLayout.toJson()
    );
  }

  protected void initResolver() {
    Path rootDir = Paths.get(rootUri);

    resolver = new ModelPathResolver<>(
        rootDir,
        List.of(
            new MultiProjectLanguageServerGradleModelPathResolver(rootDir) {
              {
                this.minimumJavaVersion = 17;
                this.maximumJavaVersion = 21;
              }

              @Override
              protected CompletableFuture<MultiProjectLayout> loadModelPaths() {
                gradleVersion = "8.14.4";
                gradleTaskName = "aggregateAndProcessInfo";
                return super.loadModelPaths();
              }

              @Override
              protected Path createTempInitScript() {
                return createTempInitScript("/lsp/lsp_init.gradle", DEFAULT_DATA_STRUCTURE_RESOURCE);
              }
            }
        ),
        new MultiProjectLSPConfigModelPathCache(ModelPathCache.getDefaultCacheDir(rootDir))
    );
  }

  @Override
  public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
    try {
      InitializeResult res = super.initialize(initializeParams).get();
      res.getCapabilities().setSignatureHelpProvider(
          new SignatureHelpOptions(List.of("("), List.of(","))
      );
      res.getCapabilities().setCompletionProvider(new CompletionOptions(false, List.of("@")));
      return CompletableFuture.completedFuture(res);
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void updateBasedOnConfig(MultiProjectLayout projects) {
    latestGradleLayout = projects;
    applyProjectLayoutWithTempJarCopies(projects);
  }

  protected void refreshAfterTrackedJarUpdate() {
    if (latestGradleLayout == null || !jarTriggeredRefreshRunning.compareAndSet(false, true)) {
      return;
    }

    try {
      applyProjectLayoutWithTempJarCopies(latestGradleLayout);
    }
    finally {
      jarTriggeredRefreshRunning.set(false);
    }
  }

  protected void applyProjectLayoutWithTempJarCopies(MultiProjectLayout projects) {
    super.updateBasedOnConfig(projectLocalJarImportManager.useTempCopiesForProjectLocalJars(projects, Path.of(rootUri)));
  }

  @Override
  public CompletableFuture<Object> shutdown() {
    projectLocalJarImportManager.close();
    return super.shutdown();
  }

  @Override
  public void exit() {
    projectLocalJarImportManager.close();
    super.exit();
  }
}
