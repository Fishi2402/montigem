/* (c) https://github.com/MontiCore/monticore */

package umlp._lsp.language_access;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import umlp._cocos.UMLPCoCoChecker;
import umlp._cocos.UMLPCoCos;

public class UMLPLspCoCoRunner extends UMLPLspCoCoRunnerTOP {
  UMLPCoCoChecker checker = new UMLPCoCoChecker();


  public UMLPLspCoCoRunner(DocumentManager documentManager) {
    super(documentManager);
    UMLPCoCos.addAllUMLPCoCos(checker);
    UMLPCoCos.addAllCDCoCos(checker);
  }

  @Override
  public void runAllCoCos(ASTCDCompilationUnit ast) {
    checker.checkAll(ast);
  }
}
