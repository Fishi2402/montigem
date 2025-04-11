package umlp._lsp;

import de.mclsg.SeCommonsLogParser;
import de.mclsg.lsp.document_management.DocumentInformation;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.eclipse.lsp4j.TextDocumentItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umlp.UMLPTool;
import umlp._lsp.language_access.UMLPLanguageAccess;

public class UMLPDocumentInformationProvider extends UMLPDocumentInformationProviderTOP {
	private static final Logger logger = LoggerFactory.getLogger(UMLPDocumentInformationProvider.class);

  protected UMLPTool tool = new UMLPTool(false);
  public UMLPDocumentInformationProvider(DocumentManager documentManager, SeCommonsLogParser logParser, UMLPLanguageAccess languageAccess) {
    super(documentManager, logParser, languageAccess);
  }

  @Override
  protected DocumentInformation prepareDocumentInformation(TextDocumentItem documentItem) {
    DocumentInformation res = super.prepareDocumentInformation(documentItem);

    try {
      tool.runAfterParseTrafo((ASTCDCompilationUnit) res.ast);
    }catch (Throwable e){
      logger.warn("Error while running after parse trafos", e);
    }
    return res;
  }
}
