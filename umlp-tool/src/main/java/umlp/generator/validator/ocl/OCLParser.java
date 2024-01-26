package umlp.generator.validator.ocl;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.codegen.OCL2JavaGenerator;
import de.monticore.ocl.codegen.util.VariableNaming;
import de.monticore.ocl.ocl._ast.ASTOCLInvariant;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import umlp.generator.validator.ocl.associationTree.OCLInvariantTree;
import umlp.generator.validator.ocl.typecheck.CustomTypeCheck3AsOCLDeriver;
import umlp.generator.validator.ocl.typecheck.CustomTypeCheck3AsOCLSynthesizer;
import umlp.generator.validator.ocl.visitors.CustomCommonExpressionsPrinter;
import umlp.generator.validator.ocl.visitors.CustomOCLExpressionsPrinter;

import java.util.List;
import java.util.Map;

public class OCLParser extends OCL2JavaGenerator {

  public OCLParser(IndentPrinter printer, Map<String, List<String>> enumMap, OCLInvariantTree OCLInvariantTree) {
    this(printer, new VariableNaming(), enumMap, OCLInvariantTree);
  }

  protected OCLParser(IndentPrinter printer, VariableNaming naming, Map<String, List<String>> enumMap, OCLInvariantTree OCLInvariantTree) {
    this(printer, naming, new CustomTypeCheck3AsOCLDeriver(enumMap), new CustomTypeCheck3AsOCLSynthesizer(enumMap), enumMap, OCLInvariantTree);
  }

  protected OCLParser(IndentPrinter printer, VariableNaming naming, IDerive deriver, ISynthesize syntheziser, Map<String, List<String>> enumMap, OCLInvariantTree OCLInvariantTree) {
    super(printer, naming, deriver, syntheziser);
    this.traverser.setCommonExpressionsHandler(new CustomCommonExpressionsPrinter(printer, naming, deriver, syntheziser, enumMap));
    CustomOCLExpressionsPrinter customOCLExpressionsPrinter = new CustomOCLExpressionsPrinter(printer, naming, deriver, syntheziser, OCLInvariantTree);
    this.traverser.setOCLExpressionsHandler(customOCLExpressionsPrinter);
  }

  public static String generate(ASTOCLInvariant invariant, IndentPrinter printer, Map<String, List<String>> enumMap, OCLInvariantTree OCLInvariantTree) {
    Preconditions.checkNotNull(invariant);
    Preconditions.checkNotNull(printer);

    invariant.accept(new OCLParser(printer, enumMap, OCLInvariantTree).getTraverser());
    return printer.getContent();
  }

  public static String generate(ASTExpression expression, IndentPrinter printer, Map<String, List<String>> enumMap, OCLInvariantTree oCLInvariantTree) {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(printer);

    expression.accept(new OCLParser(printer, enumMap, oCLInvariantTree).getTraverser());
    return printer.getContent();
  }
}
