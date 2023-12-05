/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.tagging.tags._ast.ASTSimpleTag;
import de.monticore.tagging.tags._ast.ASTTag;
import org.jetbrains.annotations.NotNull;
import umlp._tagging.UMLPTagger;

public class SingletonLookUp {

  private final static String SINGLETON = "singleton";

  // Consider caching this within the taggers?
  protected static LoadingCache<TypeSymbol, Boolean> isSingletonCacheSymbol = CacheBuilder.newBuilder()
      .build(new CacheLoader<>() {
        @Override
        public @NotNull Boolean load(@NotNull TypeSymbol key) {
          return _isSingleton(key);
        }
      });

  protected static LoadingCache<ASTCDClass, Boolean> isSingletonCacheAST = CacheBuilder.newBuilder()
      .build(new CacheLoader<>() {
        @Override
        public @NotNull Boolean load(@NotNull ASTCDClass key) {
          return _isSingleton(key);
        }
      });



  public static boolean isSingleton(TypeSymbol typeSymbol) {
    return isSingletonCacheSymbol.getUnchecked(typeSymbol);
  }

  public static boolean isSingleton(ASTCDClass astcdClass) {
    return isSingletonCacheAST.getUnchecked(astcdClass);
  }


  protected static Boolean _isSingleton(TypeSymbol typeSymbol) {
    return UMLPTagger.getInstance().getTags(typeSymbol).stream().anyMatch(SingletonLookUp::isSingleton);
  }

  protected static boolean _isSingleton(ASTCDClass astcdClass) {
    return astcdClass.getModifier().isPresentStereotype() && astcdClass.getModifier().getStereotype().contains(SINGLETON)
        || UMLPTagger.getInstance().getTags(astcdClass).stream().anyMatch(SingletonLookUp::isSingleton);
  }

  // This would be nice to have generated from a TagSchema
  protected static boolean isSingleton(ASTTag tag){
    if (tag instanceof ASTSimpleTag)
      return ((ASTSimpleTag) tag).getName().equals(SINGLETON);
    return false;
  }

}
