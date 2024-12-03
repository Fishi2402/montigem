package umlp.guidsl.additional_cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._cocos.GUIDSLASTGUINamedArgCoCo;
import de.monticore.guidsl._symboltable.GUIParamSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.util.TagLookUp;

public class RefGUIArgIncompatibleWithDerivedCoCo implements GUIDSLASTGUINamedArgCoCo {

  @Override
  public void check(ASTGUINamedArg node) {
    if (node.isPresentNameSymbol()) {
      GUIParamSymbol sym = node.getNameSymbol();
      if (sym.isIsReference()) {
        ASTExpression exp = node.getExpression();
        String expStr = GUIGeneratorUtil.getInstance().print(exp);
        // a very roundabout way of calling instanceof, without actually writing instanceof
        var expFinder = new ClosestNameOrFieldAccessFinder();
        node.accept(expFinder.getTraverser());
        if (expFinder.getNameExp().isPresent() && expFinder.getNameExp().get().equals(exp)) {
          SymTypeExpression ste = GUIGeneratorUtil.getInstance().getType(expFinder.getNameExp().get());
          if (ste.getSourceInfo().getSourceSymbol().isPresent()) {
            if (ste.getSourceInfo().getSourceSymbol().get() instanceof VariableSymbol
                && TagLookUp.isDerived((VariableSymbol) ste.getSourceInfo().getSourceSymbol().get())) {
              Log.error("0x00024 Attempted to bind a reference argument '" + node.getName()
                      + "' with a derived variable '"
                      + expStr
                      + "'. Reference arguments are objects whose value can be set.\n"
                      + " Consider using a normal variable or use a different component,"
                      + " for example, `@GemText(value = " + expStr + ")`",
                  node.get_SourcePositionStart(), node.get_SourcePositionEnd());
            }
          }
        }
        else if (expFinder.getFieldAccessExp().isPresent() && expFinder.getFieldAccessExp().get().equals(exp)) {
          SymTypeExpression ste = GUIGeneratorUtil.getInstance().getType(expFinder.getFieldAccessExp().get());
          if (ste.getSourceInfo().getSourceSymbol().isPresent()) {
            if (ste.getSourceInfo().getSourceSymbol().get() instanceof VariableSymbol
                && TagLookUp.isDerived((VariableSymbol) ste.getSourceInfo().getSourceSymbol().get())) {
              Log.error("0x00025 Attempted to bind a reference argument '" + node.getName()
                      + "' with a derived attribute '"
                      + expStr
                      + "'. Reference arguments are objects whose value can be set.\n"
                      + " Consider using a normal attribute or use a different component,"
                      + " for example, `@GemText(value = " + expStr + ")`",
                  node.get_SourcePositionStart(), node.get_SourcePositionEnd());
            }
          }
        }
        else if (!(expFinder.getNullExp().isPresent() && expFinder.getNullExp().get().equals(exp))) {
          Log.error("0x00026 Attempted to set value of a reference argument '" + node.getName()
                  + "' to an incompatible expression '"
                  + expStr
                  + "'. Reference arguments are objects whose value can be set.\n"
                  + " Consider binding to a reference variable,"
                  + " using a non-derived attribute,"
                  + " or use a different component,"
                  + " for example, `@GemText(value = " + expStr + ")`",
              node.get_SourcePositionStart(), node.get_SourcePositionEnd());
        }
      }
    }
  }

}
