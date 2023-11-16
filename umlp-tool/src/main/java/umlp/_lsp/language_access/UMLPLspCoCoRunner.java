/* (c) https://github.com/MontiCore/monticore */

package umlp._lsp.language_access;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

public class UMLPLspCoCoRunner extends UMLPLspCoCoRunnerTOP {
  public UMLPLspCoCoRunner(DocumentManager documentManager) {
    super(documentManager);
  }

  @Override
  public void runAllCoCos(ASTCDCompilationUnit ast) {
    tool.runDefaultCoCos(ast);
  }
}
