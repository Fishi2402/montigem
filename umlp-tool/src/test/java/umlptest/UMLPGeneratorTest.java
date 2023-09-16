/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.jupiter.api.Test;
import umlp.generator.domain.UMLPGenerator;
import umlp.generator.domain.UMLPGeneratorConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UMLPGeneratorTest extends AbstractUMLPTest {

  @Test
  public void testGenerateMethod() {
    ASTCDCompilationUnit ast = parse("de/monticore/A.umlp");
    runAfterParseTrafo(ast);
    createSymTab(ast);
    runDefaultTrafos(ast);
    recreateSymbolTable(ast);

    UMLPGenerator gen = new UMLPGenerator(getGenSetup(), new UMLPGeneratorConfig(false));
    gen.generate(ast, "umlp.UMLP2Java");

    assertNoErrors();
  }

  @Test
  public void testTOPMechamismMethod() {
    ASTCDCompilationUnit ast = parse("de/monticore/A.umlp");
    runAfterParseTrafo(ast);
    createSymTab(ast);
    runDefaultTrafos(ast);
    recreateSymbolTable(ast);

    UMLPGenerator gen = new UMLPGenerator(getGenSetup(), new UMLPGeneratorConfig(false));
    gen.generate(ast, "umlp.UMLP2Java");

    assertNoErrors();

    File f = new File("target/generated-test-sources/a/FooTOP.java");
    assertTrue(f.exists());
  }

  @Test
  public void testGenerateCorrectGetterName() {
    ASTCDCompilationUnit ast = parse("de/monticore/DerivedAttributeGetter.umlp");
    runAfterParseTrafo(ast);
    createSymTab(ast);
    runDefaultTrafos(ast);
    recreateSymbolTable(ast);

    UMLPGenerator gen = new UMLPGenerator(getGenSetup(), new UMLPGeneratorConfig(false));
    gen.generate(ast, "umlp.UMLP2Java");

    File f1 = new File("target/generated-test-sources/derivedattributegetter/commands/Foo_calculateFirstAttribute.java");
    assertTrue(f1.exists());
    File f2 = new File("target/generated-test-sources/derivedattributegetter/commands/Bar_calculateSecondAttribute.java");
    assertTrue(f2.exists());

    try {
      String content = Files.readString(f1.toPath());
      assertTrue(content.contains("isFirstAttribute()"));
      assertFalse(content.contains("getFirstAttribute()"));
      content = Files.readString(f2.toPath());
      assertTrue(content.contains("getSecondAttribute()"));
      assertFalse(content.contains("isSecondAttribute()"));
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

}
