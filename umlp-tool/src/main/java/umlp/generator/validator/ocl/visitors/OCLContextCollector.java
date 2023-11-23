package umlp.generator.validator.ocl.visitors;

import de.monticore.ocl.ocl._ast.ASTOCLArtifact;
import de.monticore.ocl.ocl._ast.ASTOCLCompilationUnit;
import de.monticore.ocl.ocl._ast.ASTOCLConstraint;
import de.monticore.ocl.ocl._ast.ASTOCLConstructorSignature;
import de.monticore.ocl.ocl._ast.ASTOCLContextDefinition;
import de.monticore.ocl.ocl._ast.ASTOCLInvariant;
import de.monticore.ocl.ocl._ast.ASTOCLMethodSignature;
import de.monticore.ocl.ocl._ast.ASTOCLOperationConstraint;
import de.monticore.ocl.ocl._ast.ASTOCLOperationSignature;
import de.monticore.ocl.ocl._ast.ASTOCLParamDeclaration;
import de.monticore.ocl.ocl._visitor.OCLVisitor2;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OCLContextCollector implements OCLVisitor2 {

  private ASTOCLConstraint currentConstraint;
  private final Map<ASTOCLConstraint, List<ASTOCLParamDeclaration>> paramDeclarationsForConstraints;

  public OCLContextCollector(ASTOCLCompilationUnit node) {
    this.paramDeclarationsForConstraints = new HashMap<>();
    this.visit(node);
  }

  public List<ASTMCType> collectOCLContextForConstraint(ASTOCLConstraint constraint) {
    return paramDeclarationsForConstraints.get(constraint).stream()
            .map(ASTOCLParamDeclaration::getMCType)
            .collect(Collectors.toList());
  }

  public List<ASTOCLConstraint> collectConstraintsForType(String typeName) {
    return collectConstraintsForOCLContext(Collections.singletonList(typeName));
  }

  public List<ASTOCLConstraint> collectConstraintsForOCLContext(List<String> typeNames) {
    List<ASTOCLConstraint> result = new ArrayList<>();
    for(ASTOCLConstraint constraint : paramDeclarationsForConstraints.keySet()) {
      if(new HashSet<>(paramDeclarationsForConstraints.get(constraint))
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
  public void visit(ASTOCLConstraint node) {
    currentConstraint = node;
    paramDeclarationsForConstraints.put(currentConstraint, new ArrayList<>());
    if(node instanceof ASTOCLOperationConstraint) {
      this.visit((ASTOCLOperationConstraint) node);
    } else if(node instanceof ASTOCLInvariant) {
      this.visit((ASTOCLInvariant) node);
    }
  }

  @Override
  public void visit(ASTOCLContextDefinition node) {
    paramDeclarationsForConstraints.get(currentConstraint).add(node.getOCLParamDeclaration());
  }

  @Override
  public void visit(ASTOCLMethodSignature node) {
    paramDeclarationsForConstraints
            .get(currentConstraint)
            .addAll(
                    node.getOCLParamDeclarationList()
            );
  }

  @Override
  public void visit(ASTOCLConstructorSignature node) {
    paramDeclarationsForConstraints
            .get(currentConstraint)
            .addAll(
                    node.getOCLParamDeclarationList()
            );
  }

  @Override
  public void visit(ASTOCLCompilationUnit node) {
    this.visit(node.getOCLArtifact());
  }

  @Override
  public void visit(ASTOCLArtifact node) {
    node.getOCLConstraintList().forEach(this::visit);
  }

  @Override
  public void visit(ASTOCLInvariant node) {
    node.getOCLContextDefinitionList().forEach(this::visit);
  }

  @Override
  public void visit(ASTOCLOperationConstraint node) {
    this.visit(node.getOCLOperationSignature());
  }

  @Override
  public void visit(ASTOCLOperationSignature node) {
    if(node instanceof ASTOCLMethodSignature) {
      this.visit((ASTOCLMethodSignature) node);
    } else if(node instanceof ASTOCLConstructorSignature) {
      this.visit((ASTOCLConstructorSignature) node);
    }
  }

  public List<ASTOCLParamDeclaration> collectParamDeclarationsForConstraint(ASTOCLConstraint constraint) {
    return paramDeclarationsForConstraints.get(constraint);
  }
}
