/* (c) https://github.com/MontiCore/monticore */
package umlp._symboltable;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symboltable.ImportStatement;

import java.util.stream.Collectors;

public class UMLPScopesGenitorDelegator extends UMLPScopesGenitorDelegatorTOP {

  public IUMLPArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    IUMLPArtifactScope as = super.createFromAST(rootNode);
    as.addAllImports(rootNode.getMCImportStatementList().stream()
            .map(i -> new ImportStatement(i.getQName(), i.isStar()))
            .collect(Collectors.toList()));
    if (rootNode.isPresentMCPackageDeclaration()) {
      as.setPackageName(rootNode.getMCPackageDeclaration().getMCQualifiedName().getQName());
    }
    as.addImports(new ImportStatement("java.lang", true));
    return as;
  }

}


