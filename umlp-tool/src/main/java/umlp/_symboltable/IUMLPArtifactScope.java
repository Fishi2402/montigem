/* (c) https://github.com/MontiCore/monticore */

package umlp._symboltable;

import de.monticore.symboltable.ISymbol;

import java.util.Optional;

public interface IUMLPArtifactScope extends IUMLPArtifactScopeTOP {

  /**
   * This Method is overridden, as the super implementation would
   * consider all CD Def. symbols as top-level symbol as well.
   *
   * @return
   */
  default Optional<ISymbol> getTopLevelSymbol() {
    if (1 == getUMLPDefSymbols().values().size()) {
      return Optional.ofNullable(getUMLPDefSymbols().values().get(0));
    }
    return IUMLPArtifactScopeTOP.super.getTopLevelSymbol();
  }
}
