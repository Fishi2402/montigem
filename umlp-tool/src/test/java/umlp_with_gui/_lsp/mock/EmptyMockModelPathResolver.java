package umlp_with_gui._lsp.mock;

import de.mclsg.lsp.modelpath.ModelPathResolver;

import java.util.function.Consumer;

public class EmptyMockModelPathResolver extends ModelPathResolver {
  public EmptyMockModelPathResolver() {
    super(null, null, null);
  }

  @Override
  public boolean addPathUpdatedListener(Consumer mcPathConsumer) {
    return false;
  }

  @Override
  public boolean removePathUpdatedListener(Consumer o) {
    return false;
  }

  @Override
  public void loadCachedModelPath() {
    // empty
  }

  @Override
  public void refreshModelPaths() {
    // empty
  }

  @Override
  public void addOnCacheNotFound(Runnable runnable) {
    // empty
  }

  @Override
  public void addOnError(Consumer error) {
    // empty
  }
}
