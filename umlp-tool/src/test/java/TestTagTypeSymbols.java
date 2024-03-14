/* (c) https://github.com/MontiCore/monticore */
import de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.tagging.SimpleSymbolTagger;
import de.monticore.tagging.TagRepository;
import de.monticore.tagging.tags.TagsMill;
import de.monticore.tagging.tags._ast.ASTSimpleTag;
import de.monticore.tagging.tags._ast.ASTTagUnit;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import umlp.UMLPMill;
import umlp._symboltable.IUMLPArtifactScope;

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

    SimpleSymbolTagger sst = new SimpleSymbolTagger();

    sst.addTag(c, makeSimpleTag("testsymbol"));

   sst.addTag(m, makeSimpleTag("testmethodsymbol"));

   sst.addTag(p, makeSimpleTag("testparamsymbol"));



    // Check that the same element is tagged twice - once via the ASTNode and once via the symbol
    Assertions.assertEquals(1, sst.getTags(c).size());

    Assertions.assertEquals(1, sst.getTags(m).size());

    Assertions.assertEquals(1, sst.getTags(p).size());

  }

  protected ASTSimpleTag makeSimpleTag(String v) {
    return TagsMill.simpleTagBuilder().setName(v).build();
  }
}
