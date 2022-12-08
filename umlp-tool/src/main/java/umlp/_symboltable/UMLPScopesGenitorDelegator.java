/* (c) https://github.com/MontiCore/monticore */
package umlp._symboltable;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symboltable.ImportStatement;

public class UMLPScopesGenitorDelegator extends UMLPScopesGenitorDelegatorTOP {

  public IUMLPArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    IUMLPArtifactScope as = super.createFromAST(rootNode);
    as.addImports(new ImportStatement("java.lang", true));
    return as;
  }

}


