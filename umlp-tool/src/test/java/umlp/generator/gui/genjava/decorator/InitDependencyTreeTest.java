/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import umlp.generator.gui.DomainGUITest;
import umlp.generator.gui.GUIComponentStat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("unused")
public class InitDependencyTreeTest extends DomainGUITest {

  @Test
  public void testInit() {
    ASTGUICompilationUnit gui;
    GUIComponentStat stat = GUIComponentStat.getInstance();

    List<ASTGUIComponent> components;
    List<ASTGUIGuard> guards;
    List<ASTGUIIterate> iterates;

    gui = processGUIModel(
        "gui/genjava/decorator/InitDependencyTree.gui",
        "src/test/resources/gui",
        "packaged/Domain.umlp"
    );
    ASTGUIComponentDeclaration decl = gui.getGUIComponentDeclaration();
    InitDependencyTree idt = InitDependencyTree.getInstance();

    idt.init(decl);

    Optional<InitDependency> root = InitDependencyTree.getInstance().getDependency(decl);
    assertTrue(root.isPresent());

    components = stat.getComponents(decl);
    guards = stat.getGuards(decl);
    iterates = stat.getIterates(decl);

    ASTGUIIterate lNode = stat.getIterById(iterates, "l");
    ASTGUIComponent qNode = stat.getCompById(components, "q");

    List<Executable> assertions = new ArrayList<>();

    assertions.add(() -> assertTrue(Log.getFindings().isEmpty()));
    // 7 GUIParam, 1 GUIBlock
    assertEquals(8, root.get().getDependentList().size());
    // 5 GUIParam with no dependency
    for (int i = 2; i < root.get().getDependentList().size() - 2; i++) {
      InitDependency id = root.get().getDependentList().get(i);
      assertions.add(() -> assertEquals(0, id.getDependentList().size()));
    }

    InitDependency personDefault = root.get().getDependentList().get(0);

    assertEquals(1, personDefault.getDependentList().size());
    assertions.add(() -> assertEquals(
        idt.getDependency(decl.getGUIParam(0).getDefault()).get(),
        personDefault
    ));

    InitDependency person = personDefault.getDependentList().get(0);

    assertEquals(0, person.getDependentList().size());
    assertions.add(() -> assertEquals(
        idt.getDependency(decl.getGUIParam(0)).get(),
        person
    ));

    InitDependency statusDefault = root.get().getDependentList().get(1);

    assertEquals(1, statusDefault.getDependentList().size());
    assertions.add(() -> assertEquals(
        idt.getDependency(decl.getGUIParam(1).getDefault()).get(),
        statusDefault
    ));

    InitDependency status = statusDefault.getDependentList().get(0);

    assertEquals(0, status.getDependentList().size());
    assertions.add(() -> assertEquals(
        idt.getDependency(decl.getGUIParam(1)).get(),
        status
    ));

    InitDependency declBlock = root.get().getDependentList().get(7);
    assertEquals(9, declBlock.getDependentList().size());

    InitDependency aA = declBlock.getDependentList().get(0);
    InitDependency cS = declBlock.getDependentList().get(1);
    InitDependency cA = declBlock.getDependentList().get(2);
    InitDependency gLgve = declBlock.getDependentList().get(3);
    InitDependency jE = declBlock.getDependentList().get(4);
    InitDependency kS = declBlock.getDependentList().get(5);
    InitDependency kM0 = declBlock.getDependentList().get(6);
    InitDependency l = declBlock.getDependentList().get(7);
    InitDependency o = declBlock.getDependentList().get(8);

    assertEquals(0, aA.getDependentList().size());
    assertEquals(0, cS.getDependentList().size());
    assertEquals(0, cA.getDependentList().size());
    assertEquals(0, gLgve.getDependentList().size());
    assertEquals(0, jE.getDependentList().size());
    assertEquals(0, kS.getDependentList().size());
    assertEquals(1, kM0.getDependentList().size());
    assertEquals(1, kM0.getDependentList().size());
    InitDependency kM1 = kM0.getDependentList().get(0);
    assertEquals(1, kM1.getDependentList().size());
    InitDependency kM = kM1.getDependentList().get(0);
    assertEquals(0, kM.getDependentList().size());
    assertEquals(2, l.getDependentList().size());
    assertions.add(() -> assertEquals(
        idt.getDependency(lNode.getIterateControl()).get(),
        l
    ));
    assertEquals(2, o.getDependentList().size());

    InitDependency lInnerConstructor = l.getDependentList().get(0);
    InitDependency lBlock = l.getDependentList().get(1);

    assertEquals(1, lInnerConstructor.getDependentList().size());
    assertions.add(() -> assertEquals(
        idt.getDependency(((ASTCallExpression)
            ((ASTGUIShortIterateControl) lNode.getIterateControl()).getIterable())
            .getArguments().getExpression(0)).get(),
        lInnerConstructor
    ));

    InitDependency lConstructor = lInnerConstructor.getDependentList().get(0);

    assertEquals(0, lConstructor.getDependentList().size());
    assertions.add(() -> assertEquals(
        idt.getDependency(((ASTGUIShortIterateControl) lNode.getIterateControl())
            .getIterable()).get(),
        lConstructor
    ));

    assertEquals(0, lBlock.getDependentList().size());

    InitDependency oIterable = o.getDependentList().get(0);

    assertEquals(0, oIterable.getDependentList().size());

    InitDependency oBlock = o.getDependentList().get(1);

    assertEquals(1, oBlock.getDependentList().size());

    InitDependency p = oBlock.getDependentList().get(0);

    assertEquals(2, p.getDependentList().size());

    InitDependency pIterable = p.getDependentList().get(0);

    assertEquals(0, pIterable.getDependentList().size());

    InitDependency pBlock = p.getDependentList().get(1);

    assertEquals(2, pBlock.getDependentList().size());

    InitDependency qConstructor = pBlock.getDependentList().get(0);

    assertEquals(1, qConstructor.getDependentList().size());
    assertions.add(() -> assertEquals(
        idt.getDependency(qNode.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()
            .getGUIList().getGUIArgValue(0)
            .getGUIList().getGUIArgValue(2)
            .getExpression()).get(),
        qConstructor
    ));

    InitDependency qInnerList = qConstructor.getDependentList().get(0);

    assertEquals(1, qInnerList.getDependentList().size());
    assertions.add(() -> assertEquals(
        idt.getDependency(qNode.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()
            .getGUIList().getGUIArgValue(0)
            .getGUIList()).get(),
        qInnerList
    ));

    InitDependency qCall = qInnerList.getDependentList().get(0);

    assertEquals(1, qCall.getDependentList().size());
    assertions.add(() -> assertEquals(
        idt.getDependency(qNode.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()
            .getGUIList().getGUIArgValue(1)
            .getExpression()).get(),
        qCall
    ));

    InitDependency qList = qCall.getDependentList().get(0);

    assertEquals(0, qList.getDependentList().size());
    assertions.add(() -> assertEquals(
        idt.getDependency(qNode.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()
            .getGUIList()).get(),
        qList
    ));

    InitDependency rCall = pBlock.getDependentList().get(1);

    assertEquals(1, rCall.getDependentList().size());

    InitDependency rList = rCall.getDependentList().get(0);

    assertEquals(0, rList.getDependentList().size());

    assertAll(assertions);
  }

}
