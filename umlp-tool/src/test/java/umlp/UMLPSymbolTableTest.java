/* (c) https://github.com/MontiCore/monticore */
package umlp;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import org.junit.jupiter.api.Test;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._symboltable.IUMLPGlobalScope;
import umlp._symboltable.UMLPSymbols2Json;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UMLPSymbolTableTest extends AbstractUMLPTest {

  @Test
  public void testUseJavaTypes(){
    ASTCDCompilationUnit ast = parse("de/monticore/UseJavaTypes.umlp");
    IUMLPArtifactScope as = createSymTab(ast);

    Optional<OOTypeSymbol> symbol = as.getEnclosingScope().resolveOOType("java.util.List");
    assertTrue(symbol.isPresent());

    symbol = as.getEnclosingScope().resolveOOType("GUIButton");
    assertTrue(symbol.isPresent());
    assertEquals("JButton", symbol.get().getSuperClass().getTypeInfo().getName());
  }

  @Test
  public void testResolving()  {
    ASTCDCompilationUnit ast = parse("de/monticore/B.umlp");
    IUMLPArtifactScope as = createSymTab(ast);

    final Optional<CDTypeSymbol> c = as.resolveCDType("C");
    assertTrue(c.isPresent());
    assertEquals("de.monticore.B.C", c.get().getFullName());

    final Optional<CDTypeSymbol> c2 = as.resolveCDType("de.monticore.B.C");
    assertTrue(c2.isPresent());

    final Optional<FieldSymbol> i = as.resolveField("de.monticore.B.C.i");
    assertTrue(i.isPresent());

    final Optional<FieldSymbol> i2 = c2.get().getSpannedScope().resolveField("i");
    assertTrue(i2.isPresent());

    final Optional<CDTypeSymbol> d = as.resolveCDType("D");
    assertTrue(d.isPresent());

    final Optional<VariableSymbol> i3 = d.get().getSpannedScope().resolveVariable("i");
    assertTrue(i3.isPresent());

    final Optional<FieldSymbol> i4 = d.get().getSpannedScope().resolveField("i");
    assertTrue(i4.isPresent());
  }

  @Test
  public void testResolvingFromSymTab()  {
    UMLPSymbols2Json s2j = new UMLPSymbols2Json();
    IUMLPGlobalScope gs = UMLPMill.globalScope();
    IUMLPArtifactScope as = s2j.load("src/test/resources/de/monticore/B.umlpsym");
    gs.addSubScope(as);
    assertNoErrors();

    final Optional<CDTypeSymbol> c = as.resolveCDType("C");
    assertTrue(c.isPresent());
    assertEquals("de.monticore.B.C", c.get().getFullName());

    final Optional<CDTypeSymbol> c2 = as.resolveCDType("de.monticore.B.C");
    assertTrue(c2.isPresent());

    final Optional<FieldSymbol> i = as.resolveField("de.monticore.B.C.i");
    assertTrue(i.isPresent());

    final Optional<FieldSymbol> i2 = c2.get().getSpannedScope().resolveField("i");
    assertTrue(i2.isPresent());
  }
}
