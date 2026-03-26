package ocl2java;

import de.monticore.ocl.ocl.OCLMill;
import de.monticore.ocl.ocl._ast.ASTOCLCompilationUnit;
import de.monticore.ocl.ocl._ast.ASTOCLInvariant;
import de.monticore.ocl.ocl._ast.ASTOCLNode;
import de.monticore.ocl.ocl._visitor.OCLTraverser;
import jsweet.lang.Erased;

@Erased
public class OCL2Cypher {


  /**
   * Translates a given OCL (Object Constraint Language) AST node into a Cypher query string.
   * This method processes the provided AST node using a traverser and a visitor pattern
   * to generate a valid Cypher query.
   *
   * @param ocl The OCL AST node to be translated. It must be an instance of either
   *            ASTOCLCompilationUnit or ASTOCLInvariant.
   * @return A Cypher query string derived from the provided OCL AST node.
   * @throws RuntimeException If the provided AST node is not an instance of
   *                          ASTOCLCompilationUnit or ASTOCLInvariant.
   */
  public static String translate(ASTOCLNode ocl){

    if (!(ocl instanceof ASTOCLCompilationUnit) && !(ocl instanceof ASTOCLInvariant)) {
      throw new RuntimeException("Invalid AST Type passed to translate. Must be ASTOCLCompilationUnit or ASTOCLInvariant!");
    }

    // init
    OCLTraverser traverser = OCLMill.traverser();
    OCL2CypherVisitor ocl2CypherVisitor = new OCL2CypherVisitor();

    traverser.add4OCL(ocl2CypherVisitor);
    traverser.add4OCLExpressions(ocl2CypherVisitor);
    traverser.add4CommonExpressions(ocl2CypherVisitor);
    traverser.add4ExpressionsBasis(ocl2CypherVisitor);
    traverser.add4MCCommonLiterals(ocl2CypherVisitor);
    traverser.add4SetExpressions(ocl2CypherVisitor);
    traverser.setSetExpressionsHandler(ocl2CypherVisitor);
    traverser.add4MCCommonLiterals(ocl2CypherVisitor);

    traverser.setOCLHandler(ocl2CypherVisitor);
    traverser.setCommonExpressionsHandler(ocl2CypherVisitor);
    traverser.add4BitExpressions(ocl2CypherVisitor);
    traverser.setBitExpressionsHandler(ocl2CypherVisitor);
    traverser.setOCLHandler(ocl2CypherVisitor);
    traverser.setOCLExpressionsHandler(ocl2CypherVisitor);
    traverser.add4OptionalOperators(ocl2CypherVisitor);
    traverser.setOptionalOperatorsHandler(ocl2CypherVisitor);
    traverser.setExpressionsBasisHandler(ocl2CypherVisitor);
    traverser.setMCCommonLiteralsHandler(ocl2CypherVisitor);

    // Start traversal
    ocl.accept(traverser);

    // Get the query
    String query = ocl2CypherVisitor.getQuery();

    System.out.println("\nQUERY:\n" + query + "\n");

    return query;
  }

}
