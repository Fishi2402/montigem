package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.guidsl._prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.literals.mccommonliterals._prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.literals.mcliteralsbasis._prettyprint.MCLiteralsBasisPrettyPrinter;
import de.monticore.mcbasics._prettyprint.MCBasicsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CommonExpressionsConversionPrinter extends GUIDSLFullPrettyPrinter {

  protected final List<ASTExpression> ignore = new ArrayList<>();
  protected final boolean printComments;

  public CommonExpressionsConversionPrinter(
      IndentPrinter printer,
      boolean printComments
  ) {
    super(printer, printComments);
    this.printComments = printComments;
  }

  public void asAccessorConverter() {
    final ExpressionConversion conv = ExpressionConversion.getInstance();
    Function<ASTNameExpression, String> nameConversion = x -> conv.attr2Getter(x);
    Function<ASTFieldAccessExpression, String> fieldAccessConversion = x -> conv.attr2Getter(x);

    // customized printer
    CommonExpressionsPrettyPrinter commonExpressions = new FieldAccess2AccessorConverter(
        getPrinter(), printComments, fieldAccessConversion, ignore
    );
    getTraverser().setCommonExpressionsHandler(commonExpressions);
    getTraverser().getCommonExpressionsVisitorList().clear();
    getTraverser().add4CommonExpressions(commonExpressions);

    // customized printer
    ExpressionsBasisPrettyPrinter expressionsBasis = new Name2AccessorConverter(
        getPrinter(), printComments, nameConversion, ignore
    );
    getTraverser().setExpressionsBasisHandler(expressionsBasis);
    getTraverser().getExpressionsBasisVisitorList().clear();
    getTraverser().add4ExpressionsBasis(expressionsBasis);

    MCBasicsPrettyPrinter mCBasics = new MCBasicsPrettyPrinter(getPrinter(), printComments);
    getTraverser().setMCBasicsHandler(mCBasics);
    getTraverser().getMCBasicsVisitorList().clear();
    getTraverser().add4MCBasics(mCBasics);
    MCCommonLiteralsPrettyPrinter mcCommonLiterals = new MCCommonLiteralsPrettyPrinter(getPrinter(), printComments);
    getTraverser().setMCCommonLiteralsHandler(mcCommonLiterals);
    getTraverser().getMCCommonLiteralsVisitorList().clear();
    getTraverser().add4MCCommonLiterals(mcCommonLiterals);
    MCLiteralsBasisPrettyPrinter mCLiteralsBasis = new MCLiteralsBasisPrettyPrinter(getPrinter(), printComments);
    getTraverser().setMCLiteralsBasisHandler(mCLiteralsBasis);
    getTraverser().getMCLiteralsBasisVisitorList().clear();
    getTraverser().add4MCLiteralsBasis(mCLiteralsBasis);
  }

}
