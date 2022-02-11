/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import org.junit.Test;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp._cocos.UMLPCoCos;

public class UMLPCocoTest extends AbstractUMLPTest {
  
  @Test
  public void testClassNameUpperCaseCoco() {
    ASTUMLPCompilationUnit ast = parse("cocoinvalid/CDClassLowerName.umlp");
    createSymTab(ast);
    UMLPCoCos.checkAll(ast);
    assertErrors("0xCDC0A");
  }
  
  
  @Test
  public void testAssociationNameLowerCaseCoco() {
    ASTUMLPCompilationUnit ast = parse("cocoinvalid/CDAssociationNameUpperCase.umlp");
    createSymTab(ast);
    UMLPCoCos.checkAll(ast);
    assertErrors("0xCDC61");
  }
  
  @Test
  public void testInterfaceExtendsNotCyclicCoco() {
    ASTUMLPCompilationUnit ast = parse("cocoinvalid/CDInterfaceExtendsCyclic.umlp");
    createSymTab(ast);
    UMLPCoCos.checkAll(ast);
    assertErrors("0xCDC32","0xCDC32");
  }
}