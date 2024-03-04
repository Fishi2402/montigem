package umlp.generator.validator.ocl.associationTree;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.ocl.ocl._ast.ASTOCLParamDeclaration;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.StringTransformations;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BackwardsAssociationTreePrettyPrinter implements AssociationTreePrettyPrintingVisitor {

  private final IndentPrinter forwardPrinter;

  private final IndentPrinter backwardPrinter;

  private String lastValidationResult;
  private int backwardClosingBracketCount = 0;

  private final Map<String, String> variableNameForAssociationChain = new LinkedHashMap<>();
  private final Map<Integer, String> previousValidationResultForTreeLevel = new LinkedHashMap<>();
  private final Map<String, Integer> visitedTypeCount = new LinkedHashMap<>();
  private final Set<String> visitedNonOptionalDeclarationCalls = new HashSet<>();
  private int firstAssociationTreeLevelForContextDefinition = 0;

  public BackwardsAssociationTreePrettyPrinter() {
    this(new IndentPrinter(), new IndentPrinter());
  }

  public BackwardsAssociationTreePrettyPrinter(IndentPrinter forwardPrinter, IndentPrinter backwardPrinter) {
    this.forwardPrinter = forwardPrinter;
    this.backwardPrinter = backwardPrinter;
  }

  @Override
  public void visit(OCLInvariantTree node) {
    forwardPrinter.indent();
    visitedTypeCount.clear();

    for(ASTOCLParamDeclaration declaration : node.getBranchRootNodes().stream().map(n -> n.getParameterDeclaration().get()).collect(Collectors.toList())) {
      String type = declaration.getMCType().printType();

      forwardPrinter.print("for(");
      forwardPrinter.print(type);
      forwardPrinter.print(" ");
      forwardPrinter.print(StringTransformations.uncapitalize(type));
      forwardPrinter.print(" : ");
      forwardPrinter.print(StringTransformations.uncapitalize(type));
      forwardPrinter.println("s) {");
      forwardPrinter.indent();
      forwardPrinter.print(type);
      forwardPrinter.print("ValidationResult ");
      forwardPrinter.print(StringTransformations.uncapitalize(type));
      forwardPrinter.print("ValidationResult = ");
      forwardPrinter.print(type);
      forwardPrinter.print("Validator.validate");
      forwardPrinter.print(node.getInvariant().getName());
      forwardPrinter.print("(");
      forwardPrinter.print(StringTransformations.uncapitalize(type));
      forwardPrinter.println(");");

      backwardPrinter.print("List<");
      backwardPrinter.print(type);
      backwardPrinter.print("> ");
      backwardPrinter.print(StringTransformations.uncapitalize(declaration.getMCType().printType()));
      backwardPrinter.println("s = new ArrayList<>();");
    }
  }

  @Override
  public void endVisit(OCLInvariantTree node) {
    forwardPrinter.unindent();
    forwardPrinter.print("}");
  }


  @Override
  public void visit(OCLInvariantTreeNode node) {
    if(node.getTreeLevel() == 0) {
      lastValidationResult = StringTransformations.uncapitalize(node.getParameterDeclaration().get().getMCType().printType()) + "ValidationResult";
      previousValidationResultForTreeLevel.put(0, lastValidationResult);
      firstAssociationTreeLevelForContextDefinition = node.getLevelOfFirstAssociation();
    }
    if(node.hasAssociation()) {
      if(lastValidationResult.isEmpty()) {
        int lastValidationresultLevel = previousValidationResultForTreeLevel.keySet().stream()
            .filter(value -> value < node.getTreeLevel())
            .max(Integer::compareTo)
            .orElseThrow();
        lastValidationResult = previousValidationResultForTreeLevel.get(lastValidationresultLevel);
      }
      ASTCDAssociation association = node.getAssociation().get();
      ASTCDCardinality rightCardinality = association.getRight().getCDCardinality();
      String addOperation = association.getLeft().getCDCardinality().isMult() ? "add" : "set";
      String rightType = association.getRight().getMCQualifiedType().printType();
      String rightName = association.getRight().getName();
      String leftType = node.getAssociation().get().getLeft().getMCQualifiedType().printType();
      boolean leftEqualsRight = leftType.equals(rightType);
      String leftAssocName = (leftEqualsRight ? "Left" : "") + StringTransformations.capitalize(association.getName());
      String rightAssocName = (leftEqualsRight ? "Right" : "") + StringTransformations.capitalize(association.getName());

      if(rightCardinality.isOpt()) {
        forwardPrinter.print("if(");
        forwardPrinter.print(lastValidationResult);
        forwardPrinter.print(".has");
        forwardPrinter.print(rightAssocName);
        forwardPrinter.println("ValidationResult()) {");
        forwardPrinter.indent();
        forwardPrinter.print(rightType);
        forwardPrinter.print("ValidationResult ");
        forwardPrinter.print(rightName);
        forwardPrinter.print("ValidationResult = ");
        forwardPrinter.print(lastValidationResult);
        forwardPrinter.print(".get");
        forwardPrinter.print(rightAssocName);
        forwardPrinter.println("ValidationResult();");

        forwardPrinter.print(rightType);
        forwardPrinter.print("ValidationResult ");
        forwardPrinter.print(rightName);
        forwardPrinter.print("ValidationResultClone = ");
        forwardPrinter.print(rightName);
        forwardPrinter.println("ValidationResult.shallowClone();");

        forwardPrinter.print(rightName);
        forwardPrinter.print("ValidationResultClone.");
        forwardPrinter.print(addOperation);
        forwardPrinter.print(leftAssocName);
        forwardPrinter.print("ValidationResult(");
        forwardPrinter.print(lastValidationResult);
        forwardPrinter.println(".shallowClone());");
        lastValidationResult = rightName + "ValidationResult";
      } else if(rightCardinality.isOne()) {
        forwardPrinter.print(rightType);
        forwardPrinter.print("ValidationResult ");
        forwardPrinter.print(rightName);
        forwardPrinter.print("ValidationResult = ");
        forwardPrinter.print(lastValidationResult);
        forwardPrinter.print(".get");
        forwardPrinter.print(rightAssocName);
        forwardPrinter.println("ValidationResult();");

        forwardPrinter.print(rightType);
        forwardPrinter.print("ValidationResult ");
        forwardPrinter.print(rightName);
        forwardPrinter.print("ValidationResultClone = ");
        forwardPrinter.print(rightName);
        forwardPrinter.println("ValidationResult.shallowClone();");

        forwardPrinter.print(rightName);
        forwardPrinter.print("ValidationResultClone.");
        forwardPrinter.print(addOperation);
        forwardPrinter.print(leftAssocName);
        forwardPrinter.print("ValidationResult(");
        forwardPrinter.print(lastValidationResult);
        forwardPrinter.println(".shallowClone());");

        lastValidationResult = rightName + "ValidationResult";
      } else if(rightCardinality.isMult()) {
        forwardPrinter.print("for(");
        forwardPrinter.print(rightType);
        forwardPrinter.print("ValidationResult ");
        forwardPrinter.print(rightName);
        forwardPrinter.print("ValidationResult : ");
        forwardPrinter.print(lastValidationResult);
        forwardPrinter.print(".get");
        forwardPrinter.print(rightAssocName);
        forwardPrinter.println("ValidationResults()) {");
        forwardPrinter.indent();

        forwardPrinter.print(rightType);
        forwardPrinter.print("ValidationResult ");
        forwardPrinter.print(rightName);
        forwardPrinter.print("ValidationResultClone = ");
        forwardPrinter.print(rightName);
        forwardPrinter.println("ValidationResult.shallowClone();");

        forwardPrinter.print(rightName);
        forwardPrinter.print("ValidationResultClone.");
        forwardPrinter.print(addOperation);
        forwardPrinter.print(leftAssocName);
        forwardPrinter.print("ValidationResult(");
        forwardPrinter.print(lastValidationResult);
        forwardPrinter.println(".shallowClone());");
        lastValidationResult = rightName + "ValidationResult";
      }
      previousValidationResultForTreeLevel.put(node.getTreeLevel(), rightName + "ValidationResult");
      if(node.getChildren().isEmpty()) {
        String domainValidationResultName = StringTransformations.uncapitalize(node.getAssociation().get().getRight().getMCQualifiedType().printType().substring(0,1)) + "ValidationResult";
        forwardPrinter.print(domainValidationResultName);
        forwardPrinter.print(" = ");
        forwardPrinter.print(domainValidationResultName);
        forwardPrinter.print(".merge(");
        forwardPrinter.print(lastValidationResult);
        forwardPrinter.println("Clone);");
        lastValidationResult = "";
      }
    }

  }

  @Override
  public void endVisit(OCLInvariantTreeNode node) {
    if (node.hasAssociation()) {
      if(!node.getAssociation().get().getRight().getCDCardinality().isOne()) {
        forwardPrinter.println("}");
        forwardPrinter.unindent();
      }
      Set<String> associationChains = node.getAssociationChain();
      for (String associationChain : associationChains) {
        if (!variableNameForAssociationChain.containsKey(associationChain)) {
          String leftType = node.getAssociation().get().getLeft().getMCQualifiedType().printType();
          String leftName = node.getAssociation().get().getLeft().getName();
          boolean leftEqualsRight = leftType.equals(node.getAssociation().get().getRight().getMCQualifiedType().printType());
          if(leftEqualsRight) {
              if (!visitedTypeCount.containsKey(leftType)) {
                  visitedTypeCount.put(leftType, 0);
                }
            } else {
              if (!visitedTypeCount.containsKey(leftType)) {
                  visitedTypeCount.put(leftType, 0);
                } else {
                  visitedTypeCount.put(leftType, visitedTypeCount.get(leftType) + 1);
                }
            }
          String leftVarName = StringTransformations.uncapitalize(leftType) + visitedTypeCount.get(leftType);
          variableNameForAssociationChain.put(associationChain, leftVarName);
          String rightVarName = "";
          if(node.isLeaf()) {
              rightVarName = StringTransformations.uncapitalize(node.getAssociation().get().getRight().getMCQualifiedType().printType().substring(0,1));
            } else {
              rightVarName = variableNameForAssociationChain.get(associationChain.substring(associationChain.indexOf(".") + 1));
            }
          ASTCDCardinality leftCardinality = node.getAssociation().get().getLeft().getCDCardinality();
          if (leftCardinality.isOpt()) {
            if(!leftEqualsRight) {
              backwardPrinter.print(leftType);
              backwardPrinter.print(" ");
              backwardPrinter.print(leftVarName);
              backwardPrinter.println(";");
            }
            backwardPrinter.print("if(");
            backwardPrinter.print(rightVarName);
            backwardPrinter.print(".get");
            backwardPrinter.print(StringTransformations.capitalize(leftName));
            backwardPrinter.println("Gem().isPresent()) {");
            backwardPrinter.indent();
            backwardPrinter.print(leftVarName);
            backwardPrinter.print(" = ");
            backwardPrinter.print(rightVarName);
            backwardPrinter.print(".get");
            backwardPrinter.print(StringTransformations.capitalize(leftName));
            backwardPrinter.println("();");
            if(leftEqualsRight) {
              backwardPrinter.unindent();
              backwardPrinter.println("}");
            } else {
              backwardClosingBracketCount++;
            }
          } else if (leftCardinality.isOne()) {
            String call = rightVarName + ".get" + StringTransformations.capitalize(leftName) + "();";
            if(visitedNonOptionalDeclarationCalls.add(call)) {
              backwardPrinter.print(leftType);
              backwardPrinter.print(" ");
              backwardPrinter.print(leftVarName);
              backwardPrinter.print(" = ");
              backwardPrinter.println(call);
            } else if(visitedTypeCount.get(leftType) > 0) {
              visitedTypeCount.put(leftType, visitedTypeCount.get(leftType) - 1);
              variableNameForAssociationChain.replace(associationChain, StringTransformations.uncapitalize(leftType) + visitedTypeCount.get(leftType));
            }
          } else {
            backwardPrinter.print("List<");
            backwardPrinter.print(leftType);
            backwardPrinter.print("> ");
            backwardPrinter.print(leftVarName);
            backwardPrinter.print("List");
            backwardPrinter.print(" = ");
            backwardPrinter.print(rightVarName);
            backwardPrinter.print(".get");
            backwardPrinter.print(StringTransformations.capitalize(leftName));
            backwardPrinter.println("();");
            backwardPrinter.print("for(");
            backwardClosingBracketCount++;
            backwardPrinter.print(leftType);
            backwardPrinter.print(" ");
            backwardPrinter.print(leftVarName);
            backwardPrinter.print(" : ");
            backwardPrinter.print(leftVarName);
            backwardPrinter.print("List");
            backwardPrinter.println(") {");
            backwardPrinter.indent();
          }
        }
      }
    }

    if(node.getTreeLevel() == firstAssociationTreeLevelForContextDefinition) {
      String leftType = node.getAssociation().get().getLeft().getMCQualifiedType().printType();
      backwardPrinter.print(StringTransformations.uncapitalize(leftType));
      backwardPrinter.print("s.add(");
      backwardPrinter.print(StringTransformations.uncapitalize(leftType) + visitedTypeCount.get(leftType));
      backwardPrinter.println(");");
      for(int i = backwardClosingBracketCount; i > 0; i--) {
        backwardPrinter.unindent();
        backwardPrinter.println("}");
      }
      backwardClosingBracketCount = 0;
    }
  }

  public String getResult() {
    return backwardPrinter.getContent() + "\n" + forwardPrinter.getContent();
  }
}
