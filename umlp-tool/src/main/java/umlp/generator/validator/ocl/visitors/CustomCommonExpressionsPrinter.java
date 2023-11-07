package umlp.generator.validator.ocl.visitors;

import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._ast.ASTInfixExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.ocl.codegen.util.VariableNaming;
import de.monticore.ocl.codegen.visitors.CommonExpressionsPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.util.TypeVisitorLifting;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.domain.decorator.ValidatorDecorator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomCommonExpressionsPrinter extends CommonExpressionsPrinter {

  private final Map<String, List<String>> enumMap;

  public CustomCommonExpressionsPrinter(IndentPrinter printer, VariableNaming naming, IDerive deriver, ISynthesize syntheziser, Map<String, List<String>> enumMap) {
    super(printer, naming, deriver, syntheziser);
    this.enumMap = enumMap;
  }

  @Override
  public void handle(ASTFieldAccessExpression node) {
    Preconditions.checkNotNull(node);
    node.getExpression().accept(this.getTraverser());
    Optional<String> enumNameExpression = Optional.empty();
    if(node.getExpression() instanceof ASTNameExpression) {
      ASTNameExpression nameExpression =((ASTNameExpression) node.getExpression());
      enumNameExpression = enumMap.keySet().stream().filter(eN -> eN.equals(nameExpression.getName())).findFirst();
    }
    if (enumNameExpression.isPresent() && ((ASTNameExpression) node.getExpression()).getName().equals(enumNameExpression.get()) && enumMap.get(enumNameExpression.get()).contains(node.getName())) {
      this.getPrinter().print(".");
      this.getPrinter().print(node.getName());
    } else {
      this.getPrinter().print(".");
      this.getPrinter().print("get" + StringTransformations.capitalize(node.getName()) + "()");
    }
  }

  @Override
  public void handle(ASTCallExpression node) {
    Preconditions.checkNotNull(node);
    String call = "";
    if(node.getExpression() instanceof ASTFieldAccessExpression) {
      ASTFieldAccessExpression fieldAccessExpression = (ASTFieldAccessExpression) node.getExpression();
      fieldAccessExpression.getExpression().accept(this.getTraverser());
      call += "." + fieldAccessExpression.getName();
    } else {
      node.getExpression().accept(this.getTraverser());
    }
    this.getPrinter().print(call);
    node.getArguments().accept(this.getTraverser());
  }

}
