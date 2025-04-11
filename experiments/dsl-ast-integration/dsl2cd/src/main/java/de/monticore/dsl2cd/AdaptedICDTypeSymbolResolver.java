package de.monticore.dsl2cd;

import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDTypeSymbolResolver;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * This allows the weird-imports of reported-CDs
 */
public class AdaptedICDTypeSymbolResolver implements ICDTypeSymbolResolver {
  protected final ICD4CodeArtifactScope scope;

  public AdaptedICDTypeSymbolResolver(ICD4CodeArtifactScope scope) {
    this.scope = scope;
  }

  // Lock ourselves from stack-overflows
  boolean looking = false;

  @Override
  public List<CDTypeSymbol> resolveAdaptedCDTypeSymbol(boolean foundSymbols, String name, AccessModifier accessModifier,
                                                       Predicate<CDTypeSymbol> predicate) {
    if (looking) return new ArrayList<>();
    looking = true;
    try {
      return this.scope.resolveCDTypeDownMany(foundSymbols, name, accessModifier, predicate);
    } finally {
      looking = false;
    }
  }
}
