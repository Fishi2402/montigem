package umlp.generator.validator.ocl;

import com.google.common.base.Preconditions;
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
import umlp.generator.validator.ocl.visitors.CustomOCLPrinter;
import umlp.generator.validator.ocl.visitors.CustomSetExpressionsPrinter;

import java.util.List;
import java.util.Map;

public class OCLParser extends OCL2JavaGenerator {

  public OCLParser(IndentPrinter printer, Map<String, List<String>> enumMap, OCLInvariantTree oCLInvariantTree) {
    this(printer, new VariableNaming(), enumMap, oCLInvariantTree);
  }

  protected OCLParser(IndentPrinter printer, VariableNaming naming, Map<String, List<String>> enumMap, OCLInvariantTree oCLInvariantTree) {
    this(printer, naming, new CustomTypeCheck3AsOCLDeriver(enumMap), new CustomTypeCheck3AsOCLSynthesizer(enumMap), enumMap, oCLInvariantTree);
  }

  protected OCLParser(IndentPrinter printer, VariableNaming naming, IDerive deriver, ISynthesize syntheziser, Map<String, List<String>> enumMap, OCLInvariantTree oCLInvariantTree) {
    super(printer, naming, deriver, syntheziser);
    this.traverser.setCommonExpressionsHandler(new CustomCommonExpressionsPrinter(printer, naming, deriver, syntheziser, enumMap));
    CustomOCLExpressionsPrinter customOCLExpressionsPrinter = new CustomOCLExpressionsPrinter(printer, naming, deriver, syntheziser, oCLInvariantTree);
    this.traverser.setOCLExpressionsHandler(customOCLExpressionsPrinter);
    CustomOCLPrinter customOCLPrinter = new CustomOCLPrinter(printer, naming, deriver, syntheziser, oCLInvariantTree);
    this.traverser.setOCLHandler(customOCLPrinter);
    this.traverser.setSetExpressionsHandler(new CustomSetExpressionsPrinter(printer, naming, deriver, syntheziser));
  }

  public static String generate(ASTOCLInvariant invariant, IndentPrinter printer, Map<String, List<String>> enumMap, OCLInvariantTree oCLInvariantTree) {
    Preconditions.checkNotNull(invariant);
    Preconditions.checkNotNull(printer);

    invariant.accept(new OCLParser(printer, enumMap, oCLInvariantTree).getTraverser());
    return printer.getContent();
  }
}
