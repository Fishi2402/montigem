/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.prettyprint.IndentPrinter;
import org.junit.jupiter.api.Test;
import umlp.generator.gui.GUIComponentStat;
import umlp.generator.gui.DomainGUITest;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static umlp.generator.gui.TestUtils.removeSpaces;

public class FieldAccess2BuilderConverterTest extends DomainGUITest {

  @Test
  public void testAttr2SetterFieldExp1() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();
    FieldAccess2BuilderConverter conv = new FieldAccess2BuilderConverter(
        new IndentPrinter(), true, new HashSet<>(), new SharedBoolean(true)
    );

    List<ASTGUIComponent> components;

    componentDecl = processGUIModel(
        "gui/genjava/Converter.gui",
        "src/test/resources/gui"
    ).getGUIComponentDeclaration();

    components = stat.getComponents(componentDecl);

    ASTGUIComponent comp1 = components.get(5);
    ASTGUIComponent comp2 = components.get(6);

    String expect1 = removeSpaces("name(val)");
    String result1 = removeSpaces(conv.attr2Setter((ASTFieldAccessExpression)
        comp1.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue().getExpression()
    ));

    String expect2 = removeSpaces("employed(val)");
    String result2 = removeSpaces(conv.attr2Setter((ASTFieldAccessExpression)
        comp2.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue().getExpression()
    ));

    assertAll(
        () -> assertEquals(expect1, result1),
        () -> assertEquals(expect2, result2)
    );
  }

}
