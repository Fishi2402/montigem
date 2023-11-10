package umlp._lsp.features.syntax_highlighting;

import de.monticore.cd4analysis._lsp.features.syntax_highlighting.rule.*;
import umlp._lsp.language_access.UMLPLanguageAccess;

public class UMLPLexerProvider extends UMLPLexerProviderTOP{
  public UMLPLexerProvider(UMLPLanguageAccess languageAccess) {
    super(languageAccess);

    addClassificationRule(new HighlightClassKeywordRule());
    addClassificationRule(new HighlightClassNameRule());
    addClassificationRule(new HighlightInterfaceNameRule());
    addClassificationRule(new HighlightAttributeTypeRule());
    addClassificationRule(new HighlightPackageNameRule());
    addClassificationRule(new HighlightEnumNameRule());
    addClassificationRule(new HighlightEnumMemberNameRule());
  }
}
