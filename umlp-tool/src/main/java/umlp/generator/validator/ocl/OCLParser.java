package umlp.generator.validator.ocl;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.codegen.OCL2JavaGenerator;
import de.monticore.ocl.codegen.util.VariableNaming;
import de.monticore.ocl.ocl._ast.ASTOCLInvariant;
import de.monticore.ocl.types.check.types3wrapper.TypeCheck3AsOCLDeriver;
import de.monticore.ocl.types.check.types3wrapper.TypeCheck3AsOCLSynthesizer;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import umlp.generator.validator.ocl.visitors.CustomCommonExpressionsPrinter;

public class OCLParser extends OCL2JavaGenerator {
  public OCLParser(IndentPrinter printer) {
    this(printer, new VariableNaming());
  }

  protected OCLParser(IndentPrinter printer, VariableNaming naming) {
    //this(printer, naming, new CustomTypeCheck3AsOCLDeriver(), new CustomTypeCheck3AsOCLSynthesizer());
    this(printer, naming, new TypeCheck3AsOCLDeriver(), new TypeCheck3AsOCLSynthesizer());
  }

  protected OCLParser(IndentPrinter printer, VariableNaming naming, IDerive deriver, ISynthesize syntheziser) {
    super(printer, naming, deriver, syntheziser);
    this.traverser.setCommonExpressionsHandler(new CustomCommonExpressionsPrinter(printer, naming, deriver, syntheziser));
  }

  public static String generate(ASTOCLInvariant invariant, IndentPrinter printer) {
    Preconditions.checkNotNull(invariant);
    Preconditions.checkNotNull(printer);

    invariant.accept(new OCLParser(printer).getTraverser());
    return printer.getContent();
  }

  public static String generate(ASTExpression expression, IndentPrinter printer) {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(printer);

    expression.accept(new OCLParser(printer).getTraverser());
    return printer.getContent();
  }
}
