/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import de.monticore.symboltable.serialization.JsonParser;
import org.junit.Test;
import umlp.UMLPMill;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._symboltable.UMLPSymbolTableCompleter;
import umlp._symboltable.UMLPSymbols2Json;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UMLPDeSerTest extends AbstractUMLPTest {

  @Test
  public void serializeA() {
    IUMLPArtifactScope as = processModel("de/monticore/A.umlp");

    String serialized = new UMLPSymbols2Json().serialize(as);
    assertNoErrors();

    assertTrue(serialized.contains("A"));
    assertTrue(serialized.contains("Foo"));
    assertTrue(serialized.contains("Bar"));
    assertTrue(serialized.contains("umlp._symboltable.UMLPDefSymbol"));
    assertTrue(serialized.contains("de.monticore.cdbasis._symboltable.CDTypeSymbol"));

    JsonParser.parse(serialized);
    assertNoErrors();
  }

  @Test
  public void deserializeB() {
    UMLPSymbols2Json s2j = new UMLPSymbols2Json();
    IUMLPArtifactScope expected = processModel("de/monticore/B.umlp");
    String serialized = s2j.serialize(expected);

    IUMLPArtifactScope actual = s2j.deserialize(serialized);

    assertEquals(expected.getSymbolsSize(), actual.getSymbolsSize());
    assertUMLPSymEquals(expected, actual);
    assertCDTypeSymEquals(expected, actual);
  }

  @Test
  public void storeLoadD() {
    String symFile = "target/symboltests/D.umlpsym";
    UMLPSymbols2Json s2j = new UMLPSymbols2Json();
    IUMLPArtifactScope expected = processModel("de/monticore/D.umlp");

    s2j.store(expected, symFile);
    assertNoErrors();
    assertTrue(new File(symFile).exists());

    IUMLPArtifactScope actual = s2j.load(symFile);
    assertNoErrors();

    assertEquals(expected.getSymbolsSize(), actual.getSymbolsSize());
    assertUMLPSymEquals(expected, actual);
    assertCDTypeSymEquals(expected, actual);

  }

  protected IUMLPArtifactScope processModel(String model) {
    ASTUMLPCompilationUnit ast = parse(model);
    runAfterParseTrafo(ast);
    IUMLPArtifactScope as = UMLPMill.scopesGenitorDelegator().createFromAST(ast);
    ast.accept(new UMLPSymbolTableCompleter(ast).getTraverser());
    return as;
  }

  private void assertUMLPSymEquals(IUMLPArtifactScope expected,
      IUMLPArtifactScope actual) {
    assertEquals(expected.getLocalUMLPDefSymbols().size(),
        actual.getLocalUMLPDefSymbols().size());

    String e = expected.getUMLPDefSymbols().values().get(0).getName();
    String a = actual.getUMLPDefSymbols().values().get(0).getName();
    assertEquals(e, a);
  }

  private void assertCDTypeSymEquals(IUMLPArtifactScope expected, IUMLPArtifactScope actual) {
    assertEquals(expected.getCDTypeSymbols().size(), actual.getCDTypeSymbols().size());

    Set<String> expNames = expected.getCDTypeSymbols().values().stream()
        .map(s -> s.getName())
        .collect(Collectors.toSet());
    Set<String> actNames = actual.getCDTypeSymbols().values().stream()
        .map(s -> s.getName())
        .collect(Collectors.toSet());
    assertEquals(expNames, actNames);
  }

}
