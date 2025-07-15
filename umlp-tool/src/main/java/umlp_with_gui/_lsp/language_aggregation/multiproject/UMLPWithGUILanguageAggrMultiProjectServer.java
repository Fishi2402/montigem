package umlp_with_gui._lsp.language_aggregation.multiproject;

import de.mclsg.lsp.modelpath.ModelPathCache;
import de.mclsg.lsp.modelpath.ModelPathResolver;
import de.mclsg.lsp.modelpath.multiproject.MultiProjectLSPConfigModelPathCache;
import de.mclsg.lsp.modelpath.multiproject.MultiProjectLanguageServerFileModelPathResolver;
import de.mclsg.lsp.modelpath.multiproject.MultiProjectLanguageServerGradleModelPathResolver;
import de.mclsg.lsp.modelpath.multiproject.MultiProjectLayout;
import de.mclsg.lsp.util.AsyncUtil;
import de.mclsg.lsp.util.LogRegistry;
import de.monticore.io.paths.MCPath;
import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.ShowDocumentParams;
import org.eclipse.lsp4j.SignatureHelpOptions;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class UMLPWithGUILanguageAggrMultiProjectServer extends UMLPWithGUILanguageAggrMultiProjectServerTOP {
  protected AtomicBoolean showMsgWhenModelPathIsUpdated = new AtomicBoolean(false);

  public UMLPWithGUILanguageAggrMultiProjectServer(MCPath modelPath) {
    super(modelPath);
    getWorkspaceService().registerCommand("umlpwithgui.multiproject.getProjectLayout", executeCommandParams -> multiProjectLayout);
  }

  protected void initResolver() {
    Path rootDir = Paths.get(rootUri);

    resolver = new ModelPathResolver<>(
        rootDir,
        List.of(
            new MultiProjectLanguageServerGradleModelPathResolver(rootDir) {
              {
                this.minimumJavaVersion = 11;
                this.maximumJavaVersion = 17;
              }

              @Override
              protected CompletableFuture<MultiProjectLayout> loadModelPaths() {
                gradleVersion = "7.6";
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
    resolver.addOnCacheNotFound(() -> {
      if(languageClient != null){
        showMsgWhenModelPathIsUpdated.set(true);
        languageClient.showMessage(new MessageParams(
            MessageType.Info,
            "Loading project layout via Gradle, this might take a while.\n" +
                "Future startups for the same project should be much faster")
        );
      }
    });
    resolver.addOnError(t -> {
      if(languageClient != null){
        languageClient.showMessage(new MessageParams(
            MessageType.Error,
            "Failed to load the project structure via Gradle. Check your tmp dir for logs. Msg was: "
                + t.getMessage()
        ));
      }
    });
    resolver.addPathUpdatedListener(this::updateBasedOnConfig);
    resolver.loadCachedModelPath();
    AsyncUtil.runAsync(() -> resolver.refreshModelPaths());
  }

  @Override
  protected void updateBasedOnConfig(MultiProjectLayout projects) {
    super.updateBasedOnConfig(projects);
    if(showMsgWhenModelPathIsUpdated.get()){
      showMsgWhenModelPathIsUpdated.set(false);
      if(languageClient != null){
        languageClient.showMessage(new MessageParams(
            MessageType.Info,
            "Project layout loaded via Gradle"
        ));
      }
    }
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
}
