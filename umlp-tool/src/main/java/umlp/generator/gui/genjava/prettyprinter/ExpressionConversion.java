package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.guidsl._symboltable.IGUIDSLScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.Collection;
import java.util.Optional;

import static umlp.generator.gui.genjava.prettyprinter.Expression2MutatorPrinter.MUTATOR_PARAM;

public class ExpressionConversion {

  protected static ExpressionConversion instance;

  private ExpressionConversion() {
  }

  public static ExpressionConversion getInstance() {
    if (instance == null) {
      instance = new ExpressionConversion();
    }
    return instance;
  }

  public String attr2Getter(ASTNameExpression node) {
    if (isEnum(node)) {
      return node.getName();
    }
    SymTypeExpression ste = GUIGeneratorUtil.getInstance().getType(node);

    if (SymTypeRelations.isBoolean(ste)) {
      return "is" + StringTransformations.capitalize(node.getName()) + "()";
    }
    return "get" + StringTransformations.capitalize(node.getName()) + "()";
  }

  public String attr2Getter(ASTNameExpression node, Collection<String> ignore) {
    if (ignore.contains(node.getName())) {
      return node.getName();
    }
    return attr2Getter(node);
  }

  public String attr2Getter(ASTFieldAccessExpression node) {
    if (isEnum(node)) {
      return node.getName();
    }
    SymTypeExpression ste = GUIGeneratorUtil.getInstance().getType(node);

    if (SymTypeRelations.isBoolean(ste)) {
      return "is" + StringTransformations.capitalize(node.getName()) + "()";
    }
    return "get" + StringTransformations.capitalize(node.getName()) + "()";
  }

  public String attr2Setter(ASTNameExpression node) {
    return "set" + StringTransformations.capitalize(node.getName()) + "(" + MUTATOR_PARAM + ")";
  }

  public String attr2Setter(ASTFieldAccessExpression node) {
    return "set" + StringTransformations.capitalize(node.getName()) + "(" + MUTATOR_PARAM + ")";
  }

  protected boolean isEnum(ASTNameExpression node) {
    if (node.getEnclosingScope() instanceof IGUIDSLScope) {
      Optional<OOTypeSymbol> sym = ((IGUIDSLScope) node.getEnclosingScope())
          .resolveOOType(node.getName());
      return sym.isPresent() && sym.get().isIsEnum();
    }
    else {
      Log.error("0x0000C Attempted to resolve type of '"
          + GUIGeneratorUtil.getInstance().print(node) + "' outside of GUI model context,"
          + " but expression conversion is restricted to GUI models.");
      return false;
    }
  }

  protected boolean isEnum(ASTFieldAccessExpression node) {
    SymTypeExpression ste = GUIGeneratorUtil.getInstance().getType(node);
    if (ste.hasTypeInfo()) {
      if (ste.getTypeInfo() instanceof TypeSymbolSurrogate
          && ((TypeSymbolSurrogate) ste.getTypeInfo()).lazyLoadDelegate() instanceof OOTypeSymbol) {
        OOTypeSymbol sym = (OOTypeSymbol) ((TypeSymbolSurrogate) ste.getTypeInfo()).lazyLoadDelegate();
        return sym.isIsEnum();
      }
      else {
        return false;
      }
    }
    else {
      Log.error("0x0000B Symbol of expression "
          + GUIGeneratorUtil.getInstance().print(node) + " could not be resolved.");
      return false;
    }
  }

}
