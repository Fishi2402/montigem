package umlp_with_gui._lsp.language_aggregation.language_access;

import de.mclsg.lsp.util.LanguageServerContext;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import umlp.UMLPMill;
import umlp._lsp.language_access.UMLPArtifactScopeWithFindings;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._visitor.UMLPTraverser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;


public class UMLPWithGUILanguageAggrScopeManager4UMLP extends UMLPWithGUILanguageAggrScopeManager4UMLPTOP {
    public UMLPWithGUILanguageAggrScopeManager4UMLP(LanguageServerContext uMLPContext, LanguageServerContext gUIDSLContext){
      super(uMLPContext, gUIDSLContext);
    }

  @Override
  public void initGlobalScope(MCPath modelPath) {
    super.initGlobalScope(modelPath);

    syncAccessGlobalScope(globalScope -> {
      BasicSymbolsMill.initializePrimitives();

      BuiltInTypes.setUpCollectionTypes(globalScope);

      final OOClass2MCResolver resolver = new OOClass2MCResolver(){
        @Override
        public List<OOTypeSymbol> resolveAdaptedOOTypeSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<OOTypeSymbol> predicate) {
          try (LanguageServerContext c = uMLPContext.open()) {
            return super.resolveAdaptedOOTypeSymbol(foundSymbols, name, modifier, predicate);
          }
        }

        @Override
        public List<OOTypeSymbol> resolveAdaptedInnerOOTypeSymbol(String name, IOOSymbolsScope parentScope) {
          try (LanguageServerContext c = uMLPContext.open()) {
            return super.resolveAdaptedInnerOOTypeSymbol(name, parentScope);
          }
        }

        @Override
        public List<TypeSymbol> resolveAdaptedTypeSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<TypeSymbol> predicate) {
          try (LanguageServerContext c = uMLPContext.open()) {
            return super.resolveAdaptedTypeSymbol(foundSymbols, name, modifier, predicate);
          }
        }

        @Override
        public List<TypeSymbol> resolveAdaptedInnerTypeSymbol(String name, IBasicSymbolsScope parentScope) {
          try (LanguageServerContext c = uMLPContext.open()) {
            return super.resolveAdaptedInnerTypeSymbol(name, parentScope);
          }
        }
      };
      globalScope.addAdaptedTypeSymbolResolver((boolean foundSymbols, String name,
                                                AccessModifier modifier,
                                                Predicate<TypeSymbol> predicate) -> new ArrayList<>(
          resolver.resolveAdaptedOOTypeSymbol(foundSymbols, name, modifier, predicate::test)));
      globalScope.addAdaptedOOTypeSymbolResolver((boolean foundSymbols, String name,
                                                  AccessModifier modifier,
                                                  Predicate<OOTypeSymbol> predicate) -> new ArrayList<>(
          resolver.resolveAdaptedOOTypeSymbol(foundSymbols, name, modifier, predicate::test)));
    });
  }

  @Override
  public void clearGlobalScope() {
    super.clearGlobalScope();
  }

  @Override
  public UMLPArtifactScopeWithFindings createArtifactScope(ASTCDCompilationUnit ast, IUMLPArtifactScope oldArtifactScope) {
    UMLPArtifactScopeWithFindings res = super.createArtifactScope(ast, oldArtifactScope);
    adaptAttributeVisibility(res.originalAst);
    return res;
  }

  @Override
  public Map<ASTCDCompilationUnit, UMLPArtifactScopeWithFindings> createAllArtifactScopes(Collection<ASTCDCompilationUnit> astNodes) {
    Map<ASTCDCompilationUnit, UMLPArtifactScopeWithFindings> res = super.createAllArtifactScopes(astNodes);
    for (ASTCDCompilationUnit cd : res.keySet()) {
      adaptAttributeVisibility(cd);
    }
    return res;
  }

  protected void adaptAttributeVisibility(ASTCDCompilationUnit cd){
    UMLPTraverser t = UMLPMill.traverser();
    t.add4CDBasis(new CDBasisVisitor2() {
      @Override
      public void visit(ASTCDAttribute node) {
        if (node.isPresentSymbol()) {
          FieldSymbol s = node.getSymbol();
          s.setIsPublic(true);
        }
      }
    });

    cd.accept(t);
  }
}
