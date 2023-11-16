/* (c) https://github.com/MontiCore/monticore */

package umlp._lsp.features.code_lens;

import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentManager;
import de.mclsg.lsp.features.reference.CommonReferencesProvider;
import de.monticore.cd4analysis._lsp.features.code_lens.AssociationCodeLensStrategy;

public class UMLPCodeLensProvider extends UMLPCodeLensProviderTOP{
  private final CommonReferencesProvider referencesProvider;
  private final ISymbolUsageResolutionProvider symbolUsageResolutionProvider;

  @Deprecated
  public UMLPCodeLensProvider(DocumentManager documentManager) {
    super(documentManager);
    throw new IllegalStateException();
  }

  public UMLPCodeLensProvider(DocumentManager documentManager, CommonReferencesProvider referencesProvider, ISymbolUsageResolutionProvider symbolUsageResolutionProvider) {
    super(documentManager);
    this.referencesProvider = referencesProvider;
    this.symbolUsageResolutionProvider = symbolUsageResolutionProvider;
    this.strategyManager.registerCodeLensStrategy(new AssociationCodeLensStrategy(this.referencesProvider, documentManager, this.symbolUsageResolutionProvider));
  }
}
