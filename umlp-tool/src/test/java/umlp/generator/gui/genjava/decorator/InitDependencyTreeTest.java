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
    assertions.add(() -> assertEquals(8, root.get().getDependentList().size()));
    // 6 GUIParam with no dependency
    for (int i = 1; i < root.get().getDependentList().size() - 2; i++) {
      InitDependency id = root.get().getDependentList().get(i);
      assertions.add(() -> assertEquals(0, id.getDependentList().size()));
    }

    InitDependency personConstructor = root.get().getDependentList().get(0);

    assertions.add(() -> assertEquals(1, personConstructor.getDependentList().size()));
    assertions.add(() -> assertEquals(
        idt.getDependency(decl.getGUIParam(0).getDefault()).get(),
        personConstructor
    ));

    InitDependency person = personConstructor.getDependentList().get(0);

    assertions.add(() -> assertEquals(0, person.getDependentList().size()));
    assertions.add(() -> assertEquals(
        idt.getDependency(decl.getGUIParam(0)).get(),
        person
    ));

    InitDependency declBlock = root.get().getDependentList().get(7);
    assertions.add(() -> assertEquals(8, declBlock.getDependentList().size()));

    InitDependency cS = declBlock.getDependentList().get(0);
    InitDependency cA = declBlock.getDependentList().get(1);
    InitDependency gLgve = declBlock.getDependentList().get(2);
    InitDependency jE = declBlock.getDependentList().get(3);
    InitDependency kS = declBlock.getDependentList().get(4);
    InitDependency kM = declBlock.getDependentList().get(5);
    InitDependency l = declBlock.getDependentList().get(6);
    InitDependency o = declBlock.getDependentList().get(7);

    assertions.add(() -> assertEquals(0, cS.getDependentList().size()));
    assertions.add(() -> assertEquals(0, cA.getDependentList().size()));
    assertions.add(() -> assertEquals(0, gLgve.getDependentList().size()));
    assertions.add(() -> assertEquals(0, jE.getDependentList().size()));
    assertions.add(() -> assertEquals(0, kS.getDependentList().size()));
    assertions.add(() -> assertEquals(0, kM.getDependentList().size()));
    assertions.add(() -> assertEquals(2, l.getDependentList().size()));
    assertions.add(() -> assertEquals(
        idt.getDependency(lNode.getIterateControl()).get(),
        l
    ));
    assertions.add(() -> assertEquals(1, o.getDependentList().size()));

    InitDependency lInnerConstructor = l.getDependentList().get(0);
    InitDependency lBlock = l.getDependentList().get(1);

    assertions.add(() -> assertEquals(1, lInnerConstructor.getDependentList().size()));
    assertions.add(() -> assertEquals(
        idt.getDependency(((ASTCallExpression)
            ((ASTGUIShortIterateControl) lNode.getIterateControl()).getIterable())
            .getArguments().getExpression(0)).get(),
        lInnerConstructor
    ));

    InitDependency lConstructor = lInnerConstructor.getDependentList().get(0);

    assertions.add(() -> assertEquals(0, lConstructor.getDependentList().size()));
    assertions.add(() -> assertEquals(
        idt.getDependency(((ASTGUIShortIterateControl) lNode.getIterateControl())
            .getIterable()).get(),
        lConstructor
    ));

    assertions.add(() -> assertEquals(0, lBlock.getDependentList().size()));

    InitDependency oBlock = o.getDependentList().get(0);

    assertions.add(() -> assertEquals(1, oBlock.getDependentList().size()));

    InitDependency p = oBlock.getDependentList().get(0);

    assertions.add(() -> assertEquals(1, p.getDependentList().size()));

    InitDependency pBlock = p.getDependentList().get(0);

    assertions.add(() -> assertEquals(2, pBlock.getDependentList().size()));

    InitDependency qConstructor = pBlock.getDependentList().get(0);

    assertions.add(() -> assertEquals(1, qConstructor.getDependentList().size()));
    assertions.add(() -> assertEquals(
        idt.getDependency(qNode.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()
            .getGUIList().getGUIArgValue(0)
            .getGUIList().getGUIArgValue(2)
            .getExpression()).get(),
        qConstructor
    ));

    InitDependency qInnerList = qConstructor.getDependentList().get(0);

    assertions.add(() -> assertEquals(1, qInnerList.getDependentList().size()));
    assertions.add(() -> assertEquals(
        idt.getDependency(qNode.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()
            .getGUIList().getGUIArgValue(0)
            .getGUIList()).get(),
        qInnerList
    ));

    InitDependency qList = qInnerList.getDependentList().get(0);

    assertions.add(() -> assertEquals(0, qList.getDependentList().size()));
    assertions.add(() -> assertEquals(
        idt.getDependency(qNode.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()
            .getGUIList()).get(),
        qList
    ));

    InitDependency rList = pBlock.getDependentList().get(1);

    assertions.add(() -> assertEquals(0, rList.getDependentList().size()));

    assertAll(assertions);
  }

}
