/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._tagging.BasicSymbolsTagger;
import de.monticore.symbols.oosymbols._tagging.OOSymbolsTagger;
import de.monticore.symboltable.IScope;
import de.monticore.tagging.TagRepository;
import de.monticore.tagging.tags._ast.ASTSimpleTag;
import de.monticore.tagging.tags._ast.ASTTag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TagLookUp {

  private final static String SINGLETON = "singleton";
  private final static String COMMAND = "command";
  private final static String DERIVED = "derived";

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

  protected static LoadingCache<CDMethodSignatureSymbol, Boolean> isCommandCacheSymbol = CacheBuilder.newBuilder()
      .build(new CacheLoader<>() {
        @Override
        public @NotNull Boolean load(@NotNull CDMethodSignatureSymbol key) {
          return _isCommand(key);
        }
      });

  protected static LoadingCache<TypeSymbol, Boolean> isDerivedCacheSymbol = CacheBuilder.newBuilder()
      .build(new CacheLoader<>() {
        @Override
        public @NotNull Boolean load(@NotNull TypeSymbol key) {
          return _isDerived(key);
        }
      });

  public static boolean isSingleton(TypeSymbol typeSymbol) {
    return isSingletonCacheSymbol.getUnchecked(typeSymbol);
  }

  public static boolean isSingleton(ASTCDClass astcdClass) {
    return isSingletonCacheAST.getUnchecked(astcdClass);
  }

  public static boolean isCommand(CDMethodSignatureSymbol methodSignatureSymbol) {
    return isCommandCacheSymbol.getUnchecked(methodSignatureSymbol);
  }

  public static boolean isDerived(TypeSymbol typeSymbol) {
    return isDerivedCacheSymbol.getUnchecked(typeSymbol);
  }

  protected static Boolean _isSingleton(TypeSymbol typeSymbol) {
    return basicSymbolsTagger.getTags(typeSymbol, TagRepository.getLoadedTagUnits()).stream().anyMatch(TagLookUp::isSingleton);
  }

  protected static boolean _isSingleton(ASTCDClass astcdClass) {
    return astcdClass.getModifier().isPresentStereotype() && astcdClass.getModifier().getStereotype().contains(SINGLETON)
        || ooSymbolsTagger.getTags(astcdClass, TagRepository.getLoadedTagUnits()).stream().anyMatch(TagLookUp::isSingleton);
  }

  protected static boolean _isCommand(CDMethodSignatureSymbol symbol) {
    return ooSymbolsTagger.getTags(symbol, TagRepository.getLoadedTagUnits()).stream().anyMatch(TagLookUp::isCommand);
  }

  public static boolean _isDerived(TypeSymbol typeSymbol) {
    return basicSymbolsTagger.getTags(typeSymbol, TagRepository.getLoadedTagUnits()).stream().anyMatch(TagLookUp::isDerived);
  }


  // This would be nice to have generated from a TagSchema
  protected static boolean isSingleton(ASTTag tag){
    if (tag instanceof ASTSimpleTag)
      return ((ASTSimpleTag) tag).getName().equals(SINGLETON);
    return false;
  }

  protected static boolean isCommand(ASTTag tag){
    if (tag instanceof ASTSimpleTag)
      return ((ASTSimpleTag) tag).getName().equals(COMMAND);
    return false;
  }

  protected static boolean isDerived(ASTTag tag){
    if (tag instanceof ASTSimpleTag)
      return ((ASTSimpleTag) tag).getName().equals(DERIVED);
    return false;
  }


  protected static OOSymbolsTagger ooSymbolsTagger = new OOSymbolsTagger(){
    @Override
    protected List<String> getScopeDifferences(IScope scope, IScope target) {
      List<String> scopeStack = new ArrayList<>();
      do {
        scopeStack.add(0, scope.getName());
        scope = scope.getEnclosingScope();
        if (!scope.isPresentName()) {
          break;
        }
      } while ((scope != target.getEnclosingScope()));
      return scopeStack;
    }
  };

  protected static BasicSymbolsTagger basicSymbolsTagger = new BasicSymbolsTagger(){
    @Override
    protected List<String> getScopeDifferences(IScope scope, IScope target) {
      List<String> scopeStack = new ArrayList<>();
      do {
        scopeStack.add(0, scope.getName());
        scope = scope.getEnclosingScope();
        if (!scope.isPresentName()) {
          break;
        }
      } while ((scope != target.getEnclosingScope()));
      return scopeStack;
    }
  };


}
