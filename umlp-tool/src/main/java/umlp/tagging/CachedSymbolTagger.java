/* (c) https://github.com/MontiCore/monticore */
package umlp.tagging;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.monticore.symboltable.ISymbol;
import de.monticore.tagging.SimpleSymbolTagger;
import de.monticore.tagging.tags._ast.ASTTag;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CachedSymbolTagger extends SimpleSymbolTagger {

  protected static Cache<ISymbol, List<ASTTag>> tagsCache = CacheBuilder.newBuilder().build();

  @Override
  @Nonnull
  public List<ASTTag> getTags(@Nonnull ISymbol symbol) {
    try {
      return tagsCache.get(symbol, () -> super.getTags(symbol));
    }
    catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
}
