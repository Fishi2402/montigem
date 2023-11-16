/* (c) https://github.com/MontiCore/monticore */

package umlp._lsp.features.code_action;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cd4analysis._lsp.features.code_action.CodeActionStrategy;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.AstPrettyPrinter;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionContext;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.List;

public class UMLPCodeActionProvider extends UMLPCodeActionProviderTOP{
  private final List<CodeActionStrategy> strategies = new ArrayList<>();

  public UMLPCodeActionProvider(DocumentManager documentManager, AstPrettyPrinter<ASTCDCompilationUnit> prettyPrinter) {
    super(documentManager, prettyPrinter);
  }

  public void addCodeActionStrategy(CodeActionStrategy codeActionStrategy) {
    strategies.add(codeActionStrategy);
  }

  @Override
  public List<Either<Command, CodeAction>> codeAction(TextDocumentItem document, CodeActionContext context, Range range) {
    List<Either<Command, CodeAction>> baseActions = super.codeAction(document, context, range);

    strategies.stream()
        .flatMap(strategy -> strategy.apply(document, context, range).stream())
        .forEach(baseActions::add);

    return baseActions;
  }
}
