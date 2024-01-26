package umlp.generator.validator.ocl.visitors;

import de.monticore.ocl.ocl._ast.ASTOCLCompilationUnit;
import de.monticore.ocl.ocl._ast.ASTOCLConstraint;
import de.monticore.ocl.ocl._ast.ASTOCLConstructorSignature;
import de.monticore.ocl.ocl._ast.ASTOCLContextDefinition;
import de.monticore.ocl.ocl._ast.ASTOCLInvariant;
import de.monticore.ocl.ocl._ast.ASTOCLMethodSignature;
import de.monticore.ocl.ocl._ast.ASTOCLOperationConstraint;
import de.monticore.ocl.ocl._ast.ASTOCLParamDeclaration;
import de.monticore.ocl.ocl._visitor.OCLVisitor2;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OCLContextCollector implements OCLVisitor2 {

  private ASTOCLOperationConstraint currentOperationConstraint;
  private ASTOCLInvariant currentInvariant;
  private final Map<ASTOCLOperationConstraint, List<ASTOCLParamDeclaration>> paramDeclarationsForOperationConstraints;
  private final Map<ASTOCLInvariant, List<ASTOCLParamDeclaration>> paramDeclarationsForInvariants;

  public OCLContextCollector() {
    this.paramDeclarationsForOperationConstraints = new LinkedHashMap<>();
    this.paramDeclarationsForInvariants = new LinkedHashMap<>();
  }

  public List<ASTOCLConstraint> collectConstraintsForType(String typeName) {
    return collectConstraintsForOCLContext(Collections.singletonList(typeName));
  }

  public List<ASTOCLConstraint> collectConstraintsForOCLContext(List<String> typeNames) {
    List<ASTOCLConstraint> result = new ArrayList<>();
    for(ASTOCLConstraint constraint : paramDeclarationsForOperationConstraints.keySet()) {
      if(new HashSet<>(paramDeclarationsForOperationConstraints.get(constraint))
              .stream()
              .map(ASTOCLParamDeclaration::getMCType)
              .map(ASTMCType::printType)
              .collect(Collectors.toSet())
              .containsAll(typeNames)) {
        result.add(constraint);
      }
    }
    return result;
  }

  @Override
  public void visit(ASTOCLOperationConstraint node) {
    currentOperationConstraint = node;
  }

  @Override
  public void visit(ASTOCLInvariant node) {
    currentInvariant = node;
    paramDeclarationsForInvariants.put(node, new ArrayList<>());
  }

  @Override
  public void visit(ASTOCLContextDefinition node) {
    paramDeclarationsForInvariants.get(currentInvariant).add(node.getOCLParamDeclaration());
  }

  @Override
  public void visit(ASTOCLMethodSignature node) {
    paramDeclarationsForOperationConstraints
            .get(currentOperationConstraint)
            .addAll(
                    node.getOCLParamDeclarationList()
            );
  }

  @Override
  public void visit(ASTOCLConstructorSignature node) {
    paramDeclarationsForOperationConstraints
            .get(currentOperationConstraint)
            .addAll(
                    node.getOCLParamDeclarationList()
            );
  }

  public Map<ASTOCLInvariant, List<ASTOCLParamDeclaration>> getParamDeclarationsForInvariants() {
    return paramDeclarationsForInvariants;
  }

  public Map<ASTOCLOperationConstraint, List<ASTOCLParamDeclaration>> getParamDeclarationsForOperationConstraints() {
    return paramDeclarationsForOperationConstraints;
  }
}
