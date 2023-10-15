package umlp.generator.validator.ocl;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.codegen.OCL2JavaGenerator;
import de.monticore.ocl.codegen.util.VariableNaming;
import de.monticore.ocl.ocl._ast.ASTOCLInvariant;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import umlp.generator.validator.ocl.typecheck.CustomTypeCheck3AsOCLDeriver;
import umlp.generator.validator.ocl.typecheck.CustomTypeCheck3AsOCLSynthesizer;
import umlp.generator.validator.ocl.visitors.CustomCommonExpressionsPrinter;

import java.util.List;
import java.util.Map;

public class OCLParser extends OCL2JavaGenerator {

  public OCLParser(IndentPrinter printer, Map<String, List<String>> enumMap) {
    this(printer, new VariableNaming(), enumMap);
  }

  protected OCLParser(IndentPrinter printer, VariableNaming naming, Map<String, List<String>> enumMap) {
    this(printer, naming, new CustomTypeCheck3AsOCLDeriver(enumMap), new CustomTypeCheck3AsOCLSynthesizer(enumMap), enumMap);
    //this(printer, naming, new TypeCheck3AsOCLDeriver(), new TypeCheck3AsOCLSynthesizer());
  }

  protected OCLParser(IndentPrinter printer, VariableNaming naming, IDerive deriver, ISynthesize syntheziser, Map<String, List<String>> enumMap) {
    super(printer, naming, deriver, syntheziser);
    this.traverser.setCommonExpressionsHandler(new CustomCommonExpressionsPrinter(printer, naming, deriver, syntheziser, enumMap));
  }

  public static String generate(ASTOCLInvariant invariant, IndentPrinter printer, Map<String, List<String>> enumMap) {
    Preconditions.checkNotNull(invariant);
    Preconditions.checkNotNull(printer);

    invariant.accept(new OCLParser(printer, enumMap).getTraverser());
    return printer.getContent();
  }

  public static String generate(ASTExpression expression, IndentPrinter printer, Map<String, List<String>> enumMap) {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(printer);

    expression.accept(new OCLParser(printer, enumMap).getTraverser());
    return printer.getContent();
  }
}
