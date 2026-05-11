package umlp.generator.ocl;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.ocl.ocl._ast.ASTOCLInvariant;
import oclwitherrormessages.OCLWithErrorMessagesMill;
import oclwitherrormessages._visitor.OCLWithErrorMessagesTraverser;
import oclwitherrormessages._visitor.OCLWithErrorMessagesVisitor2;

import java.util.HashSet;
import java.util.Set;

public class OCLAttributeExtractor implements CommonExpressionsVisitor2, ExpressionsBasisVisitor2, OCLWithErrorMessagesVisitor2 {

  protected Set<String> usedAttributes = new HashSet<>();
  protected String contextParamName;
  protected boolean hasErrorMessages = false;

  public OCLAttributeExtractor(String contextParamName) {
    this.contextParamName = contextParamName;
  }

  public static Result extract(ASTOCLInvariant invariant, String contextParamName) {
    OCLAttributeExtractor extractor = new OCLAttributeExtractor(contextParamName);
    OCLWithErrorMessagesTraverser traverser = OCLWithErrorMessagesMill.traverser();
    traverser.add4CommonExpressions(extractor);
    traverser.add4ExpressionsBasis(extractor);
    traverser.add4OCLWithErrorMessages(extractor);
    invariant.accept(traverser);
    return new Result(extractor.usedAttributes, extractor.hasErrorMessages);
  }

  @Override
  public void visit(ASTFieldAccessExpression node) {
    // If we have p.age, thenfa.getName() is "age" and fa.getExpression() is NameExpression "p"
    // If we have p.address.city, we have a chain.
    // Inner: fa.getName()="address", fa.getExpression()=NameExpression "p"
    // Outer: fa.getName()="city", fa.getExpression()=FieldAccessExpression "p.address"
    
    // We only care about the attribute directly on the context object (e.g. "age" or "address")
    // because that's what we generate get...ValidationError methods for.
    
    if (node.getExpression() instanceof ASTNameExpression) {
      ASTNameExpression nameExpr = (ASTNameExpression) node.getExpression();
      if (nameExpr.getName().equals(contextParamName) || nameExpr.getName().equals("this")) {
        usedAttributes.add(node.getName());
      }
    }
  }

  @Override
  public void visit(ASTNameExpression node) {
    if (!node.getName().equals(contextParamName) && !node.getName().equals("this")) {
      // It might be an implicit attribute access
      usedAttributes.add(node.getName());
    }
  }

  @Override
  public void visit(oclwitherrormessages._ast.ASTOCLInvariant node) {
    if (node.isPresentInvErrorMessages()) {
      hasErrorMessages = true;
    }
  }

  public static class Result {
    public final Set<String> usedAttributes;
    public final boolean hasErrorMessages;

    public Result(Set<String> usedAttributes, boolean hasErrorMessages) {
      this.usedAttributes = usedAttributes;
      this.hasErrorMessages = hasErrorMessages;
    }
  }
}
