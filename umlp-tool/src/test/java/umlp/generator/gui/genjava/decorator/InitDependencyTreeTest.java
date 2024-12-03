/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import de.monticore.ocl.setexpressions._ast.ASTSetEnumeration;
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
/*
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
    ASTGUIIterate oNode = stat.getIterById(iterates, "o");
    ASTGUIIterate pNode = stat.getIterById(iterates, "p");
    ASTGUIComponent qNode = stat.getCompById(components, "q");

    assertTrue(Log.getFindings().isEmpty());
    // first GUIParam default value
    InitDependency personDefault = root.get().getDependentList().get(0);
    assertEquals(
        idt.getDependency(decl.getGUIParam(0).getDefault()).get(),
        personDefault
    );
    InitDependency person = personDefault.getDependentList().get(0);
    assertEquals(
        idt.getDependency(decl.getGUIParam(0)).get(),
        person
    );
    InitDependency statusDefault = person.getDependentList().get(0);
    assertEquals(
        idt.getDependency(decl.getGUIParam(1).getDefault()).get(),
        statusDefault
    );
    InitDependency status = statusDefault.getDependentList().get(0);
    assertEquals(
        idt.getDependency(decl.getGUIParam(1)).get(),
        status
    );
    InitDependency lla = status.getDependentList().get(0);
    assertEquals(
        idt.getDependency(decl.getGUIParam(2)).get(),
        lla
    );
    InitDependency s = lla.getDependentList().get(0);
    assertEquals(
        idt.getDependency(decl.getGUIParam(3)).get(),
        s
    );
    InitDependency i = s.getDependentList().get(0);
    assertEquals(
        idt.getDependency(decl.getGUIParam(4)).get(),
        i
    );
    InitDependency v = i.getDependentList().get(0);
    assertEquals(
        idt.getDependency(decl.getGUIParam(5)).get(),
        v
    );
    InitDependency e = v.getDependentList().get(0);
    assertEquals(
        idt.getDependency(decl.getGUIParam(6)).get(),
        e
    );
    InitDependency declBlock = e.getDependentList().get(0);
    assertEquals(
        idt.getDependency(decl.getGUIBlock()).get(),
        declBlock
    );

    InitDependency aA = declBlock.getDependentList().get(0);
    InitDependency cS = aA.getDependentList().get(0);
    InitDependency cA = cS.getDependentList().get(0);
    InitDependency gLgve = cA.getDependentList().get(0);
    InitDependency jE = gLgve.getDependentList().get(0);
    InitDependency kS = jE.getDependentList().get(0);
    InitDependency kM0 = kS.getDependentList().get(0);
    InitDependency kM1 = kM0.getDependentList().get(0);
    InitDependency kM = kM1.getDependentList().get(0);

    InitDependency lInnerConstructor = kM.getDependentList().get(0);
    assertEquals(
        idt.getDependency(((ASTCallExpression)
            ((ASTGUIShortIterateControl) lNode.getIterateControl()).getIterable())
            .getArguments().getExpression(0)).get(),
        lInnerConstructor
    );
    InitDependency lConstructor = lInnerConstructor.getDependentList().get(0);
    assertEquals(
        idt.getDependency(((ASTGUIShortIterateControl) lNode.getIterateControl())
            .getIterable()).get(),
        lConstructor
    );
    InitDependency l = lConstructor.getDependentList().get(0);
    assertEquals(
        idt.getDependency(lNode.getIterateControl()).get(),
        l
    );
    InitDependency lBlock = l.getDependentList().get(0);
    assertEquals(
        idt.getDependency(lNode.getGUIBlock()).get(),
        lBlock
    );

    InitDependency oIterable = lBlock.getDependentList().get(0);
    InitDependency o = oIterable.getDependentList().get(0);
    InitDependency oBlock = o.getDependentList().get(0);
    assertEquals(
        idt.getDependency(oNode.getGUIBlock()).get(),
        oBlock
    );
    InitDependency pIterable = oBlock.getDependentList().get(0);
    InitDependency p = pIterable.getDependentList().get(0);
    InitDependency pBlock = p.getDependentList().get(0);
    assertEquals(
        idt.getDependency(pNode.getGUIBlock()).get(),
        pBlock
    );

    /*
    InitDependency qConstructor = pBlock.getDependentList().get(0);
    ASTSetEnumeration listNode = GUIDSLMill.typeDispatcher().asSetExpressionsASTSetEnumeration(qNode.getGUINamedArgs().getGUINamedArg(0).getExpression());
    assertEquals(
        idt.getDependency(listNode
            .getGUIList().getGUIArgValue(0)
            .getGUIList().getGUIArgValue(2)
            .getExpression()).get(),
        qConstructor
    );
    InitDependency qInnerList = qConstructor.getDependentList().get(0);
    assertEquals(
        idt.getDependency(qNode.getGUINamedArgs().getGUINamedArg(0).getExpression()
            .getGUIList().getGUIArgValue(0)
            .getGUIList()).get(),
        qInnerList
    );
    InitDependency qCall = qInnerList.getDependentList().get(0);
    assertEquals(
        idt.getDependency(qNode.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()
            .getGUIList().getGUIArgValue(1)
            .getExpression()).get(),
        qCall
    );

    InitDependency qList = qCall.getDependentList().get(0);
    assertEquals(
        idt.getDependency(qNode.getGUINamedArgs().getGUINamedArg(0).getGUIArgValue()
            .getGUIList()).get(),
        qList
    );

    InitDependency rCall = qList.getDependentList().get(0);
    InitDependency rList = rCall.getDependentList().get(0);
    */

  }

}
