package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.genjava.decorator.Attribute;
import umlp.generator.gui.genjava.decorator.Method;
import umlp.generator.gui.genjava.decorator.PredefinedFunction;
import umlp.generator.gui.genjava.decorator.StaticObjectsCollector;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class FieldAccess2AccessorConverter extends CommonExpressionsPrettyPrinter {

  protected final Collection<ASTExpression> ignore;

  public FieldAccess2AccessorConverter(
      IndentPrinter printer, boolean printComments,
      Collection<ASTExpression> ignore
  ) {
    super(printer, printComments);
    this.ignore = ignore;
  }

  @Override
  public void handle(ASTCallExpression node) {
    // TODO AGe: better way to customize printing for corner cases
    Map<ASTNode, Attribute> node2Attr = StaticObjectsCollector.getInstance().getNode2Attr();
    Map<ASTNode, Method> node2Method = StaticObjectsCollector.getInstance().getNode2Method();
    // a special case with overloaded predefined method
    if (node2Method.containsKey(node)) {
      Method method = node2Method.get(node);
      if (this.isPrintComments()) {
        CommentPrettyPrinter.printPreComments(node, getPrinter());
      }

      getPrinter().print("this." + method.getAlias());

      // arguments start
      if (this.isPrintComments()) {
        CommentPrettyPrinter.printPreComments(node.getArguments(), getPrinter());
      }
      Iterator<ASTExpression> iter_expression = node.getArguments().getExpressionList().iterator();

      getPrinter().stripTrailing();
      getPrinter().print("(");

      if (method.getPredefined().isPresent()
          && (method.getPredefined().get().equals(PredefinedFunction.ASSOC_ADD)
          || method.getPredefined().get().equals(PredefinedFunction.ASSOC_REMOVE)
          || method.getPredefined().get().equals(PredefinedFunction.ASSOC_SET))
      ) {
        if (node.getArguments().sizeExpressions() != 2) {
          Log.error("0x0001E Functions 'assoc_add' and 'assoc_remove' expect 2 arguments"
              + " at " + node.get_SourcePositionStart().toString()
              + " - " + node.get_SourcePositionEnd().toString());
        }
        iter_expression.next();
      }

      if (iter_expression.hasNext()) {
        iter_expression.next().accept(getTraverser());
        while (iter_expression.hasNext()) {
          getPrinter().stripTrailing();
          getPrinter().print(",");
          iter_expression.next().accept(getTraverser());
        }
      }

      getPrinter().stripTrailing();
      getPrinter().print(")");

      if (this.isPrintComments()) {
        CommentPrettyPrinter.printPostComments(node.getArguments(), getPrinter());
      }
      // arguments end

      if (this.isPrintComments()) {
        CommentPrettyPrinter.printPostComments(node, getPrinter());
      }
    }
    // a special case with builder or constructor
    else if (node2Attr.containsKey(node)) {
      if (this.isPrintComments()) {
        CommentPrettyPrinter.printPreComments(node, getPrinter());
      }

      getPrinter().print("this.get" + StringTransformations.capitalize(
          node2Attr.get(node).getCdAttr().getName()
      ));

      // arguments start
      if (this.isPrintComments()) {
        CommentPrettyPrinter.printPreComments(node.getArguments(), getPrinter());
      }
      getPrinter().stripTrailing();
      getPrinter().print("(");

      Iterator<VariableSymbol> vs = GUIGeneratorUtil.getInstance()
          .findIterationItemSymbols(node.getEnclosingScope()).iterator();

      if (vs.hasNext()) {
        getPrinter().print(vs.next().getName());
        while (vs.hasNext()) {
          getPrinter().stripTrailing();
          getPrinter().print(",");
          getPrinter().print(vs.next().getName());
        }
      }

      getPrinter().stripTrailing();
      getPrinter().print(")");

      if (this.isPrintComments()) {
        CommentPrettyPrinter.printPostComments(node.getArguments(), getPrinter());
      }
      // arguments end

      if (this.isPrintComments()) {
        CommentPrettyPrinter.printPostComments(node, getPrinter());
      }
    }
    // ignore field and name expressions that are direct children of ASTCallExpression
    else {
      ignore.add(node.getExpression());
      super.handle(node);
    }
  }

  @Override
  public void handle(ASTFieldAccessExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getExpression().accept(getTraverser());

    getPrinter().stripTrailing();
    getPrinter().print(".");

    var conversions = ExpressionPrinterConfig.getInstance().customConversions;
    if (conversions.containsKey(node)) {
      getPrinter().print(conversions.get(node).apply(node));
    }
    else if (ignore.contains(node) || isEnum(node)) {
      getPrinter().print(node.getName() + " ");
    }
    else {
      getPrinter().print(attr2GetterCall(node) + " ");
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }

  }

  protected String attr2GetterCall(ASTFieldAccessExpression node) {
    return ExpressionPrinterConfig.attr2Getter(node) + "()";
  }

  protected boolean isEnum(ASTFieldAccessExpression node) {
    return GUIGeneratorUtil.getInstance().isEnumMember(node);
  }
}
