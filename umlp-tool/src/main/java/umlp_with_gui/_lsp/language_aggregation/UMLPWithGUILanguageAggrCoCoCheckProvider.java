  package umlp_with_gui._lsp.language_aggregation;

import de.mclsg.lsp.document_management.DocumentManager;
import de.mclsg.lsp.util.LanguageServerContext;


public class UMLPWithGUILanguageAggrCoCoCheckProvider extends UMLPWithGUILanguageAggrCoCoCheckProviderTOP {
    public UMLPWithGUILanguageAggrCoCoCheckProvider(DocumentManager documentManager, LanguageServerContext uMLPContext, LanguageServerContext gUIDSLContext){
      super(documentManager, uMLPContext, gUIDSLContext);
      try(var c = gUIDSLContext.open()){
        gUIDSLLspCoCoRunner = new GUIDSLLspCoCo4UMLPRunner(documentManager);
      }
    }
}
