/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import de.se_rwth.commons.logging.Log;
import org.junit.Test;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp._cocos.UMLPCoCos;
import umlp._parser.UMLPParser;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UMLPCocoTest extends AbstractUMLPTest {
  
  @Test
  public void testClassNameUpperCaseCoco() {
    ASTUMLPCompilationUnit ast = parse("cocoinvalid/CDClassLowerName.umlp");
    createSymTab(ast);
    UMLPCoCos.checkAll(ast);
    assertErrors("0xCDC0A");
  }
  

  @Test
  public void testAssociationNameLowerCaseCoco() throws IOException {
    UMLPParser parser = new UMLPParser();
    Optional<ASTUMLPCompilationUnit> optAST = parser.parse("src/test/resources/cocoinvalid/CDAssociationNameUpperCase.umlp");
    assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    UMLPCoCos.checkAll(optAST.get());
    assertFalse(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testInterfaceExtendsNotCyclicCoco() throws IOException {
    UMLPParser parser = new UMLPParser();
    Optional<ASTUMLPCompilationUnit> optAST = parser.parse("src/test/resources/cocoinvalid/CDInterfaceExtendsCyclic.umlp");
    assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    try{
      UMLPCoCos.checkAll(optAST.get());
    } catch(IllegalStateException e){
      System.out.println(e.getMessage());
    }
    assertFalse(Log.getFindings().isEmpty());
  }

}
