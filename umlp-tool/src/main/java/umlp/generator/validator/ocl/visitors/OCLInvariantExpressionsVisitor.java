package umlp.generator.validator.ocl.visitors;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.ocl.ocl._ast.ASTOCLInvariant;
import de.monticore.ocl.ocl._ast.ASTOCLParamDeclaration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OCLInvariantExpressionsVisitor implements CommonExpressionsVisitor2 {

  private final ASTOCLInvariant currentInvariant;
  private final Map<ASTOCLInvariant, List<String>> attributeNamesForConstraints;
  private final List<ASTOCLParamDeclaration> paramDeclarations;

  public OCLInvariantExpressionsVisitor(ASTOCLInvariant node, List<ASTOCLParamDeclaration> paramDeclarations) {
    this.currentInvariant = node;
    this.paramDeclarations = paramDeclarations;
    this.attributeNamesForConstraints = new HashMap<>();
    this.visit(node);
  }

  public List<String> collectAttributeNamesForInvariant(ASTOCLInvariant invariant) {
    return attributeNamesForConstraints.get(invariant);
  }

  @Override
  public void visit(ASTFieldAccessExpression node) {
    if(node.getExpression() instanceof ASTNameExpression) {
      Optional<ASTOCLParamDeclaration> domainObjectDeclaration = paramDeclarations.stream().filter(p -> p.getName().equals(((ASTNameExpression) node.getExpression()).getName())).findFirst();
      if(domainObjectDeclaration.isPresent()) {
        String attributeType = domainObjectDeclaration.get().getMCType().printType();
        String attributeName = node.getName();
        attributeNamesForConstraints
                .computeIfAbsent(currentInvariant, a -> new ArrayList<>())
                .add(attributeType + "." + attributeName);
      }
    }
  }
}
