package ocl2cypher;

import de.monticore.expressions.bitexpressions._ast.ASTLeftShiftExpression;
import de.monticore.expressions.bitexpressions._ast.ASTLogicalRightShiftExpression;
import de.monticore.expressions.bitexpressions._ast.ASTRightShiftExpression;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsHandler;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsTraverser;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsVisitor2;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.literals.mccommonliterals._ast.ASTMCCommonLiteralsNode;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsHandler;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsTraverser;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.ocl.ocl.OCLMill;
import de.monticore.ocl.ocl._ast.*;
import de.monticore.ocl.ocl._visitor.OCLHandler;
import de.monticore.ocl.ocl._visitor.OCLTraverser;
import de.monticore.ocl.ocl._visitor.OCLVisitor2;
import de.monticore.ocl.oclexpressions._ast.*;
import de.monticore.ocl.oclexpressions._visitor.OCLExpressionsHandler;
import de.monticore.ocl.oclexpressions._visitor.OCLExpressionsTraverser;
import de.monticore.ocl.oclexpressions._visitor.OCLExpressionsVisitor2;
import de.monticore.ocl.optionaloperators._ast.*;
import de.monticore.ocl.optionaloperators._visitor.OptionalOperatorsHandler;
import de.monticore.ocl.optionaloperators._visitor.OptionalOperatorsTraverser;
import de.monticore.ocl.optionaloperators._visitor.OptionalOperatorsVisitor2;
import de.monticore.ocl.setexpressions._ast.*;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsHandler;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsTraverser;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsVisitor2;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import jsweet.lang.Erased;
import ocl2cypher.utils.CypherQueryBuilder;
import ocl2cypher.utils.LiteralUtils;
import ocl2cypher.utils.SetContainmentUtils;

import java.util.ArrayList;
import java.util.List;

