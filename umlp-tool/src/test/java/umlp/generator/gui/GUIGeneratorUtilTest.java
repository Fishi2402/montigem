package umlp.generator.gui;

import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("unused")
public class GUIGeneratorUtilTest extends DomainGUITest {

  @Test
  public void testFindClosestGUIScope() {
    ASTGUICompilationUnit ast;
    GUIComponentStat stat = GUIComponentStat.getInstance();
    GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

    List<ASTGUIComponent> components;
    List<ASTGUIGuard> guards;
    List<ASTGUIIterate> iterates;

    ast = processGUIModel("gui/genhtml/HTMLHelper.gui", "src/test/resources/gui");
    ASTGUIComponentDeclaration decl = ast.getGUIComponentDeclaration();

    components = stat.getComponents(decl);
    guards = stat.getGuards(decl);
    iterates = stat.getIterates(decl);

    ASTGUIComponent a = stat.getCompById(components, "a");
    ASTGUIIterate b = stat.getIterById(iterates, "b");
    ASTGUIComponent c = stat.getCompById(components, "c");
    ASTGUIGuard d = stat.getGuardById(guards, "d");
    ASTGUIIterate e = stat.getIterById(iterates, "e");
    ASTGUIComponent f = stat.getCompById(components, "f");
    ASTGUIGuard g = stat.getGuardById(guards, "g");
    ASTGUIComponent h = stat.getCompById(components, "h");
    ASTGUIComponent i = stat.getCompById(components, "i");
    ASTGUIComponent j = stat.getCompById(components, "j");
    ASTGUIComponent k = stat.getCompById(components, "k");
    ASTGUIComponent l = stat.getCompById(components, "l");
    ASTGUIComponent m = stat.getCompById(components, "m");
    ASTGUIComponent n = stat.getCompById(components, "n");
    ASTGUIComponent o = stat.getCompById(components, "o");
    ASTGUIComponent p = stat.getCompById(components, "p");
    ASTGUIGuard q = stat.getGuardById(guards, "q");
    ASTGUIComponent r = stat.getCompById(components, "r");
    ASTGUIComponent s = stat.getCompById(components, "s");
    ASTGUIIterate t = stat.getIterById(iterates, "t");
    ASTGUIIterate u = stat.getIterById(iterates, "u");
    ASTGUIComponent v = stat.getCompById(components, "v");
    ASTGUIComponent w = stat.getCompById(components, "w");
    ASTGUIComponent x = stat.getCompById(components, "x");
    ASTGUIGuard y = stat.getGuardById(guards, "y");
    ASTGUIGuard z = stat.getGuardById(guards, "z");
    ASTGUIIterate aa = stat.getIterById(iterates, "aa");
    ASTGUIIterate ab = stat.getIterById(iterates, "ab");
    ASTGUIGuard ac = stat.getGuardById(guards, "ac");

    // every scope in a GUI model is a GUI DSL scope
    assertAll(
        () -> assertEquals(
            a.getEnclosingScope(),
            util.findClosestGUIDSLScope(a.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            b.getEnclosingScope(),
            util.findClosestGUIDSLScope(b.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            c.getEnclosingScope(),
            util.findClosestGUIDSLScope(c.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            d.getEnclosingScope(),
            util.findClosestGUIDSLScope(d.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            e.getEnclosingScope(),
            util.findClosestGUIDSLScope(e.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            f.getEnclosingScope(),
            util.findClosestGUIDSLScope(f.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            g.getEnclosingScope(),
            util.findClosestGUIDSLScope(g.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            h.getEnclosingScope(),
            util.findClosestGUIDSLScope(h.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            i.getEnclosingScope(),
            util.findClosestGUIDSLScope(i.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            j.getEnclosingScope(),
            util.findClosestGUIDSLScope(j.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            k.getEnclosingScope(),
            util.findClosestGUIDSLScope(k.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            l.getEnclosingScope(),
            util.findClosestGUIDSLScope(l.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            m.getEnclosingScope(),
            util.findClosestGUIDSLScope(m.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            n.getEnclosingScope(),
            util.findClosestGUIDSLScope(n.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            o.getEnclosingScope(),
            util.findClosestGUIDSLScope(o.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            p.getEnclosingScope(),
            util.findClosestGUIDSLScope(p.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            q.getEnclosingScope(),
            util.findClosestGUIDSLScope(q.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            r.getEnclosingScope(),
            util.findClosestGUIDSLScope(r.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            s.getEnclosingScope(),
            util.findClosestGUIDSLScope(s.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            t.getEnclosingScope(),
            util.findClosestGUIDSLScope(t.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            u.getEnclosingScope(),
            util.findClosestGUIDSLScope(u.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            v.getEnclosingScope(),
            util.findClosestGUIDSLScope(v.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            w.getEnclosingScope(),
            util.findClosestGUIDSLScope(w.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            x.getEnclosingScope(),
            util.findClosestGUIDSLScope(x.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            y.getEnclosingScope(),
            util.findClosestGUIDSLScope(y.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            z.getEnclosingScope(),
            util.findClosestGUIDSLScope(z.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            aa.getEnclosingScope(),
            util.findClosestGUIDSLScope(aa.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            ab.getEnclosingScope(),
            util.findClosestGUIDSLScope(ab.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            ac.getEnclosingScope(),
            util.findClosestGUIDSLScope(ac.getEnclosingScope()).get()
        )
    );
  }

  @Test
  public void testFindIterationItemsSymbols1() {
    ASTGUICompilationUnit ast;
    GUIComponentStat stat = GUIComponentStat.getInstance();
    GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

    List<ASTGUIComponent> components;
    List<ASTGUIGuard> guards;
    List<ASTGUIIterate> iterates;

    ast = processGUIModel("gui/genhtml/HTMLHelper.gui", "src/test/resources/gui");
    ASTGUIComponentDeclaration decl = ast.getGUIComponentDeclaration();

    components = stat.getComponents(decl);
    guards = stat.getGuards(decl);
    iterates = stat.getIterates(decl);

    ASTGUIComponent a = stat.getCompById(components, "a");
    ASTGUIIterate b = stat.getIterById(iterates, "b");
    ASTGUIComponent c = stat.getCompById(components, "c");
    ASTGUIGuard d = stat.getGuardById(guards, "d");
    ASTGUIIterate e = stat.getIterById(iterates, "e");
    ASTGUIComponent f = stat.getCompById(components, "f");
    ASTGUIGuard g = stat.getGuardById(guards, "g");
    ASTGUIComponent h = stat.getCompById(components, "h");
    ASTGUIComponent i = stat.getCompById(components, "i");
    ASTGUIComponent j = stat.getCompById(components, "j");
    ASTGUIComponent k = stat.getCompById(components, "k");
    ASTGUIComponent l = stat.getCompById(components, "l");
    ASTGUIComponent m = stat.getCompById(components, "m");
    ASTGUIComponent n = stat.getCompById(components, "n");
    ASTGUIComponent o = stat.getCompById(components, "o");
    ASTGUIComponent p = stat.getCompById(components, "p");
    ASTGUIGuard q = stat.getGuardById(guards, "q");
    ASTGUIComponent r = stat.getCompById(components, "r");
    ASTGUIComponent s = stat.getCompById(components, "s");
    ASTGUIIterate t = stat.getIterById(iterates, "t");
    ASTGUIIterate u = stat.getIterById(iterates, "u");
    ASTGUIComponent v = stat.getCompById(components, "v");
    ASTGUIComponent w = stat.getCompById(components, "w");
    ASTGUIComponent x = stat.getCompById(components, "x");
    ASTGUIGuard y = stat.getGuardById(guards, "y");
    ASTGUIGuard z = stat.getGuardById(guards, "z");
    ASTGUIIterate aa = stat.getIterById(iterates, "aa");
    ASTGUIIterate ab = stat.getIterById(iterates, "ab");
    ASTGUIGuard ac = stat.getGuardById(guards, "ac");

    VariableSymbol bSym = ((ASTGUIShortIterateControl) b.getIterateControl()).getItem().getSymbol();
    VariableSymbol eSym = ((ASTGUIShortIterateControl) e.getIterateControl()).getItem().getSymbol();
    VariableSymbol uSym = ((ASTGUIShortIterateControl) u.getIterateControl()).getItem().getSymbol();
    VariableSymbol tSym = ((ASTGUIShortIterateControl) t.getIterateControl()).getItem().getSymbol();

    assertAll(
        () -> assertTrue(
            util.findIterationItemSymbols(bSym.getName(), a.getEnclosingScope()).isEmpty()
        ),
        () -> assertEquals(
            bSym, util.findIterationItemSymbols(bSym.getName(), b.getIterateControl().getEnclosingScope()).get()
        ),
        () -> assertEquals(
            bSym, util.findIterationItemSymbols(bSym.getName(), c.getGUINamedArgs().getEnclosingScope()).get()
        ),
        () -> assertEquals(
            bSym, util.findIterationItemSymbols(bSym.getName(), d.getCondition().getEnclosingScope()).get()
        ),
        () -> assertTrue(
            util.findIterationItemSymbols(eSym.getName(), d.getCondition().getEnclosingScope()).isEmpty()
        ),
        () -> assertEquals(
            eSym, util.findIterationItemSymbols(eSym.getName(), e.getIterateControl().getEnclosingScope()).get()
        ),
        () -> assertEquals(
            bSym, util.findIterationItemSymbols(bSym.getName(), e.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            bSym, util.findIterationItemSymbols(bSym.getName(), j.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            eSym, util.findIterationItemSymbols(eSym.getName(), j.getEnclosingScope()).get()
        ),
        () -> assertTrue(
            util.findIterationItemSymbols(eSym.getName(), n.getEnclosingScope()).isEmpty()
        ),
        () -> assertTrue(
            util.findIterationItemSymbols(eSym.getName(), n.getGUINamedArgs().getEnclosingScope()).isEmpty()
        ),
        () -> assertEquals(
            uSym, util.findIterationItemSymbols(uSym.getName(), v.getEnclosingScope()).get()
        ),
        () -> assertEquals(
            tSym, util.findIterationItemSymbols(tSym.getName(), v.getGUINamedArgs().getEnclosingScope()).get()
        )
    );
  }

  @Test
  public void testFindIterationItemsSymbols2() {
    ASTGUICompilationUnit ast;
    GUIComponentStat stat = GUIComponentStat.getInstance();
    GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

    List<ASTGUIComponent> components;
    List<ASTGUIGuard> guards;
    List<ASTGUIIterate> iterates;

    ast = processGUIModel("gui/genhtml/HTMLHelper.gui", "src/test/resources/gui");
    ASTGUIComponentDeclaration decl = ast.getGUIComponentDeclaration();

    components = stat.getComponents(decl);
    guards = stat.getGuards(decl);
    iterates = stat.getIterates(decl);

    ASTGUIComponent a = stat.getCompById(components, "a");
    ASTGUIIterate b = stat.getIterById(iterates, "b");
    ASTGUIComponent c = stat.getCompById(components, "c");
    ASTGUIGuard d = stat.getGuardById(guards, "d");
    ASTGUIIterate e = stat.getIterById(iterates, "e");
    ASTGUIComponent f = stat.getCompById(components, "f");
    ASTGUIGuard g = stat.getGuardById(guards, "g");
    ASTGUIComponent h = stat.getCompById(components, "h");
    ASTGUIComponent i = stat.getCompById(components, "i");
    ASTGUIComponent j = stat.getCompById(components, "j");
    ASTGUIComponent k = stat.getCompById(components, "k");
    ASTGUIComponent l = stat.getCompById(components, "l");
    ASTGUIComponent m = stat.getCompById(components, "m");
    ASTGUIComponent n = stat.getCompById(components, "n");
    ASTGUIComponent o = stat.getCompById(components, "o");
    ASTGUIComponent p = stat.getCompById(components, "p");
    ASTGUIGuard q = stat.getGuardById(guards, "q");
    ASTGUIComponent r = stat.getCompById(components, "r");
    ASTGUIComponent s = stat.getCompById(components, "s");
    ASTGUIIterate t = stat.getIterById(iterates, "t");
    ASTGUIIterate u = stat.getIterById(iterates, "u");
    ASTGUIComponent v = stat.getCompById(components, "v");
    ASTGUIComponent w = stat.getCompById(components, "w");
    ASTGUIComponent x = stat.getCompById(components, "x");
    ASTGUIGuard y = stat.getGuardById(guards, "y");
    ASTGUIGuard z = stat.getGuardById(guards, "z");
    ASTGUIIterate aa = stat.getIterById(iterates, "aa");
    ASTGUIIterate ab = stat.getIterById(iterates, "ab");
    ASTGUIGuard ac = stat.getGuardById(guards, "ac");

    VariableSymbol bSym = ((ASTGUIShortIterateControl) b.getIterateControl()).getItem().getSymbol();
    VariableSymbol eSym = ((ASTGUIShortIterateControl) e.getIterateControl()).getItem().getSymbol();
    VariableSymbol uSym = ((ASTGUIShortIterateControl) u.getIterateControl()).getItem().getSymbol();
    VariableSymbol tSym = ((ASTGUIShortIterateControl) t.getIterateControl()).getItem().getSymbol();

    List<VariableSymbol> result1 = util.findIterationItemSymbols(a.getEnclosingScope());
    List<VariableSymbol> result2 = util.findIterationItemSymbols(b.getIterateControl().getEnclosingScope());
    List<VariableSymbol> result3 = util.findIterationItemSymbols(c.getGUINamedArgs().getEnclosingScope());
    List<VariableSymbol> result4 = util.findIterationItemSymbols(d.getCondition().getEnclosingScope());
    List<VariableSymbol> result5 = util.findIterationItemSymbols(e.getIterateControl().getEnclosingScope());
    List<VariableSymbol> result6 = util.findIterationItemSymbols(j.getEnclosingScope());
    List<VariableSymbol> result7 = util.findIterationItemSymbols(n.getEnclosingScope());
    List<VariableSymbol> result8 = util.findIterationItemSymbols(n.getGUINamedArgs().getEnclosingScope());
    List<VariableSymbol> result9 = util.findIterationItemSymbols(v.getEnclosingScope());
    List<VariableSymbol> result10 = util.findIterationItemSymbols(v.getGUINamedArgs().getEnclosingScope());

    assertAll(
        () -> assertEquals(0, result1.size()),
        () -> assertEquals(1, result2.size()),
        () -> assertEquals(1, result3.size()),
        () -> assertEquals(1, result4.size()),
        () -> assertEquals(2, result5.size()),
        () -> assertEquals(2, result6.size()),
        () -> assertEquals(1, result7.size()),
        () -> assertEquals(1, result8.size()),
        () -> assertEquals(2, result9.size()),
        () -> assertEquals(2, result10.size()),

        () -> assertTrue(result2.contains(bSym)),
        () -> assertTrue(result3.contains(bSym)),
        () -> assertTrue(result4.contains(bSym)),
        () -> assertTrue(result5.contains(bSym)),
        () -> assertTrue(result5.contains(eSym)),
        () -> assertTrue(result6.contains(bSym)),
        () -> assertTrue(result6.contains(eSym)),
        () -> assertTrue(result7.contains(bSym)),
        () -> assertTrue(result8.contains(bSym)),
        () -> assertTrue(result9.contains(uSym)),
        () -> assertTrue(result9.contains(tSym)),
        () -> assertTrue(result10.contains(uSym)),
        () -> assertTrue(result10.contains(tSym))
    );

  }
}
