package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.prettyprint.IndentPrinter;
import org.junit.jupiter.api.Test;
import umlp.generator.gui.GUIComponentStat;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static umlp.generator.gui.TestUtils.removeSpaces;

public class FieldAccess2AccessorConverterTest extends DomainGUITest {

  @Test
  public void testAttr2GetterFieldExp1() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();
    FieldAccess2AccessorConverter conv = new FieldAccess2AccessorConverter(
        new IndentPrinter(), true, new HashSet<>()
    );

    List<ASTGUIComponent> components;

    componentDecl = processGUIModel("gui/genjava/Converter.gui", "src/test/resources/gui");

    components = stat.getComponents(componentDecl);

    ASTGUIComponent comp1 = components.get(5);
    ASTGUIComponent comp2 = components.get(6);
    ASTGUIComponent comp3 = components.get(7);

    String expect1 = removeSpaces("getName()");
    String result1 = removeSpaces(conv.attr2Getter((ASTFieldAccessExpression)
        comp1.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue().getExpression()
    ));

    String expect2 = removeSpaces("isEmployed()");
    String result2 = removeSpaces(conv.attr2Getter((ASTFieldAccessExpression)
        comp2.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue().getExpression()
    ));

    String expect3 = removeSpaces("MARRIED");
    String result3 = removeSpaces(conv.attr2Getter((ASTFieldAccessExpression)
        comp3.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue().getExpression()
    ));

    assertAll(
        () -> assertEquals(expect1, result1),
        () -> assertEquals(expect2, result2),
        () -> assertEquals(expect3, result3)
    );
  }

}
