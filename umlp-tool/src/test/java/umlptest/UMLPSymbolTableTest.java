/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._symboltable.UMLPSymbols2Json;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UMLPSymbolTableTest extends AbstractUMLPTest {

  @Disabled
  @Test
  public void testUseJavaTypes(){
    ASTUMLPCompilationUnit ast = parse("de/monticore/UseJavaTypes.umlp");
    IUMLPArtifactScope as = createSymTab(ast);

    Optional<OOTypeSymbol> symbol = as.getEnclosingScope().resolveOOType("java.util.List");
    assertEquals(1, as.getCDTypeSymbols().size());
    CDTypeSymbol cdSym = as.getCDTypeSymbols().values().get(0);
    System.out.println(new UMLPSymbols2Json().serialize(as));
  }

}
