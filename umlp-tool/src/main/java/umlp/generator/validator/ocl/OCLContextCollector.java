package umlp.generator.validator.ocl;

import de.monticore.ocl.ocl._ast.*;
import de.monticore.ocl.ocl._visitor.OCLTraverser;
import de.monticore.ocl.ocl._visitor.OCLVisitor2;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.*;
import java.util.stream.Collectors;

public class OCLContextCollector implements OCLVisitor2 {

  private ASTOCLConstraint currentConstraint;
  private final Map<ASTOCLConstraint, List<ASTMCType>> typesForConstraints;

  public OCLContextCollector(ASTOCLCompilationUnit node) {
    this.typesForConstraints = new HashMap<>();
    this.visit(node);
  }

  public List<ASTMCType> collectOCLContextForConstraint(ASTOCLConstraint constraint) {
    return typesForConstraints.get(constraint);
  }

  public List<ASTOCLConstraint> collectConstraintsForType(String typeName) {
    return collectConstraintsForOCLContext(Collections.singletonList(typeName));
  }

  public List<ASTOCLConstraint> collectConstraintsForOCLContext(List<String> typeNames) {
    List<ASTOCLConstraint> result = new ArrayList<>();
    for(ASTOCLConstraint constraint : typesForConstraints.keySet()) {
      if(new HashSet<>(typesForConstraints.get(constraint))
              .stream()
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
    typesForConstraints.put(currentConstraint, new ArrayList<>());
    if(node instanceof ASTOCLOperationConstraint) {
      this.visit((ASTOCLOperationConstraint) node);
    } else if(node instanceof ASTOCLInvariant) {
      this.visit((ASTOCLInvariant) node);
    }
  }

  @Override
  public void visit(ASTOCLContextDefinition node) {
    typesForConstraints.get(currentConstraint).add(node.getOCLParamDeclaration().getMCType());
  }

  @Override
  public void visit(ASTOCLMethodSignature node) {
    typesForConstraints
            .get(currentConstraint)
            .addAll(
                    node.getOCLParamDeclarationList()
                            .stream()
                            .map(ASTOCLParamDeclaration::getMCType)
                            .collect(Collectors.toList())
            );
  }

  @Override
  public void visit(ASTOCLConstructorSignature node) {
    typesForConstraints
            .get(currentConstraint)
            .addAll(
                    node.getOCLParamDeclarationList()
                            .stream()
                            .map(ASTOCLParamDeclaration::getMCType)
                            .collect(Collectors.toList())
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
}
