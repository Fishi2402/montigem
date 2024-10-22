package umlp_with_gui._lsp.mock;

import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.services.LanguageClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MockLanguageClient implements LanguageClient {
  private Map<String, PublishDiagnosticsParams> diagnostics = new HashMap<>();

  @Override
  public void telemetryEvent(Object o) {

  }

  @Override
  public void publishDiagnostics(PublishDiagnosticsParams publishDiagnosticsParams) {
    diagnostics.put(publishDiagnosticsParams.getUri(), publishDiagnosticsParams);
  }

  @Override
  public void showMessage(MessageParams messageParams) {

  }

  @Override
  public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams showMessageRequestParams) {
    return null;
  }

  @Override
  public void logMessage(MessageParams messageParams) {

  }

  public Map<String, PublishDiagnosticsParams> getDiagnostics() {
    return diagnostics;
  }

  public long countDiagnostics(){
    return getDiagnostics()
        .values()
        .stream()
        .flatMap(publishDiagnosticsParams -> publishDiagnosticsParams.getDiagnostics().stream())
        .count();
  }

  public long countErrors(){
    return getDiagnostics()
        .values()
        .stream()
        .flatMap(publishDiagnosticsParams -> publishDiagnosticsParams.getDiagnostics().stream())
        .filter(diagnostic -> DiagnosticSeverity.Error.equals(diagnostic.getSeverity()))
        .count();
  }
}
