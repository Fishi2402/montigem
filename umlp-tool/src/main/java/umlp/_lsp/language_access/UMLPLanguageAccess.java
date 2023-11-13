/* (c) https://github.com/MontiCore/monticore */

package umlp._lsp.language_access;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.AstPrettyPrinter;
import umlp.UMLPMill;

import java.util.Optional;

public class UMLPLanguageAccess extends UMLPLanguageAccessTOP {
  public UMLPLanguageAccess(DocumentManager documentManager, UMLPScopeManager scopeManager) {
    super(documentManager, scopeManager);
  }

  @Override
  public Optional<AstPrettyPrinter<ASTCDCompilationUnit>> getPrettyPrinter() {
    return Optional.of(cd -> UMLPMill.prettyPrint(cd, true));
  }
}