@Erased
public class OCL2CypherVisitor implements OCLVisitor2,
        OCLHandler,
        CommonExpressionsVisitor2,
        CommonExpressionsHandler,
        OCLExpressionsVisitor2,
        OCLExpressionsHandler,
        ExpressionsBasisVisitor2,
        ExpressionsBasisHandler,
        BitExpressionsVisitor2,
        BitExpressionsHandler,
        MCCommonLiteralsVisitor2,
        SetExpressionsHandler,
        SetExpressionsVisitor2,
        OptionalOperatorsHandler,
        OptionalOperatorsVisitor2,
        MCCommonLiteralsHandler {

  private boolean isImplicitContext = false;


  OCLTraverser ocl_t;

  @Override
  public OCLTraverser getTraverser() {
    return ocl_t;
  }

  @Override
  public void setTraverser(OCLTraverser traverser) {
    this.ocl_t = traverser;
  }

  OptionalOperatorsTraverser optOp_t;
  @Override
  public void setTraverser(OptionalOperatorsTraverser traverser) {
    if(traverser instanceof  OCLTraverser) {
      this.optOp_t = traverser;
    } else {
      throw new IllegalArgumentException("Expected OCLTraverser, got " + traverser.getClass());
    }
  }

  @Override
  public void setTraverser(SetExpressionsTraverser traverser) {
    if (traverser instanceof OCLTraverser) {
      this.ocl_t = (OCLTraverser) traverser;
    } else {
      throw new IllegalArgumentException("Expected OCLTraverser, got " + traverser.getClass());
    }
  }

  @Override
  public void setTraverser(CommonExpressionsTraverser traverser) {

  }

  OCLExpressionsTraverser oclE_t;

  public void setTraverser(OCLExpressionsTraverser traverser) {
    this.ocl_t = (OCLTraverser) traverser;
  }

  @Override
  public void setTraverser(ExpressionsBasisTraverser traverser) {
    if (traverser instanceof OCLTraverser) {
      this.ocl_t = (OCLTraverser) traverser;
    } else {
      throw new IllegalArgumentException("Expected OCLTraverser, got " + traverser.getClass());
    }
  }

  @Override
  public void setTraverser(MCCommonLiteralsTraverser traverser) {
    if (traverser instanceof OCLTraverser) {
      this.ocl_t = (OCLTraverser) traverser;
    } else {
      throw new IllegalArgumentException("Expected OCLTraverser, got " + traverser.getClass());
    }
  }

  @Override
  public void setTraverser(BitExpressionsTraverser bitExpressionsTraverser) {

  }


  private final CypherQueryBuilder queryBuilder = new CypherQueryBuilder();


  /**
   * Retrieves the constructed Cypher query as a string.
   *
   * This method uses the query builder to generate the final query by combining
   * all completed query blocks. The returned query is ready for execution in a Cypher-compatible database.
   *
   * @return the string representation of the completed Cypher query.
   */
  public String getQuery() {
    return queryBuilder.build();
  }


  /**
   * Processes the given {@link ASTOCLCompilationUnit} node by iterating over the list of OCL constraints
   * retrieved from the node. It specifically handles instances of {@link ASTOCLInvariant} constraints
   * by invoking their {@code accept} method with the current traverser.
   *
   * @param node the {@link ASTOCLCompilationUnit} node containing OCL constraints to be processed
   */
  @Override
  public void handle(ASTOCLCompilationUnit node) {
    List<ASTOCLConstraint> constraintsList = node.getOCLArtifact().getOCLConstraintList();

    for (ASTOCLConstraint constraint : constraintsList) {
      if (constraint instanceof ASTOCLInvariant) {
        ((ASTOCLInvariant) constraint).accept(getTraverser());
      }
    }
  }


  String param = "";
  String paramType = "";
  ArrayList<String> allParams = new ArrayList<>();

  /**
   * Processes the given {@link ASTOCLInvariant} node to transform it into a
   * query component by interpreting its context definitions and parameters.
   * Constructs and appends the MATCH and WHERE parts of the query using
   * supported OCL context definitions while validating the node structure.
   *
   * @param node the {@link ASTOCLInvariant} node to be visited, representing
   *             the invariant structure including context definitions and other components
   * @throws IllegalArgumentException           if the node contains no context definitions
   * @throws UnsupportedOperationException      if an unsupported context variant
   *                                            (e.g., {@link ASTGeneratorDeclaration}) is encountered
   */
  @Override
  public void visit(ASTOCLInvariant node) {
    queryBuilder.startNewBlock();
    allParams = new ArrayList<>();

    List<ASTOCLContextDefinition> ctxDefs = node.getOCLContextDefinitionList();
    if (ctxDefs.isEmpty()) {
      throw new IllegalArgumentException("Invariant without context definition is not supported.");
    }

    List<String> matchParts = new ArrayList<>();
    for (ASTOCLContextDefinition ctxDef : ctxDefs) {
      String localParam;
      String localParamType;

      if (ctxDef.isPresentOCLParamDeclaration()) {
        ASTOCLParamDeclaration decl = ctxDef.getOCLParamDeclaration();
        localParam = decl.getName();
        localParamType = decl.getMCType().printType();
      }
      else if (ctxDef.isPresentMCType()) {
        isImplicitContext = true;
        localParamType = ctxDef.getMCType().printType();
        localParam = Character.toLowerCase(localParamType.charAt(0)) + localParamType.substring(1);
      }
      else if (ctxDef.isPresentGeneratorDeclaration()) {
        ASTGeneratorDeclaration gen = ctxDef.getGeneratorDeclaration();
        throw new UnsupportedOperationException("GeneratorDeclaration not supported yet.");
      }
      else {
        throw new UnsupportedOperationException("Unsupported OCLContextDefinition variant at " + ctxDef.get_SourcePositionStart());
      }
      matchParts.add("(" +localParam + ":" + localParamType + ")");
      allParams.add(localParam);
    }

    ASTOCLContextDefinition firstDef = ctxDefs.get(0);
    if (firstDef.isPresentOCLParamDeclaration()) {
      param = firstDef.getOCLParamDeclaration().getName();
      paramType = firstDef.getOCLParamDeclaration().getMCType().printType();
    }
    else {
      paramType = firstDef.getMCType().printType();
      param = Character.toLowerCase(paramType.charAt(0)) + paramType.substring(1);
    }

    // MATCH part
    queryBuilder.appendMatch(String.join(", ", matchParts));

    // WHERE part
    queryBuilder.appendToWhere("not ");
  }


  /**
   * Finalizes the visit to the given {@link ASTOCLInvariant} node by appending
   * a RETURN clause to the Cypher query. The RETURN clause provides the gemId
   * attribute for the corresponding node.
   *
   * @param node the {@link ASTOCLInvariant} node that was previously visited and
   *             processed to construct parts of the Cypher query
   */
  @Override
  public void endVisit(ASTOCLInvariant node) {
    queryBuilder.appendReturn(param + ".gemId AS gemId");
  }

  // Common Expressions

  /**
   * Processes an {@link ASTLessEqualExpression} node to generate a part of the
   * Cypher query. This method appends a comparison expression using the "<="
   * operator to the WHERE clause of the query. It handles the traversal of the
   * left and right operands of the expression to include their corresponding
   * Cypher representations.
   *
   * @param node the {@link ASTLessEqualExpression} node to be processed,
   *             representing a "less than or equal to" expression in the AST
   */
  @Override
  public void handle(ASTLessEqualExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" <= ");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Handles the processing of an ASTGreaterEqualExpression node by appending
   * the necessary components to the query string. Constructs a SQL-style
   * 'greater than or equal to' condition using the left and right expressions
   * of the node.
   *
   * @param node the ASTGreaterEqualExpression node representing the
   *             greater than or equal to expression in the abstract syntax tree
   */
  @Override
  public void handle(ASTGreaterEqualExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" >= ");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Handles an ASTLessThanExpression node by appending the appropriate SQL
   * representation of a "less than" comparison to the query builder.
   *
   * @param node the ASTLessThanExpression node to handle, representing a
   *             "less than" comparison in the abstract syntax tree
   */
  @Override
  public void handle(ASTLessThanExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" < ");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Handles the ASTGreaterThanExpression node by appending its representation
   * to the query builder's "where" clause. This method processes the left
   * and right child nodes of the expression and adds the greater-than operator
   * between them.
   *
   * @param node the ASTGreaterThanExpression node to be processed
   */
  @Override
  public void handle(ASTGreaterThanExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" > ");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Handles the processing of an {@link ASTEqualsExpression} node during traversal.
   * Constructs the query condition for the equality expression by appending appropriate
   * clauses to the query builder. Special handling is applied for collection types to manage
   * collection equality appropriately.
   *
   * @param node the {@link ASTEqualsExpression} node representing the equality expression
   *             in the abstract syntax tree (AST)
   */
  @Override
  public void handle(ASTEqualsExpression node) {
    // Because of typecheck on shadowed variable: Throws: 0xFD226 internal error: resolved 2 occurrences of Symbol, but expected only one
    // Just a Hotfix should probably be handled differently
    SymTypeExpression left = null;
    SymTypeExpression right = null;
    if (!(node.getLeft() instanceof ASTNameExpression) && !(node.getRight() instanceof ASTNameExpression )) {
      left = TypeCheck3.typeOf(node.getLeft());
      right = TypeCheck3.typeOf(node.getRight());
    }

    if (left != null && right != null) {
      if(isCollectionType(left) || isCollectionType(right)) {
        queryBuilder.appendToWhere("(all( x IN ");
        node.getLeft().accept(getTraverser());
        queryBuilder.appendToWhere(" WHERE x IN ");
        node.getRight().accept(getTraverser());
        queryBuilder.appendToWhere(") AND all(y IN ");
        node.getRight().accept(getTraverser());
        queryBuilder.appendToWhere(" WHERE y IN ");
        node.getLeft().accept(getTraverser());
        queryBuilder.appendToWhere("))");
        return;
      }
    }

    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" = ");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Handles the processing of an {@link ASTNotEqualsExpression} node.
   * This method analyzes the left and right expressions of the node to generate appropriate query
   * structures based on their types, specifically handling cases where collection types are involved.
   *
   * @param node the {@link ASTNotEqualsExpression} node to be processed. This represents a "not equals"
   *             expression in the abstract syntax tree and contains left and right subexpressions.
   */
  @Override
  public void handle(ASTNotEqualsExpression node) {
    // Because of typecheck on shadowed variable: Throws: 0xFD226 internal error: resolved 2 occurrences of Symbol, but expected only one
    // Just a Hotfix should probably be handled differently
    SymTypeExpression left = null;
    SymTypeExpression right = null;
    if (!(node.getLeft() instanceof ASTNameExpression) && !(node.getRight() instanceof ASTNameExpression )) {
      left = TypeCheck3.typeOf(node.getLeft());
      right = TypeCheck3.typeOf(node.getRight());
    }

    if (left != null && right != null) {
      if (isCollectionType(left) || isCollectionType(right)) {
        queryBuilder.appendToWhere("NOT (all( x IN ");
        node.getLeft().accept(getTraverser());
        queryBuilder.appendToWhere(" WHERE x IN ");
        node.getRight().accept(getTraverser());
        queryBuilder.appendToWhere(") AND all(y IN ");
        node.getRight().accept(getTraverser());
        queryBuilder.appendToWhere(" WHERE y IN ");
        node.getLeft().accept(getTraverser());
        queryBuilder.appendToWhere("))");
        return;
      }
    }

    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" <> ");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  // Logical Expressions
  /**
   * Handles the traversal and query building for a logical AND operation in an abstract syntax tree.
   *
   * @param node the ASTBooleanAndOpExpression node representing the logical AND operation; it contains
   *             the left and right operands of the expression.
   */
  @Override
  public void handle(ASTBooleanAndOpExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" AND ");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Processes an `ASTBooleanOrOpExpression` node by appending its
   * logical OR operation representation to the query builder's WHERE clause.
   *
   * @param node the `ASTBooleanOrOpExpression` node representing
   *             the logical OR operation in the syntax tree
   */
  @Override
  public void handle(ASTBooleanOrOpExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" OR ");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Handles the processing of an ASTLogicalNotExpression node in the abstract syntax tree.
   * Appends a "not" clause to the query builder's WHERE clause, processes the inner expression,
   * and ensures proper syntax closure.
   *
   * @param node the ASTLogicalNotExpression node to process, representing a logical NOT expression
   */
  @Override
  public void handle(ASTLogicalNotExpression node) {
    queryBuilder.appendToWhere("not (");
    node.getExpression().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  // Prefix Expressions
  /**
   * Handles the ASTPlusPrefixExpression node during traversal.
   * This method processes prefix expressions with the '+' symbol
   * and appends it to the query builder's where clause while traversing
   * the inner expression of the node.
   *
   * @param node the ASTPlusPrefixExpression node representing a prefix
   *             expression in the syntax tree to be handled
   */
  @Override
  public void handle(ASTPlusPrefixExpression node) {
    queryBuilder.appendToWhere("+");
    node.getExpression().accept(getTraverser());
  }

  /**
   * Handles the ASTMinusPrefixExpression node in the abstract syntax tree.
   * This method processes the minus prefix expression by appending
   * a "-" symbol to the query builder's WHERE clause and then traversing
   * its child expression node.
   *
   * @param node the ASTMinusPrefixExpression node to be handled
   */
  @Override
  public void handle(ASTMinusPrefixExpression node) {
    queryBuilder.appendToWhere("-");
    node.getExpression().accept(getTraverser());
  }

  // Mathematical Expressions
  /**
   * Handles an ASTPlusExpression node by traversing its left and right subexpressions
   * and appending the mathematical addition operation to the query builder.
   *
   * @param node the ASTPlusExpression node to be processed, containing the left
   *             and right subexpressions involved in the addition operation
   */
  @Override
  public void handle(ASTPlusExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere("+");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Handles the ASTMinusExpression node and appends its representation to the queryBuilder's WHERE clause.
   * Constructs the expression by enclosing it in parentheses, processing the left operand, appending the subtraction operator,
   * and processing the right operand.
   *
   * @param node the ASTMinusExpression node representing a subtraction expression in the abstract syntax tree
   */
  @Override
  public void handle(ASTMinusExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere("-");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Handles the processing of an ASTMultExpression node by constructing the
   * corresponding part of the query in the `queryBuilder`.
   *
   * @param node The ASTMultExpression node that represents a multiplication
   *             expression in the abstract syntax tree.
   */
  @Override
  public void handle(ASTMultExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere("*");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Handles the processing of an ASTDivideExpression node.
   * This method generates a division operation in the query by traversing
   * the left and right operands of the divide expression and appending the
   * corresponding SQL representation to the query builder's "WHERE" clause.
   *
   * @param node the ASTDivideExpression node representing a division operation
   *             in the abstract syntax tree
   */
  @Override
  public void handle(ASTDivideExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere("/");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Handles an ASTModuloExpression node by appending its representation
   * to the queryBuilder in the format of "(left % right)".
   *
   * @param node the ASTModuloExpression node to be processed. It contains
   *             the left and right expressions to be combined using the
   *             modulo operator.
   */
  @Override
  public void handle(ASTModuloExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere("%");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  // BitExpressions
  /*LeftShiftExpression
   *   a << b => a * (2 ^ (((b % 32))
   */

  /**
   * Handles the processing of an ASTLeftShiftExpression node and updates the queryBuilder accordingly.
   * Logs a warning message if a LeftShiftExpression is used in OCL.
   *
   * @param node the ASTLeftShiftExpression node to process
   */
  @Override
  public void handle(ASTLeftShiftExpression node) {
    Log.warn("[WARN]: OCL with LeftShiftExpression used!");

    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" * (2 ^ (((");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(" % 32) + 32) % 32))");
  }

  /*RightShiftExpression
   *  a >> b => floor(a / (2 ^ ((((b % 32) + 32) % 32))))
   */

  /**
   * Handles the ASTRightShiftExpression node by translating it into a specific query fragment.
   * This method processes the right shift operation, logs a warning,
   * and appends the relevant calculation to the query builder.
   *
   * @param node The ASTRightShiftExpression node representing the right shift operation in the syntax tree.
   */
  @Override
  public void handle(ASTRightShiftExpression node) {
    Log.warn("[WARN]: OCL with RightShiftExpression used!");

    queryBuilder.appendToWhere("floor(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" / (2 ^ ((((");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(" % 32) + 32) % 32))))");
  }

  /*LogicalRightShiftExpression
   *  a >>> b  =>  floor( (a + CASE WHEN n = 0 THEN 0 WHEN a < 0 THEN 4294967296 ELSE 0 END) / (2 ^ n) )
   *      where n = (((b % 32) + 32) % 32)
   */

  /**
   * Handles the traversal and processing of an ASTLogicalRightShiftExpression node.
   * This method modifies the query builder to append appropriate SQL-compatible expressions
   * for logical right shift operations during the traversal of the AST. Additionally, it logs
   * a warning if such an expression is encountered.
   *
   * @param node the ASTLogicalRightShiftExpression node representing a logical right shift
   *             operation in the abstract syntax tree. It contains the left and right
   *             operands, which are processed and transformed into SQL-compatible expressions.
   */
  @Override
  public void handle(ASTLogicalRightShiftExpression node) {
    Log.warn("[WARN]: OCL with LogicalRightShiftExpression used!");

    queryBuilder.appendToWhere("floor((");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" + CASE WHEN (((");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(" % 32) + 32) % 32) = 0 THEN 0 WHEN ");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" < 0 THEN 4294967296 ELSE 0 END) / (2 ^ (((");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(" % 32) + 32) % 32)))");
  }


  // SetExpressions

  /**
   * Handles an ASTSetInExpression node, processes its element and set components,
   * and appends the resulting query clause to the query builder's WHERE clause.
   *
   * @param node the ASTSetInExpression node to handle. Contains the element
   *             and the set to evaluate for containment.
   */
  @Override
  public void handle(ASTSetInExpression node) {
    ASTExpression element = node.getElem();
    ASTExpression set = node.getSet();

    StringBuilder clauseBuilder = new StringBuilder();

    // traverse deeper to handle ASTBracketExpression
    if(element instanceof ASTBracketExpression){
      element.accept(getTraverser());
      if(queryBuilder.getCurrentBlock().getWhereClause().toString().length() > 0) {
        // when element is already resolved in other visitors/handlers
        element = null;
      }
    }

    SetContainmentUtils.handleSetContainmentLogic(element, set, this.param, clauseBuilder);
    queryBuilder.appendToWhere(clauseBuilder.toString());
  }

  /**
   * Handles the ASTSetNotInExpression node by generating the corresponding query clause
   * and appending it to the WHERE clause in the query builder. It processes the element
   * and set expressions, and applies the appropriate NOT IN logic using utility methods.
   *
   * @param node the ASTSetNotInExpression node representing the "NOT IN" expression in the AST
   */
  @Override
  public void handle(ASTSetNotInExpression node) {
    ASTExpression element = node.getElem();
    ASTExpression set = node.getSet();

    StringBuilder clauseBuilder = new StringBuilder("NOT ");

    // traverse deeper to handle ASTBracketExpression
    if(element instanceof ASTBracketExpression){
      element.accept(getTraverser());
      if(queryBuilder.getCurrentBlock().getWhereClause().toString().length() > 0) {
        // when element is already resolved in other visitors/handlers
        element = null;
      }
    }

    SetContainmentUtils.handleSetContainmentLogic(element, set, this.param, clauseBuilder);
    queryBuilder.appendToWhere(clauseBuilder.toString());
  }

  /**
   * Handles the processing of an ASTSetComprehension node in the abstract syntax tree (AST).
   * Constructs a query representation by appending the set comprehension's elements
   * and conditions to the query builder.
   *
   * @param node the ASTSetComprehension node representing a set comprehension
   */
  @Override
  public void handle(ASTSetComprehension node) {

    queryBuilder.appendToWhere("[");

    List<ASTSetComprehensionItem> comprehensionItems= node.getSetComprehensionItemList();
    for (ASTSetComprehensionItem item : comprehensionItems) {
      if (item.isPresentExpression()) {
        queryBuilder.appendToWhere(" WHERE ");
      }
      item.accept(getTraverser());
    }

    queryBuilder.appendToWhere("|");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere("]");
  }

  /**
   * Handles the processing of an {@code ASTGeneratorDeclaration} node. This method appends
   * the name of the provided node to a query's "WHERE" clause and ensures that its
   * expression is further traversed and processed.
   *
   * @param node the {@code ASTGeneratorDeclaration} node that is being processed.
   */
  @Override
  public void handle(ASTGeneratorDeclaration node) {
    queryBuilder.appendToWhere(node.getName());
    queryBuilder.appendToWhere(" IN ");
    node.getExpression().accept(getTraverser());
  }

  /**
   * Handles the parsing of an AST node representing a set variable declaration.
   * Currently, variable declarations in set comprehensions are not supported.
   * Logs an error message indicating this limitation and details about the
   * unsupported node that will be ignored.
   *
   * @param node the ASTSetVariableDeclaration node representing the variable
   *             declaration in a set comprehension
   */
  @Override
  public void handle(ASTSetVariableDeclaration node) {
    Log.error("Varaible declaration in Set Comprehension not supported yet! " + OCLMill.prettyPrint(node, false) + " Will be ignored!");
  }

  /**
   * Handles the processing of an {@link ASTSetEnumeration} node. This method constructs a query condition
   * based on the set enumeration items in the given node.
   *
   * @param node the {@link ASTSetEnumeration} node to handle, which contains a list of set collection items
   *             that can either be value ranges or individual literals.
   */
  @Override
  public void handle(ASTSetEnumeration node) {
    List<ASTSetCollectionItem> items = node.getSetCollectionItemList();
    if (items.isEmpty()) {
     queryBuilder.appendToWhere("[]");
     return;
    }

    // Value range or one literal
    if (items.size() == 1) {
      items.get(0).accept(getTraverser());
      return;
    }

    for (int i = 0; i < items.size(); i++) {

      if (items.get(i) instanceof ASTSetValueRange) {
        // Range given, so no [ ] allowed
        items.get(i).accept(getTraverser());
      } else {
        // Literal
        queryBuilder.appendToWhere("[");
        items.get(i).accept(getTraverser());
        queryBuilder.appendToWhere("]");
      }

      // Concatenate the rest of the list enumeration
      if (i < items.size() - 1) {
        queryBuilder.appendToWhere(" + ");
      }
    }
  }

  /**
   * Handles the processing of an {@code ASTSetValueRange} node within an abstract syntax tree (AST).
   * This method evaluates the lower and upper bounds of the value range, ensuring they are of type integer.
   * If the bounds are valid, it constructs a portion of a query with proper range conditions.
   *
   * @param node the {@code ASTSetValueRange} node to be handled. It contains details
   *             about the lower and upper bounds of the range to process.
   */
  @Override
  public void handle(ASTSetValueRange node) {

    SymTypeExpression lowerType = TypeCheck3.typeOf(node.getLowerBound());
    SymTypeExpression upperType = TypeCheck3.typeOf(node.getUpperBound());

    if (!lowerType.isPrimitive() || !"int".equals(lowerType.print())
            || !upperType.isPrimitive() || !"int".equals(upperType.print())) {
      Log.warn("SetValueRange bounds must be integers, got: "
              + lowerType.print() + " .. " + upperType.print());
      return;
    }

    queryBuilder.appendToWhere("CASE WHEN ");
    node.getLowerBound().accept(getTraverser());
    queryBuilder.appendToWhere(" <= ");
    node.getUpperBound().accept(getTraverser());

    queryBuilder.appendToWhere(" THEN range(");
    node.getLowerBound().accept(getTraverser());
    queryBuilder.appendToWhere(", ");
    node.getUpperBound().accept(getTraverser());
    queryBuilder.appendToWhere(")");

    queryBuilder.appendToWhere(" ELSE range(");
    node.getLowerBound().accept(getTraverser());
    queryBuilder.appendToWhere(", ");
    node.getUpperBound().accept(getTraverser());
    queryBuilder.appendToWhere(", -1)");
    queryBuilder.appendToWhere(" END");
  }

  // OCL Expressions

  /**
   * Handles the processing of an ASTImpliesExpression node by appending the corresponding logical
   * operation (implication) to the query. The implication is represented as "NOT left OR right"
   * in the query.
   *
   * @param node the ASTImpliesExpression node to process
   */
  @Override
  public void handle(ASTImpliesExpression node) {
    queryBuilder.appendToWhere("(");
    queryBuilder.appendToWhere("NOT ");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" OR ");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Handles the processing of an ASTEquivalentExpression node by building an appropriate
   * SQL equivalent expression into the queryBuilder's WHERE clause.
   *
   * @param node the ASTEquivalentExpression node to process, containing the left and
   *             right expressions to be compared for equality
   */
  @Override
  public void handle(ASTEquivalentExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" = ");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Handles the processing of an ASTIfThenElseExpression node by building a query
   * representation for its conditional expression in a CASE-WHEN-THEN-ELSE format.
   *
   * @param node the ASTIfThenElseExpression node representing the conditional statement
   */
  @Override
  public void handle(ASTIfThenElseExpression node) {
    queryBuilder.appendToWhere("(CASE WHEN ");
    node.getCondition().accept(getTraverser());
    queryBuilder.appendToWhere(" THEN ");
    node.getThenExpression().accept(getTraverser());
    queryBuilder.appendToWhere(" ELSE ");
    node.getElseExpression().accept(getTraverser());
    queryBuilder.appendToWhere(" END)");
  }


  // Quantifiers
  /* ForallExpression
  * forall A a in b.setOfAs : expression => all(a IN b.setOfAs WHERE expression)
   */
  @Override
  public void handle(ASTForallExpression node) {

    List<ASTInDeclaration> inDecs = node.getInDeclarationList();
    for (ASTInDeclaration inDec : inDecs) {
      queryBuilder.appendToWhere("\n\tall(");

      inDec.accept(getTraverser());

      queryBuilder.appendToWhere(" WHERE ");
    }

    node.getExpression().accept(getTraverser());

    for (int i = 0; i < inDecs.size(); i++) {
      queryBuilder.appendToWhere(")");
    }
  }

  /* ExistsExpression
   * exists A a in b.setOfAs : expression => any(a IN b.setOfAs WHERE expression)
   */
  @Override
  public void handle(ASTExistsExpression node) {
    List<ASTInDeclaration> inDecs = node.getInDeclarationList();
    for (ASTInDeclaration inDec : inDecs) {
      queryBuilder.appendToWhere("\n\tany(");

      inDec.accept(getTraverser());

      queryBuilder.appendToWhere(" WHERE ");
    }

    node.getExpression().accept(getTraverser());

    for (int i = 0; i < inDecs.size(); i++) {
      queryBuilder.appendToWhere(")");
    }
  }


  // optional operators (elvis operators)
   /**
    * The ASTOptionalExpressionPrefix gets appended to WHERE clause in as coalesce(left, right)
    *
   * @param node ASTOptionalExpressionPrefix to be considered for translation
   */
  @Override
  public void handle(ASTOptionalExpressionPrefix node) {
    queryBuilder.appendToWhere("coalesce(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(", ");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

   /**
    * ASTOptionalLessThanExpression gets appended to WHERE clause in as (left IS NOT NULL AND left < right)
   * @param node ASTOptionalLessThanExpression to be considered for translation
   */
  @Override
  public void handle(ASTOptionalLessThanExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" IS NOT NULL AND ");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" < ");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Handles the processing of an ASTOptionalEqualsExpression node in the
   * context of constructing a query. This method constructs a condition
   * for the specified expression, appending a check for non-null values
   * and an equality constraint to the query builder.
   *
   * @param node the ASTOptionalEqualsExpression node representing the
   *             optional equals expression to be processed
   */
  @Override
  public void handle(ASTOptionalEqualsExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" IS NOT NULL AND ");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" = ");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Handles the processing of an {@code ASTOptionalSimilarExpression} node by constructing the
   * necessary SQL-like "WHERE" clause based on its components.
   *
   * @param node the {@code ASTOptionalSimilarExpression} node to be handled, which contains
   *             the left and right expressions to be compared
   */
  @Override
  public void handle(ASTOptionalSimilarExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" IS NOT NULL AND ");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" = ");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Handles the ASTOptionalNotSimilarExpression node by appending a conditional
   * expression that checks for non-nullability and inequality to the query builder's
   * WHERE clause.
   *
   * @param node the ASTOptionalNotSimilarExpression node to be processed, which
   *             contains the left and right expressions for comparison
   */
  @Override
  public void handle(ASTOptionalNotSimilarExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" IS NOT NULL AND ");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" <> ");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Handles the processing of an ASTOptionalGreaterEqualExpression node by constructing
   * a SQL-like conditional WHERE expression that represents a "greater than or equal to"
   * condition, considering the optional aspect of the left operand.
   *
   * @param node the ASTOptionalGreaterEqualExpression node to be processed. It contains
   *             the left and right operands that represent the expression to be evaluated.
   */
  @Override
  public void handle(ASTOptionalGreaterEqualExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" IS NOT NULL AND ");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" >= ");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Handles the processing of an ASTOptionalNotEqualsExpression node by appending
   * specific conditions to the query's WHERE clause. This method ensures that the
   * left-hand side is checked for non-nullity and inequality with the right-hand side.
   *
   * @param node the ASTOptionalNotEqualsExpression node to be processed
   */
  @Override
  public void handle(ASTOptionalNotEqualsExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" IS NOT NULL AND ");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" <> ");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Processes the given ASTOptionalLessEqualExpression node by appending a condition to the queryBuilder.
   * The condition checks if the left operand is not null and is less than or equal to the right operand.
   *
   * @param node the ASTOptionalLessEqualExpression node representing the expression to be handled
   */
  @Override
  public void handle(ASTOptionalLessEqualExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" IS NOT NULL AND ");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" <= ");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }

  /**
   * Handles the provided ASTOptionalGreaterThanExpression node and constructs
   * the corresponding query by appending its components to the query builder.
   *
   * @param node the ASTOptionalGreaterThanExpression node to be processed
   */
  @Override
  public void handle(ASTOptionalGreaterThanExpression node) {
    queryBuilder.appendToWhere("(");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" IS NOT NULL AND ");
    node.getLeft().accept(getTraverser());
    queryBuilder.appendToWhere(" > ");
    node.getRight().accept(getTraverser());
    queryBuilder.appendToWhere(")");
  }


  /**
   * Handles the provided ASTLetinExpression node by traversing its variable
   * declarations and expression.
   *
   * @param node the ASTLetinExpression node to handle
   */
  @Override
  public void handle(ASTLetinExpression node) {
    for (ASTOCLVariableDeclaration varDec : node.getOCLVariableDeclarationList()){
      varDec.accept(getTraverser());
    }
    node.getExpression().accept(getTraverser());
  }

  /**
   * Handles the processing of an OCLVariableDeclaration node within the AST.
   * This method builds a query by appending necessary components based on the
   * provided variable declaration and its expression to the "WITH" clause. Ensures no duplicate
   * parameter names exist in the result columns.
   *
   * @param node the ASTOCLVariableDeclaration node representing a variable
   *             declaration within the Abstract Syntax Tree (AST)
   */
  @Override
  public void handle(ASTOCLVariableDeclaration node) {
    queryBuilder.beginWithLine();
    String currentParam = node.getName();
    allParams.remove(currentParam); // To prevent Multiple result columns with the same name
    queryBuilder.appendToWhere(String.join(", ", allParams) + ", ");
    node.getExpression().accept(getTraverser());
    queryBuilder.appendToWhere(" AS " + currentParam);
    allParams.add(currentParam);
    queryBuilder.endWithLine();
  }

  /**
   * Handles the processing of an {@link ASTInDeclaration} node in the abstract syntax tree (AST).
   * Constructs a string representation of the variables in the "IN" declaration and appends it to
   * the query builder's "WHERE" clause. The method then processes the associated expression
   * within the node.
   *
   * @param node the {@link ASTInDeclaration} node that contains the variables and expression
   *             to be processed and appended to the query builder.
   */
  @Override
  public void handle(ASTInDeclaration node) {

    List<ASTInDeclarationVariable> vars = node.getInDeclarationVariableList();
    StringBuilder varsString = new StringBuilder();
    for (int j = 0; j < vars.size(); j++) {
      varsString.append(vars.get(j).getName());
      if (j < vars.size() - 1) {
        varsString.append(", ");
      }
    }

    queryBuilder.appendToWhere(varsString + " IN ");

    node.getExpression().accept(getTraverser());
  }

  /**
   * Handles the provided ASTNameExpression node and appends its relevant name
   * to the query builder's WHERE clause. The behavior is influenced by the
   * implicit context state.
   *
   * @param node the ASTNameExpression node containing the name to be appended
   *             to the query builder's WHERE clause
   */
  @Override
  public void handle(ASTNameExpression node) {
    if (node.getName().equals("this")) {
      queryBuilder.appendToWhere(param);
      return;
    }
    if (isImplicitContext) {
      queryBuilder.appendToWhere(param + "." + node.getName());
    } else {
      queryBuilder.appendToWhere(node.getName());
    }
  }

  /**
   * Handles an ASTFieldAccessExpression by determining its type and, depending on that, assures a correct field acces
   * in Cypher
   *
   * @param node the ASTFieldAccessExpression representing a field access in the abstract syntax tree
   */
  @Override
  public void handle(ASTFieldAccessExpression node) {
    String name = node.getName();

    SymTypeExpression type = null;
    SymTypeExpression exprType = TypeCheck3.typeOf(node.getExpression());
    if (exprType.isIntersectionType()) {
      var parts = exprType.asIntersectionType().getIntersectedTypeSet();
      if (!parts.isEmpty() &&
              parts.stream().map(SymTypeExpression::printFullName).distinct().count() == 1) {
        var owner = parts.iterator().next();
        if (owner.isObjectType()) {
          var varSymOpt = owner.asObjectType().getTypeInfo()
                  .getSpannedScope().resolveVariable(name);
          if (varSymOpt.isPresent()) {
            type = varSymOpt.get().getType();
          }
        }
      }
    }
    if (type == null) {
      type = TypeCheck3.typeOf(node);
    }

    // Check for relationship access
    SymTypeExpression fieldType = null;

    boolean isColl =
            isCollectionType(type)
            && type.isGenericType()
            && !type.asGenericType().getArgumentList().isEmpty();

    if (isColl) {
      fieldType = type.asGenericType().getArgument(0);
    }

    boolean toManyDomain = isColl && isDomainType(fieldType); // List\Set<DomainType>
    boolean toOneDomain = !isColl && isDomainType(type);

    if (toManyDomain || toOneDomain) {

      if (toOneDomain){
        queryBuilder.appendToWhere("(head(");
      }
      queryBuilder.appendToWhere("[(");
      node.getExpression().accept(getTraverser());
      queryBuilder.appendToWhere(")-[:");
      queryBuilder.appendToWhere(name);
      queryBuilder.appendToWhere("]->(v:");
      queryBuilder.appendToWhere((toManyDomain ? fieldType : type).getTypeInfo().getName());
      queryBuilder.appendToWhere(") | v ]");
      if (toOneDomain){
        queryBuilder.appendToWhere("))");
      }
      return;
    }

    // List are capital lettered in DB? Thus, if list capitalize
    if (isCollectionType(type)) {
      name = name.substring(0, 1).toUpperCase() + name.substring(1);
    }
    node.getExpression().accept(getTraverser());
    queryBuilder.appendToWhere("." + name);
  }

  /**
   * Handles the processing of an ASTTypeIfExpression node and appends the corresponding
   * SQL-like "CASE WHEN" expression to the query builder.
   *
   * @param node the ASTTypeIfExpression node to be processed, which contains a variable name,
   *             a type, a "then" expression, and an "else" expression.
   */
  @Override
  public void handle(ASTTypeIfExpression node) {
    String var = node.getName();

    String label = OCLMill.prettyPrint(node.getMCType(), false);

    queryBuilder.appendToWhere("(");
    queryBuilder.appendToWhere("CASE WHEN ");
    queryBuilder.appendToWhere(var + ":" + label);
    queryBuilder.appendToWhere(" THEN ");
    node.getThenExpression().accept(getTraverser());
    queryBuilder.appendToWhere(" ELSE ");
    node.getElseExpression().accept(getTraverser());
    queryBuilder.appendToWhere(" END");
    queryBuilder.appendToWhere(")");
  }

  /**
   * Handles the processing of the given ASTIterateExpression node to generate
   * the corresponding query structure. Validates the iteration variable count
   * and ensures the accumulator variable name matches the expected name.
   *
   * @param node the ASTIterateExpression node to process, which contains the
   *             iteration and accumulation definitions, alongside their
   *             associated expressions.
   * @throws UnsupportedOperationException if the number of iteration variables
   *                                       is not exactly one.
   */
  @Override
  public void handle(ASTIterateExpression node) {
    var inDecl = node.getIteration();
    var vars = inDecl.getInDeclarationVariableList();
    if (vars.size() != 1) {
      throw new UnsupportedOperationException("iterate: exactly one iteration variable supported for now.");
    }
    String iterVar = vars.get(0).getName();

    String accVar = node.getInit().getName();
    String nameAfterColon = node.getName();
    if (!accVar.equals(nameAfterColon)) {
      Log.warn("[Iterate] Accumulator name mismatch: init=" + accVar + " vs Name=" + nameAfterColon);
    }

    queryBuilder.appendToWhere("(");
    queryBuilder.appendToWhere("reduce(" + accVar + " = ");
    node.getInit().getExpression().accept(getTraverser());
    queryBuilder.appendToWhere(", ");

    inDecl.accept(getTraverser());

    queryBuilder.appendToWhere(" | ");
    node.getValue().accept(getTraverser());
    queryBuilder.appendToWhere("))");
  }

  /**
   * Handles an ASTAnyExpression node to generate a query builder's "where" clause
   * based on the expression type and its structure. Specifically processes cases
   * for ASTFieldAccessExpression with domain types and other expression types.
   *
   * @param node the ASTAnyExpression node to be handled, representing a part of the
   *        abstract syntax tree to be analyzed and transformed into a query segment
   */
  @Override
  public void handle(ASTAnyExpression node) {
    ASTExpression expr = node.getExpression();
    SymTypeExpression t = TypeCheck3.typeOf(expr);

    if (expr instanceof ASTFieldAccessExpression && isDomainType(t)) {
      ASTFieldAccessExpression fae = (ASTFieldAccessExpression) expr;

      String elemLabel = simpleName(elementLabelFrom(t));
      String tmp = fresh("_any");

      queryBuilder.appendToWhere("head([(");
      queryBuilder.appendToWhere(param);
      queryBuilder.appendToWhere(")-[:");
      queryBuilder.appendToWhere(fae.getName());
      queryBuilder.appendToWhere("]->(");
      queryBuilder.appendToWhere(tmp);
      queryBuilder.appendToWhere(":");
      queryBuilder.appendToWhere(elemLabel);
      queryBuilder.appendToWhere(") | ");
      queryBuilder.appendToWhere(tmp);
      queryBuilder.appendToWhere("])");
    } else {
      queryBuilder.appendToWhere("head(");
      expr.accept(getTraverser());
      queryBuilder.appendToWhere(")");
    }
  }

  /**
   * Generates a label string for the provided SymTypeExpression.
   *
   * @param t the SymTypeExpression from which the label is to be derived;
   *          can be a generic type, object type, or other type.
   * @return a string representing the label of the type; returns the full name
   *         of the first generic argument if the type is generic with arguments,
   *         the full name of the object type if it is an object type,
   *         or "Object" by default.
   */
  private String elementLabelFrom(SymTypeExpression t) {
    if (t.isGenericType() && t.asGenericType().sizeArguments() > 0) {
      return t.asGenericType().getArgument(0).printFullName();
    }
    if (t.isObjectType()) {
      return t.asObjectType().printFullName();
    }
    return "Object";
  }

  /**
   * Extracts the simple name from a fully qualified name by removing the package portion.
   *
   * @param qn the fully qualified name of a class, interface, or other element
   * @return the simple name without the package path, or the original string if no '.' character is found
   */
  private String simpleName(String qn) {
    int i = qn.lastIndexOf('.');
    return i >= 0 ? qn.substring(i + 1) : qn;
  }

  private int tmpCnt = 0;
  private String fresh(String p) { return p + (tmpCnt++); }

  /**
   * Handles the given ASTLiteralExpression node, extracting its literal source if it is of type
   * ASTMCCommonLiteralsNode, and appending it to the query builder's WHERE clause. If the literal
   * does not have a source or is not of the expected type, logs a warning message.
   *
   * @param node the ASTLiteralExpression node to handle
   */
  @Override
  public void handle(ASTLiteralExpression node) {
    ASTLiteral lit = node.getLiteral();

    if (lit instanceof ASTMCCommonLiteralsNode) {
      LiteralUtils.extractLiteralSource((ASTMCCommonLiteralsNode) lit)
              .ifPresentOrElse(
                      src -> queryBuilder.appendToWhere(src),
                      () -> Log.error("[WARN] Literal without source: " + lit.getClass().getSimpleName())
              );
    } else {
      Log.error("[WARN] Literal not instance of ASTMCCommonLiteralsNode: " + lit.getClass().getSimpleName());
    }
  }

  /**
   * Determines if the given SymTypeExpression represents a type within the "Mydomain" domain.
   *
   * @param symTypeExpression the SymTypeExpression object to be checked; it may be null
   * @return true if the given SymTypeExpression corresponds to a domain type within "Mydomain"; false otherwise
   */
  private boolean isDomainType(SymTypeExpression symTypeExpression) {
    if (symTypeExpression == null) {
      return false;
    }

    return symTypeExpression.isObjectType() && symTypeExpression.asObjectType().printFullName().startsWith("Mydomain")
            || symTypeExpression.isGenericType() && isDomainType(symTypeExpression.asGenericType().getArgument(0));
  }

  /**
   * Checks whether the provided type expression represents a collection type.
   *
   * @param type the SymTypeExpression to be evaluated
   * @return true if the provided type expression corresponds to a collection type such as List, Set, or Collection; false otherwise
   */
  private boolean isCollectionType(SymTypeExpression type) {
    if (type.hasTypeInfo()) {
      return "List".equals(type.getTypeInfo().getName())
              || "Set".equals(type.getTypeInfo().getName())
              || "Collection".equals(type.getTypeInfo().getName());
    } else {
      return false; // May result in wrong return
    }
  }

}
