package domain;

import domain.commands.ParseMyCompilationUnit;
import domain.test_infrastructure.DomainMockTestSetup;
import mydsl.mydsl.MyDSLMill;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import umlp.test_rte.ClientSetup;

public class DomainParseTest extends DomainMockTestSetup {
  public DomainParseTest() {
    super(ClientSetup.of(1));
  }

  @Test
  public void testDSL2CD() {
    MyDSLMill.init();

    // Test that we can parse a valid model
    var r = (new ParseMyCompilationUnit("package de.mc.test; unit MyTestModel {  initial state StartState;   }")).doAction();

    Assertions.assertTrue(r.isSuccessful());
    Assertions.assertTrue(r.get().isPresentAst());
    var ast = r.get().getAst();
    // and then have our workaround for List<String> work
    Assertions.assertEquals("de<sep>mc<sep>test", ast.getMCPackageDeclaration().getMCQualifiedName().getParts());
  }

  @Test
  public void testDSLError() {
    MyDSLMill.init();

    // Test that incorrect models
    var r = (new ParseMyCompilationUnit("Syntax Error Incomming")).doAction();

    // run the command
    Assertions.assertTrue(r.isSuccessful());
    // but report their errors
    Assertions.assertFalse(r.get().isPresentAst());
    Assertions.assertEquals(1, r.get().getErrorsList().size());
  }
}
