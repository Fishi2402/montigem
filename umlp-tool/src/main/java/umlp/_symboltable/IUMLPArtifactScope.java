/* (c) https://github.com/MontiCore/monticore */

package umlp._symboltable;

import de.monticore.symboltable.ISymbol;

import java.util.Optional;

public interface IUMLPArtifactScope extends IUMLPArtifactScopeTOP {

  default Optional<ISymbol> getTopLevelSymbol() {
    if (1 == getUMLPDefSymbols().values().size()) {
      return Optional.ofNullable(getUMLPDefSymbols().values().get(0));
    }
    return Optional.empty();
  }
}
