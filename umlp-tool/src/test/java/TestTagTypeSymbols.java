/* (c) https://github.com/MontiCore/monticore */
import de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols._tagging.BasicSymbolsTagger;
import de.monticore.symboltable.IScope;
import de.monticore.tagging.TagRepository;
import de.monticore.tagging.tags.TagsMill;
import de.monticore.tagging.tags._ast.ASTSimpleTag;
import de.monticore.tagging.tags._ast.ASTTagUnit;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import umlp.UMLPMill;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._tagging.UMLPTagger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TestTagTypeSymbols {
  @Test
  public void test1() throws Exception {
    Log.init();
    Log.enableFailQuick(false);
    TagsMill.init(); // init tags mill before UMLP
    UMLPMill.init();

    Optional<ASTCDCompilationUnit> opt = UMLPMill.parser().parse_String("umlp Test { class C { void sendIssueMail(int p); } }");
    Assertions.assertTrue(opt.isPresent());

    IUMLPArtifactScope scope = UMLPMill.scopesGenitorDelegator().createFromAST(opt.get());
    ASTTagUnit tags = TagRepository.loadTempTagModel("Test").get();

    CDTypeSymbol c = scope.resolveCDType("Test.C").get();
    CDMethodSignatureSymbol m = scope.resolveCDMethodSignature("Test.C.sendIssueMail").get();
    VariableSymbol p = scope.resolveVariable("Test.C.sendIssueMail.p").get();

    UMLPTagger.getInstance().addTag(c.getAstNode(), tags, makeSimpleTag("testast"));
    ((BasicSymbolsTagger) BasicSymbolsTagger.getInstance()).addTag(c, tags, makeSimpleTag("testsymbol"));

    UMLPTagger.getInstance().addTag(m.getAstNode(), tags, makeSimpleTag("testmethodast"));
    ((BasicSymbolsTagger) BasicSymbolsTagger.getInstance()).addTag(m, tags, makeSimpleTag("testmethodsymbol"));

    // TODO: Add symbol inheritance borked => uses AST addAST as of now
//    UMLPTagger.getInstance().addTag((ASTCDParameter) p.getAstNode(), tags, makeSimpleTag("testparamast"));
    ((BasicSymbolsTagger) BasicSymbolsTagger.getInstance()).addTag(p, tags, makeSimpleTag("testparamsymbol"));


    BasicSymbolsTagger v2 = new BasicSymbolsTagger() {
      @Override
      protected List<String> getScopeDifferences(IScope scope, IScope target) {
        System.err.println("diff from " + scope + " to " + target);
        List<String> scopeStack = new ArrayList<>();
        do {
          scopeStack.add(0, scope.getName());
          scope = scope.getEnclosingScope();
          if (!scope.isPresentName()) {
            break;
          }
        } while ((scope != target.getEnclosingScope()));
        System.err.println("=>" + scopeStack);
        return scopeStack;
      }
    };

    // Check that the same element is tagged twice - once via the ASTNode and once via the symbol
    Assertions.assertEquals(2, v2.getTags(c.getAstNode(), Collections.singletonList(tags)).size());
    Assertions.assertEquals(2, v2.getTags(c, Collections.singletonList(tags)).size());

    Assertions.assertEquals(2, v2.getTags(m.getAstNode(), Collections.singletonList(tags)).size());
    Assertions.assertEquals(2, v2.getTags(m, Collections.singletonList(tags)).size());

    Assertions.assertEquals(1, v2.getTags(p.getAstNode(), Collections.singletonList(tags)).size());
    Assertions.assertEquals(1, v2.getTags(p, Collections.singletonList(tags)).size());

  }

  protected ASTSimpleTag makeSimpleTag(String v) {
    return TagsMill.simpleTagBuilder().setName(v).build();
  }
}
