package umlp.generator.validator.ocl;

import de.monticore.ocl.ocl._ast.ASTOCLCompilationUnit;
import de.monticore.ocl.ocl._ast.ASTOCLConstraint;
import de.monticore.ocl.ocl._ast.ASTOCLContextDefinition;
import de.monticore.ocl.ocl._ast.ASTOCLContextDefinitionBuilder;
import de.monticore.ocl.ocl._ast.ASTOCLInvariant;
import de.monticore.ocl.ocl._ast.ASTOCLParamDeclaration;
import de.monticore.ocl.ocl._ast.ASTOCLParamDeclarationBuilder;
import de.monticore.ocl.oclexpressions._ast.ASTForallExpression;
import de.monticore.ocl.oclexpressions._ast.ASTForallExpressionBuilder;
import de.monticore.ocl.oclexpressions._ast.ASTInDeclaration;
import de.monticore.ocl.oclexpressions._ast.ASTInDeclarationBuilder;
import de.monticore.ocl.oclexpressions._ast.ASTInDeclarationVariable;
import de.monticore.ocl.oclexpressions._ast.ASTInDeclarationVariableBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OCLASTPreProcessor {
  public static ASTOCLCompilationUnit preProcessInvariants(ASTOCLCompilationUnit ast) {
    List<ASTOCLConstraint> newConstraints = new ArrayList<>();
    for(ASTOCLConstraint constraint : ast.getOCLArtifact().getOCLConstraintList()) {
      if(constraint instanceof ASTOCLInvariant) {
        ASTOCLInvariant invariant = (ASTOCLInvariant) constraint;
        if(invariant.getOCLContextDefinitionList().size() > 1) {
          ASTOCLInvariant originalInvariant = invariant.deepClone();
          List<ASTOCLContextDefinition> contextDefinitions = invariant.getOCLContextDefinitionList();
          invariant.setOCLContextDefinitionList(contextDefinitions.subList(0, 1));
          decorateInvariantWithForAllExpressionsForContextDefinitions(invariant, contextDefinitions.subList(1, contextDefinitions.size()));
          for(int i = 1; i < contextDefinitions.size(); i++) {
            ASTOCLInvariant newInvariant = originalInvariant.deepClone();
            newInvariant.setOCLContextDefinitionList(contextDefinitions.subList(i, i + 1));
            decorateInvariantWithForAllExpressionsForContextDefinitions(newInvariant, Stream.concat(contextDefinitions.subList(0, i).stream(), contextDefinitions.subList(i + 1, contextDefinitions.size()).stream()).collect(Collectors.toList()));
            newConstraints.add(newInvariant);
          }
        }
      }
    }
    ast.getOCLArtifact().addAllOCLConstraints(newConstraints);
    return ast;
  }

  private static void decorateInvariantWithForAllExpressionsForContextDefinitions(ASTOCLInvariant invariant, List<ASTOCLContextDefinition> contextDefinitions) {
    for(int i = 0; i < contextDefinitions.size(); i++) {
      ASTOCLParamDeclaration paramDeclaration = contextDefinitions.get(i).getOCLParamDeclaration();
      ASTInDeclarationVariable inDeclarationVariable = new ASTInDeclarationVariableBuilder().setName(paramDeclaration.getName()).build();
      ASTInDeclaration inDeclaration = new ASTInDeclarationBuilder().addInDeclarationVariable(inDeclarationVariable).setMCType(paramDeclaration.getMCType()).build();
      ASTForallExpression newExpression = new ASTForallExpressionBuilder().addInDeclaration(inDeclaration).setExpression(invariant.getExpression()).build();
      invariant.setExpression(newExpression);
    }
  }
}
