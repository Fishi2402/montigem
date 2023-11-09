package umlp._lsp.features.syntax_highlighting;

import de.mclsg.lsp.extensions.syntax_highlighting.lexer.Token;
import de.mclsg.lsp.features.sematic_tokens.impl.SemanticTokenTypesWrapper;
import de.mclsg.lsp.features.sematic_tokens.impl.TokenClassification;
import de.mclsg.lsp.features.sematic_tokens.impl.TokenClassificationRule;

import java.util.Optional;

public class HighlightClassKeywordRule implements TokenClassificationRule {
  @Override
  public Optional<TokenClassification> apply(Token token) {
    return Optional.of(new TokenClassification(SemanticTokenTypesWrapper.Keyword));
  }

  @Override
  public boolean matches(Token token) {
    return token.tokenPathMatches(".*[.]cDClass") && token.getName().equals("keyword");
  }
}
