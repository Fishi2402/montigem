package umlp.guidsl.additional_cocos;

import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._cocos.GUIDSLASTGUINamedArgCoCo;
import de.monticore.guidsl._symboltable.GUIParamSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.util.TagLookUp;

public class RefGUIArgIncompatibleWithDerivedCoCo implements GUIDSLASTGUINamedArgCoCo {

  @Override
  public void check(ASTGUINamedArg node) {
    if (node.isPresentNameSymbol()) {
      GUIParamSymbol sym = node.getNameSymbol();
      if (node.getGUIArgValue().isPresentExpression()) {
        SymTypeExpression ste = GUIGeneratorUtil.getInstance().getType(node.getGUIArgValue().getExpression());
        if (ste.hasTypeInfo()) {
          if (sym.isIsReference() && TagLookUp.isDerived(ste.getTypeInfo())) {
            String argName = node.getName();
            if (node.getEnclosingScope().isPresentName()) {
              argName = node.getEnclosingScope().getName() + "#" + node.getName();
            }
            String exp = GUIGeneratorUtil.getInstance().print(node.getGUIArgValue().getExpression());
            Log.error("0x00024 Attempted to bind a reference argument '" + argName
                    + "' with a derived attribute '"
                    + exp
                    + "'. Reference arguments are objects that have setters."
                    + " Consider using a normal attribute or use a different component,"
                    + " for example, `@GemText(value = " + exp + ")`",
                node.get_SourcePositionStart(), node.get_SourcePositionEnd());
          }
        }
      }
    }
  }

}
