package umlp_with_gui._lsp.language_aggregation;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._cocos.GUIDSLCoCoChecker;
import de.monticore.guidsl._lsp.language_access.GUIDSLLspCoCoRunner;
import umlp.generator.gui.coco.GUIDSLCoCosForUMLP;

public class GUIDSLLspCoCo4UMLPRunner extends GUIDSLLspCoCoRunner {
  protected GUIDSLCoCoChecker checker = new GUIDSLCoCosForUMLP().createChecker();

  public GUIDSLLspCoCo4UMLPRunner(DocumentManager documentManager) {
    super(documentManager);
  }

  @Override
  public void runAllCoCos(ASTGUICompilationUnit ast) {
    checker.checkAll(ast);
  }
}
