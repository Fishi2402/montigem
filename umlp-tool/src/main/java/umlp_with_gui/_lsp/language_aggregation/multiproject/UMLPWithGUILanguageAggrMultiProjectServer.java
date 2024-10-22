package umlp_with_gui._lsp.language_aggregation.multiproject;

import de.mclsg.lsp.modelpath.ModelPathCache;
import de.mclsg.lsp.modelpath.ModelPathResolver;
import de.mclsg.lsp.modelpath.multiproject.MultiProjectLSPConfigModelPathCache;
import de.mclsg.lsp.modelpath.multiproject.MultiProjectLanguageServerFileModelPathResolver;
import de.mclsg.lsp.modelpath.multiproject.MultiProjectLanguageServerGradleModelPathResolver;
import de.mclsg.lsp.modelpath.multiproject.MultiProjectLayout;
import de.mclsg.lsp.util.AsyncUtil;
import de.monticore.io.paths.MCPath;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UMLPWithGUILanguageAggrMultiProjectServer extends UMLPWithGUILanguageAggrMultiProjectServerTOP {
  public UMLPWithGUILanguageAggrMultiProjectServer(MCPath modelPath) {
    super(modelPath);
  }

  protected void initResolver() {
    Path rootDir = Paths.get(rootUri);

    resolver = new ModelPathResolver<>(
        rootDir,
        List.of(
            new MultiProjectLanguageServerGradleModelPathResolver(rootDir) {
              @Override
              protected CompletableFuture<MultiProjectLayout> loadModelPaths() {
                gradleVersion = "7.6";
                gradleTaskName = ":aggregateAndProcessInfo";
                return super.loadModelPaths();
              }

              @Override
              protected Path createTempInitScript() {
                return createTempInitScript("/lsp/lsp_init.gradle");
              }
            },
            new MultiProjectLanguageServerFileModelPathResolver(rootDir)
        ),
        new MultiProjectLSPConfigModelPathCache(ModelPathCache.getDefaultCacheDir(rootDir))
    );
    resolver.addPathUpdatedListener(this::updateBasedOnConfig);
    resolver.loadCachedModelPath();
    AsyncUtil.runAsync(() -> resolver.refreshModelPaths());
  }
}
