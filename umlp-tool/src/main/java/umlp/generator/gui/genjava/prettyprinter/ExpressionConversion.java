package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.guidsl._symboltable.IGUIDSLScope;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl.types.check.util.GUIDSLTypeTraverserFactory;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.Type4Ast;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.Collection;
import java.util.Optional;

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
    Type4Ast type4Ast = new Type4Ast();
    GUIDSLTraverser traverser = GUIDSLTypeTraverserFactory.getInstance().createTraverserForOO(type4Ast);
    node.accept(traverser);

    if (SymTypeRelations.isBoolean(type4Ast.getTypeOfExpression(node))) {
      return "this.is" + StringTransformations.capitalize(node.getName()) + "()";
    }
    return "this.get" + StringTransformations.capitalize(node.getName()) + "()";
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
    Type4Ast type4Ast = new Type4Ast();
    GUIDSLTraverser traverser = GUIDSLTypeTraverserFactory.getInstance().createTraverserForOO(type4Ast);
    node.accept(traverser);

    if (SymTypeRelations.isBoolean(type4Ast.getTypeOfExpression(node))) {
      return "is" + StringTransformations.capitalize(node.getName()) + "()";
    }
    return "get" + StringTransformations.capitalize(node.getName()) + "()";
  }

  public String attr2Setter(ASTNameExpression node) {
    return "set" + StringTransformations.capitalize(node.getName()) + "()";
  }

  public String attr2Setter(ASTFieldAccessExpression node) {
    return "set" + StringTransformations.capitalize(node.getName()) + "()";
  }

  protected boolean isEnum(ASTNameExpression node) {
    return isEnumType(node.getName(), (IGUIDSLScope) node.getEnclosingScope());
  }

  protected boolean isEnum(ASTFieldAccessExpression node) {
    return isEnumType(
        GUIGeneratorUtil.getInstance().print(node.getExpression()),
        (IGUIDSLScope) node.getEnclosingScope()
    );
  }

  private boolean isEnumType(String type, IGUIDSLScope enclosingScope) {
    if (enclosingScope == null) {
      return false;
    }
    Optional<OOTypeSymbol> ooTypeSymbol = enclosingScope.resolveOOType(type);
    return ooTypeSymbol.isPresent() && ooTypeSymbol.get().isIsEnum();
  }

}
