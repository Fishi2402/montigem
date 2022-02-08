/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import de.monticore.cdassociation.cocos.ebnf.CDAssociationNameLowerCase;
import de.monticore.cdbasis.cocos.ebnf.CDClassNameUpperCase;
import de.monticore.cdinterfaceandenum.cocos.ebnf.CDInterfaceExtendsNotCyclic;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;
import umlp.UMLPMill;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp._cocos.UMLPCoCoChecker;
import umlp._parser.UMLPParser;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UMLPCocoTest extends AbstractUMLPTest {
  
  private static final UMLPCoCoChecker checker = new UMLPCoCoChecker();
  
  @BeforeClass
  public static void disableFailQuick() {
    LogStub.init();
    Log.enableFailQuick(false);
    UMLPMill.reset();
    UMLPMill.init();
    checker.setTraverser(UMLPMill.traverser());
    checker.addCoCo(new CDClassNameUpperCase());
    checker.addCoCo(new CDAssociationNameLowerCase());
    checker.addCoCo(new CDInterfaceExtendsNotCyclic());
  }
  
  @Test
  public void testClassNameUpperCaseCoco() throws IOException {
    UMLPParser parser = new UMLPParser();
    Optional<ASTUMLPCompilationUnit> optAST = parser.parse("src/test/resources/cocoinvalid/CDCLASSLowerName.umlp");
    assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    assertFalse(Log.getFindings().isEmpty());
  }
  

  @Test
  public void testAssociationNameLowerCaseCoco() throws IOException {
    UMLPParser parser = new UMLPParser();
    Optional<ASTUMLPCompilationUnit> optAST = parser.parse("src/test/resources/cocoinvalid/CDAssociationNameUpperCase.umlp");
    assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    assertFalse(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testInterfaceExtendsNotCyclicCoco() throws IOException {
    UMLPParser parser = new UMLPParser();
    Optional<ASTUMLPCompilationUnit> optAST = parser.parse("src/test/resources/cocoinvalid/CDInterfaceExtendsCyclic.umlp");
    assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    try{
      checker.checkAll(optAST.get());
    } catch(IllegalStateException e){
      System.out.println(e.getMessage());
    }
    assertFalse(Log.getFindings().isEmpty());
  }

}
