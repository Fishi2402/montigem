package oclwitherrormessages._symboltable;

import java.util.*;
import java.util.function.*;

import de.monticore.ocl.ocl.OCLMill;
import de.monticore.ocl.ocl._ast.ASTOCLCompilationUnit;
import de.monticore.ocl.ocl._symboltable.OCLInvariantSymbol;
import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.logging.Log;
import com.google.common.base.Preconditions;
import oclwitherrormessages.OCLWithErrorMessagesMill;
import oclwitherrormessages._ast.ASTOCLInvariant;

import static de.monticore.ocl.ocl._symboltable.OCLSymbolTableHelper.getImportStatements;


public class OCLWithErrorMessagesScopesGenitor extends OCLWithErrorMessagesScopesGenitorTOP {

  @Override
  public IOCLWithErrorMessagesArtifactScope createFromAST(ASTOCLCompilationUnit node) {
    Preconditions.checkNotNull(
        node,
        "0xA7004x47781 Error by creating of the OCLWithErrorMessagesScopesGenitor symbol table: top ast node is null");
    IOCLWithErrorMessagesArtifactScope artifactScope = OCLWithErrorMessagesMill.artifactScope();
    artifactScope.setPackageName(node.getPackage());
    List<ImportStatement> imports = getImportStatements(node.getMCImportStatementList());
    artifactScope.setImportsList(imports);
    artifactScope.setName(node.getOCLArtifact().getName());

    putOnStack(artifactScope);
    node.accept(getTraverser());
    return artifactScope;
  }

  @Override
  public void visit(final ASTOCLInvariant node) {
    if (!getCurrentScope().isPresent()) {
      Log.debug(
          String.format(
              "%s: Visiting %s, missing scope on scope stack.",
              node.get_SourcePositionStart(), node.getClass()),
          "ScopesGenitor");
      return;
    }
    // link the ast with its enclosing scope
    node.setEnclosingScope(getCurrentScope().get());
    // create the spanned scope
    IOCLWithErrorMessagesScope scope = createScope(false);
    // link the ast with the spanned scope
    scope.setAstNode(node);
    node.setSpannedScope(scope);

    if (node.isPresentName()) {
      // create the symbol
      OCLInvariantSymbol symbol =
          OCLMill.oCLInvariantSymbolBuilder().setName(node.getName()).build();
      // link the symbol with its enclosing scope
      getCurrentScope().get().add(symbol);
      symbol.setEnclosingScope(getCurrentScope().get());
      // link the symbol with its ast
      symbol.setAstNode(node);
      node.setSymbol(symbol);
      // link the symbol with the spanned scope
      scope.setSpanningSymbol(symbol);
      symbol.setSpannedScope(scope);
    }
    putOnStack(scope);
  }

  @Override
  public void endVisit(ASTOCLInvariant inv) {
    removeCurrentScope();
    initScopeHP2(inv.getSpannedScope());
    if (inv.isPresentSymbol()) {
      initOCLInvariantHP2(inv.getSymbol());
    }
  }
}
