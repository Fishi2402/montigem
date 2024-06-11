package umlp.guidsl.additional_cocos;

import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.guidsl._cocos.GUIDSLASTGUIParamCoCo;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class GUIParamNoUnderspecifiedGenericsCoCo implements GUIDSLASTGUIParamCoCo {

  @Override
  public void check(ASTGUIParam node) {
    if (node.isPresentSymbol()) {
      checkGeneric(node.getSymbol().getType(), node);
    }
  }

  protected void checkGeneric(SymTypeExpression ste, ASTGUIParam node) {
    if (ste.isGenericType()) {
      for (SymTypeExpression inner : ste.asGenericType().getArgumentList()) {
        checkGeneric(inner, node);
      }
    }
    else if (ste.isObjectType()) {
      Optional<TypeSymbol> tsOpt = node.getEnclosingScope().resolveType(ste.printFullName());
      if (tsOpt.isPresent()) {
        TypeSymbol ts = tsOpt.get();
        if (!ts.getTypeParameterList().isEmpty()) {
          Log.error("0x00021 Underspecified generic type are not supported yet."
              + " Please specify type parameter (at least 'Object')"
              + " at " + node.get_SourcePositionStart().toString()
              + " - " + node.get_SourcePositionEnd().toString());
        }
      }
    }
  }
}
