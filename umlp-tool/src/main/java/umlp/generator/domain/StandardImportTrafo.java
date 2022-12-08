package umlp.generator.domain;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;

import java.util.List;

public class StandardImportTrafo implements CDBasisVisitor2 {
  protected CD4CodeTraverser traverser;

  public StandardImportTrafo(CD4CodeTraverser traverser) {
    this.traverser = traverser;
    this.traverser.add4CDBasis(this);
  }

  @Override
  public void visit(ASTCDCompilationUnit node) {
    node.addMCImportStatement(
        CD4CodeMill.mCImportStatementBuilder()
            .setMCQualifiedName(CD4CodeMill.mCQualifiedNameBuilder().setPartsList(List.of("java", "lang")).build())
            .setStar(true)
            .build()
    );
  }

  public void transform(ASTCDCompilationUnit ast) {
    ast.accept(this.traverser);
  }
}
