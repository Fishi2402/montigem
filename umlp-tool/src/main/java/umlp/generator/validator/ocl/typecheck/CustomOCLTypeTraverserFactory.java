package umlp.generator.validator.ocl.typecheck;

import de.monticore.ocl.ocl.OCLMill;
import de.monticore.ocl.ocl._visitor.OCLTraverser;
import de.monticore.ocl.ocl.types3.OCLTypeTraverserFactory;
import de.monticore.ocl.types3.util.OCLWithinTypeBasicSymbolsResolver;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.util.FunctionRelations;
import de.monticore.types3.util.NameExpressionTypeCalculator;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;

import java.util.List;
import java.util.Map;

public class CustomOCLTypeTraverserFactory extends OCLTypeTraverserFactory {

  private final Map<String, List<String>> enumMap;

  public CustomOCLTypeTraverserFactory(Map<String, List<String>> enumMap) {
      super();
      this.enumMap = enumMap;
  }

  @Override
  public OCLTraverser createTraverser(Type4Ast type4Ast) {
    OCLTraverser traverser = OCLMill.inheritanceTraverser();
    VisitorList visitors = constructVisitors();
    setType4Ast(visitors, type4Ast);
    populateTraverser(visitors, traverser);
    return traverser;
  }

  @Override
  protected VisitorList constructVisitors() {
    VisitorList visitors = constructVisitorsDefault();
    WithinTypeBasicSymbolsResolver withinTypeBasicSymbolsResolver =
            new OCLWithinTypeBasicSymbolsResolver();
    NameExpressionTypeCalculator nameExpressionTypeCalculator =
            new CustomOCLNameExpressionTypeCalculator(enumMap);
    FunctionRelations functionRelations = new FunctionRelations();
    visitors.derCommonExpressions.setWithinTypeBasicSymbolsResolver(withinTypeBasicSymbolsResolver);
    visitors.derCommonExpressions.setNameExpressionTypeCalculator(nameExpressionTypeCalculator);
    visitors.derExpressionBasis.setNameExpressionTypeCalculator(nameExpressionTypeCalculator);
    visitors.synMCBasicTypes.setWithinTypeResolver(withinTypeBasicSymbolsResolver);
    visitors.synMCBasicTypes.setNameExpressionTypeCalculator(nameExpressionTypeCalculator);
    return visitors;
  }
}
