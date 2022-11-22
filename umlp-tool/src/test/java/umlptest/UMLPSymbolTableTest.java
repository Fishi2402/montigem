/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import umlp._symboltable.IUMLPArtifactScope;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// TODO: MB Die Symboltabelle wird noch nicht korrekt aufgebaut
@Disabled
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
  public void testFullName()  {
    ASTCDCompilationUnit ast = parse("de/monticore/B.umlp");
    IUMLPArtifactScope as = createSymTab(ast);

    final Optional<CDTypeSymbol> c = as.resolveCDType("C");
    assertTrue(c.isPresent());
    assertEquals("mypackage.C", c.get().getFullName());
  }

}
