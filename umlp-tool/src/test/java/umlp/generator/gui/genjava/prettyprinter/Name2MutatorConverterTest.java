/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.prettyprint.IndentPrinter;
import org.junit.jupiter.api.Test;
import umlp.generator.gui.DomainGUITest;
import umlp.generator.gui.GUIComponentStat;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static umlp.generator.gui.TestUtils.removeSpaces;

public class Name2MutatorConverterTest extends DomainGUITest {

  @Test
  public void testAttr2SetterNameExp1() {
    ASTGUIComponentDeclaration componentDecl;
    GUIComponentStat stat = GUIComponentStat.getInstance();
    Name2MutatorConverter conv = new Name2MutatorConverter(
        new IndentPrinter(), true,
        new HashSet<>(), new HashSet<>(),
        new SharedBoolean(true)
    );

    List<ASTGUIComponent> components;

    componentDecl = processGUIModel(
        "gui/genjava/Converter.gui",
        "src/test/resources/gui"
    ).getGUIComponentDeclaration();

    components = stat.getComponents(componentDecl);

    ASTGUIComponent comp1 = components.get(0);
    ASTGUIComponent comp2 = components.get(1);
    ASTGUIComponent comp3 = components.get(2);
    ASTGUIComponent comp4 = components.get(3);
    ASTGUIComponent comp5 = components.get(4);

    String expect1 = removeSpaces("setPerson(val)");
    String result1 = removeSpaces(conv.attr2Setter((ASTNameExpression)
        comp1.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue().getExpression()
    ));

    String expect2 = removeSpaces("set$per$son(val)");
    String result2 = removeSpaces(conv.attr2Setter((ASTNameExpression)
        comp2.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue().getExpression()
    ));

    String expect3 = removeSpaces("set_per_son(val)");
    String result3 = removeSpaces(conv.attr2Setter((ASTNameExpression)
        comp3.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue().getExpression()
    ));

    String expect4 = removeSpaces("setTest1(val)");
    String result4 = removeSpaces(conv.attr2Setter((ASTNameExpression)
        comp4.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue().getExpression()
    ));

    String expect5 = removeSpaces("setTest2(val)");
    String result5 = removeSpaces(conv.attr2Setter((ASTNameExpression)
        comp5.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue().getExpression()
    ));

    assertAll(
        () -> assertEquals(expect1, result1),
        () -> assertEquals(expect2, result2),
        () -> assertEquals(expect3, result3),
        () -> assertEquals(expect4, result4),
        () -> assertEquals(expect5, result5)
    );
  }

}
