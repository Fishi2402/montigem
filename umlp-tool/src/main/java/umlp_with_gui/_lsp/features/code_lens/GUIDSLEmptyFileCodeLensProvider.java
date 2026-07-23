package umlp_with_gui._lsp.features.code_lens;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.guidsl._lsp.features.code_lens.GUIDSLCodeLensProvider;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.TextDocumentItem;
import umlp._lsp.features.code_lens.EmptyFileCodeLensSupport;

import java.util.ArrayList;
import java.util.List;

public class GUIDSLEmptyFileCodeLensProvider extends GUIDSLCodeLensProvider {

  public GUIDSLEmptyFileCodeLensProvider(DocumentManager documentManager) {
    super(documentManager);
  }

  @Override
  public List<? extends CodeLens> codeLens(TextDocumentItem document) {
    List<CodeLens> codeLenses = new ArrayList<>(super.codeLens(document));
    EmptyFileCodeLensSupport.create(document, ".gui", "Add basic GUI content")
        .ifPresent(codeLenses::add);
    return codeLenses;
  }
}
