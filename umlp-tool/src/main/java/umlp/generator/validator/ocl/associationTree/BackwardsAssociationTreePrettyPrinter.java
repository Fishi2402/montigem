package umlp.generator.validator.ocl.associationTree;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.ocl.ocl._ast.ASTOCLParamDeclaration;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BackwardsAssociationTreePrettyPrinter implements AssociationTreePrettyPrintingVisitor {

  private final IndentPrinter forwardPrinter;
  private final IndentPrinter globalBackwardPrinter;
  private final Map<String, IndentPrinter> backwardPrinterForAssociationChain = new LinkedHashMap<>();
  private String lastValidationResult;
  private final Map<IndentPrinter, Integer> backwardClosingBracketCountForBackwardsPrinter = new LinkedHashMap<>();
  private final Map<String, String> variableNameForAssociationChain = new LinkedHashMap<>();
  private final Map<Integer, String> previousValidationResultForTreeLevel = new LinkedHashMap<>();
  private final Map<IndentPrinter, Map<String, Integer>> visitedTypeCountForBackwardsPrinter = new LinkedHashMap<>();
  private final Set<String> visitedNonOptionalDeclarationCalls = new HashSet<>();
  private int firstAssociationTreeLevelForContextDefinition = 0;
  private List<String> finishingAssociationChains = new ArrayList<>();
  private int curListCount = 0;

  public BackwardsAssociationTreePrettyPrinter() {
    this(new IndentPrinter(), new IndentPrinter());
  }

  public BackwardsAssociationTreePrettyPrinter(IndentPrinter forwardPrinter, IndentPrinter globalBackwardPrinter) {
    this.forwardPrinter = forwardPrinter;
    this.globalBackwardPrinter = globalBackwardPrinter;
  }

  @Override
  public void visit(OCLInvariantTree node) {
    forwardPrinter.indent();
    visitedTypeCountForBackwardsPrinter.clear();

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

      globalBackwardPrinter.print("List<");
      globalBackwardPrinter.print(type);
      globalBackwardPrinter.print("> ");
      globalBackwardPrinter.print(StringTransformations.uncapitalize(declaration.getMCType().printType()));
      globalBackwardPrinter.println("s = new ArrayList<>();");
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
      finishingAssociationChains.addAll(node.getAssociationChain());
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
          boolean newPrinter = false;
          ASTCDCardinality leftCardinality = node.getAssociation().get().getLeft().getCDCardinality();
          if(!backwardPrinterForAssociationChain.containsKey(associationChain)) {
            if(backwardPrinterForAssociationChain.containsKey(associationChain.substring(associationChain.indexOf(".") + 1))) {
              boolean newPrinterNecessary = leftCardinality.isMult() || (leftCardinality.isOpt() && !leftEqualsRight);
              if(newPrinterNecessary && backwardPrinterForAssociationChain.keySet().stream().anyMatch(a -> a.endsWith(associationChain.substring(associationChain.indexOf(".") + 1)) && !a.equals(associationChain.substring(associationChain.indexOf(".") + 1)))) {
                newPrinter = true;
                backwardPrinterForAssociationChain.put(associationChain, new IndentPrinter());
                backwardClosingBracketCountForBackwardsPrinter.put(backwardPrinterForAssociationChain.get(associationChain), 0);
                visitedTypeCountForBackwardsPrinter.put(backwardPrinterForAssociationChain.get(associationChain), new LinkedHashMap<>());
              } else {
                backwardPrinterForAssociationChain.put(associationChain, backwardPrinterForAssociationChain.get(associationChain.substring(associationChain.indexOf(".") + 1)));
              }
            } else {
              newPrinter = true;
              backwardPrinterForAssociationChain.put(associationChain, new IndentPrinter());
              backwardClosingBracketCountForBackwardsPrinter.put(backwardPrinterForAssociationChain.get(associationChain), 0);
              visitedTypeCountForBackwardsPrinter.put(backwardPrinterForAssociationChain.get(associationChain), new LinkedHashMap<>());
            }
            if(leftCardinality.isMult()) {
              curListCount++;
            }
          }
          IndentPrinter backwardPrinter = backwardPrinterForAssociationChain.get(associationChain);
          Map<String, Integer> visitedTypeCount;
          if(visitedTypeCountForBackwardsPrinter.containsKey(backwardPrinter)) {
            visitedTypeCount = visitedTypeCountForBackwardsPrinter.get(backwardPrinter);
          } else {
            throw new RuntimeException("No visited type count found for backward printer. Initialize the count when creating the backward printer.");
          }
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
          if(newPrinter) {
            while (variableNameForAssociationChain.containsValue(leftVarName)) {
              visitedTypeCount.put(leftType, visitedTypeCount.get(leftType) + 1);
              leftVarName = StringTransformations.uncapitalize(leftType) + visitedTypeCount.get(leftType);
            }
          }
          variableNameForAssociationChain.put(associationChain, leftVarName);
          String rightVarName;
          if(node.isLeaf()) {
            rightVarName = StringTransformations.uncapitalize(node.getAssociation().get().getRight().getMCQualifiedType().printType().substring(0, 1));
          } else {
            rightVarName = variableNameForAssociationChain.get(associationChain.substring(associationChain.indexOf(".") + 1));
          }
          boolean visitedNonOptionalDecCall = true;
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
            if(!leftEqualsRight) {
              backwardPrinter.print(leftVarName);
            } else {
              backwardPrinter.print(rightVarName);
            }
            backwardPrinter.print(" = ");
            backwardPrinter.print(rightVarName);
            backwardPrinter.print(".get");
            backwardPrinter.print(StringTransformations.capitalize(leftName));
            backwardPrinter.println("();");
            if(leftEqualsRight) {
              if(finishingAssociationChains.contains(associationChain)) {
                backwardPrinter.print(StringTransformations.uncapitalize(leftType));
                backwardPrinter.print("s.add(");
                backwardPrinter.print(StringTransformations.uncapitalize(leftType) + visitedTypeCount.get(leftType));
                backwardPrinter.println(");");
                finishingAssociationChains.remove(associationChain);
              }
              backwardPrinter.unindent();
              backwardPrinter.println("}");
            } else {
              if(backwardClosingBracketCountForBackwardsPrinter.containsKey(backwardPrinter)) {
                backwardClosingBracketCountForBackwardsPrinter.put(backwardPrinter, backwardClosingBracketCountForBackwardsPrinter.get(backwardPrinter) + 1);
              } else {
                throw new RuntimeException("No backward closing bracket count found for backward printer. Initialize the count when creating the backward printer.");
              }
            }
          } else if (leftCardinality.isOne()) {
            String call = rightVarName + ".get" + StringTransformations.capitalize(leftName) + "();";
            visitedNonOptionalDecCall = visitedNonOptionalDeclarationCalls.add(call);
            if(visitedNonOptionalDecCall) {
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
            backwardPrinter.print(curListCount);
            backwardPrinter.print(" = ");
            backwardPrinter.print(rightVarName);
            backwardPrinter.print(".get");
            backwardPrinter.print(StringTransformations.capitalize(leftName));
            backwardPrinter.println("();");
            backwardPrinter.print("for(");
            if(backwardClosingBracketCountForBackwardsPrinter.containsKey(backwardPrinter)) {
              backwardClosingBracketCountForBackwardsPrinter.put(backwardPrinter, backwardClosingBracketCountForBackwardsPrinter.get(backwardPrinter) + 1);
            } else {
              throw new RuntimeException("No backward closing bracket count found for backward printer. Initialize the count when creating the backward printer.");
            }
            backwardPrinter.print(leftType);
            backwardPrinter.print(" ");
            backwardPrinter.print(leftVarName);
            backwardPrinter.print(" : ");
            backwardPrinter.print(leftVarName);
            backwardPrinter.print("List");
            backwardPrinter.print(curListCount);
            backwardPrinter.println(") {");
            backwardPrinter.indent();
          }

          if(!(leftCardinality.isOpt() && leftEqualsRight) && visitedNonOptionalDecCall && finishingAssociationChains.contains(associationChain)) {
            backwardPrinter.print(StringTransformations.uncapitalize(leftType));
            backwardPrinter.print("s.add(");
            if(leftEqualsRight) {
              backwardPrinter.print(rightVarName);
            } else {
              backwardPrinter.print(leftVarName);
            }
            backwardPrinter.println(");");
            finishingAssociationChains.remove(associationChain);
          }
          if(node.getTreeLevel() == firstAssociationTreeLevelForContextDefinition) {
            for (int i = backwardClosingBracketCountForBackwardsPrinter.get(backwardPrinter); i > 0; i--) {
              backwardPrinter.unindent();
              backwardPrinter.println("}");
            }
            backwardClosingBracketCountForBackwardsPrinter.put(backwardPrinter, 0);
          }
        }
      }
    }
  }

  public String getResult() {
    return globalBackwardPrinter.getContent() + "\n" + backwardPrinterForAssociationChain.values().stream().distinct().map(IndentPrinter::getContent).collect(Collectors.joining("\n")) + "\n" + forwardPrinter.getContent();
  }
}
