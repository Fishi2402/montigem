package umlp._lsp.features.code_lens;

import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentItem;

import java.util.List;
import java.util.Optional;

public final class EmptyFileCodeLensSupport {

  public static final String FILL_EMPTY_FILE_COMMAND = "umlp.fillEmptyFile";

  private EmptyFileCodeLensSupport() {
  }

  public static Optional<CodeLens> create(TextDocumentItem document, String extension, String title) {
    if (document == null
        || document.getUri() == null
        || !document.getUri().endsWith(extension)
        || document.getText() == null
        || !document.getText().isEmpty()) {
      return Optional.empty();
    }

    CodeLens codeLens = new CodeLens();
    codeLens.setRange(new Range(new Position(0, 0), new Position(0, 0)));
    codeLens.setCommand(new Command(title, FILL_EMPTY_FILE_COMMAND, List.of(document.getUri())));
    return Optional.of(codeLens);
  }
}
