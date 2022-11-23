/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.jupiter.api.Test;
import umlp._cocos.UMLPCoCos;

public class UMLPCocoTest extends AbstractUMLPTest {
  
  @Test
  public void testClassNameUpperCaseCoco() {
    ASTCDCompilationUnit ast = parse("cocoinvalid/CDClassLowerName.umlp");
    createSymTab(ast);
    UMLPCoCos.checkAll(ast);
    assertErrors("0xCDC0A");
  }
  
  
  @Test
  public void testAssociationNameLowerCaseCoco() {
    ASTCDCompilationUnit ast = parse("cocoinvalid/CDAssociationNameUpperCase.umlp");
    createSymTab(ast);
    UMLPCoCos.checkAll(ast);
    assertErrors("0xCDC61");
  }
  
  @Test
  public void testInterfaceExtendsNotCyclicCoco() {
    ASTCDCompilationUnit ast = parse("cocoinvalid/CDInterfaceExtendsCyclic.umlp");
    createSymTab(ast);
    UMLPCoCos.checkAll(ast);
    assertErrors("0xCDC32","0xCDC32");
  }
}