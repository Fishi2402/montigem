package umlp.generator.validator.ocl.visitors;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.ocl.codegen.util.VariableNaming;
import de.monticore.ocl.codegen.visitors.EndingBracketPrinter;
import de.monticore.ocl.codegen.visitors.OCLExpressionsPrinter;
import de.monticore.ocl.ocl.OCLMill;
import de.monticore.ocl.oclexpressions._ast.ASTForallExpression;
import de.monticore.ocl.oclexpressions._ast.ASTInDeclaration;
import de.monticore.ocl.oclexpressions._ast.ASTInDeclarationVariable;
import de.monticore.ocl.oclexpressions._ast.ASTLetinExpression;
import de.monticore.ocl.oclexpressions._ast.ASTOCLVariableDeclaration;
import de.monticore.ocl.oclexpressions._visitor.OCLExpressionsTraverser;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehension;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.generator.validator.ocl.associationTree.OCLInvariantTree;
import umlp.generator.validator.ocl.associationTree.OCLInvariantTreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomOCLExpressionsPrinter extends OCLExpressionsPrinter {
  private int currentForallLevel = 0;
  private final OCLInvariantTree oCLInvariantTree;
  Map<String, Integer> firstVisitedInCallLevel = new HashMap<>();

  public CustomOCLExpressionsPrinter(IndentPrinter printer, VariableNaming naming, IDerive deriver, ISynthesize syntheziser, OCLInvariantTree oCLInvariantTree) {
    super(printer, naming, deriver, syntheziser);
    this.oCLInvariantTree = oCLInvariantTree;
  }

  @Override
  public void handle(ASTForallExpression node) {
    printExpressionBeginLambda(getDeriver().deriveType(node));
    this.getPrinter().print("Boolean ");
    this.getPrinter().print(getNaming().getName(node));
    this.getPrinter().println(" = true;");

    List<String> validationResultCalls = new ArrayList<>();
    List<String> finishingCalls = new ArrayList<>();
    List<String> addToValidationResultListCalls = new ArrayList<>();

    for(ASTInDeclaration dec : node.getInDeclarationList()) {
      List<String> inCall = getExpressionCall(dec.getExpression());
      Optional<OCLInvariantTreeNode> treeNode = oCLInvariantTree.findMatchingBranch(inCall, node.getEnclosingScope());
      if(inCall.isEmpty()) {
        // TODO: handle this case
      } else if(inCall.size() == 1) {
        treeNode = traceBackCallFromParamDeclarationExpressionName(oCLInvariantTree, inCall, node.getEnclosingScope());
      }
      if (treeNode.isPresent()) {
        String call = String.join(".", inCall);
        if (!firstVisitedInCallLevel.containsKey(call)) {
          firstVisitedInCallLevel.put(call, currentForallLevel);
        }
        boolean newLevel = firstVisitedInCallLevel.computeIfAbsent(call, k -> -1) == currentForallLevel;
        String decType = dec.getMCType().printType();
        for (ASTInDeclarationVariable var : dec.getInDeclarationVariableList()) {
          if (newLevel) {
            this.getPrinter().println(getValidationResultListDeclaration(decType, getVRCallForCallBranch(oCLInvariantTree, treeNode.get(), currentForallLevel)));
            validationResultCalls.add(getValidationResultCall(decType, var.getName()));
            Optional<OCLInvariantTreeNode> varTreeNode = oCLInvariantTree.findNode(var.getName(), var.getEnclosingScope());
            if (varTreeNode.isPresent()) {
              finishingCalls.addAll(varTreeNode.get().getFinishingCalls(true, "value" + currentForallLevel));
              addToValidationResultListCalls.add(getAddToValidationResultListCall(var.getName()));
            }
          }
        }
      }
      dec.accept(getTraverser());
    }
    for(String call : validationResultCalls) {
      printer.println(call);
    }
    int valueLevel = currentForallLevel;
    this.getPrinter().print("boolean value");
    this.getPrinter().print(valueLevel);
    this.getPrinter().print(" = ");
    currentForallLevel++;
    node.getExpression().accept(getTraverser());
    this.getPrinter().println(";");
    for (String call : finishingCalls) {
      printer.println(call);
    }

    this.getPrinter().print(getNaming().getName(node));
    this.getPrinter().print(" &= ");
    this.getPrinter().print("value");
    this.getPrinter().print(valueLevel);
    this.getPrinter().println(";");
    for (String call : addToValidationResultListCalls) {
      printer.println(call);
    }
    OCLExpressionsTraverser endTraverser = OCLMill.traverser();
    endTraverser.setOCLExpressionsHandler(new EndingBracketPrinter(this.getPrinter()));
    node.getInDeclarationList().forEach(dec -> dec.accept(endTraverser));
    this.getPrinter().print("return ");
    this.getPrinter().print(this.getNaming().getName(node));
    this.getPrinter().println(";");
    printExpressionEndLambda();
  }

  @Override
  public void handle(ASTInDeclaration node) {
    SymTypeExpression innerType = null;
    if (node.isPresentMCType()) {
      TypeCheckResult type = this.getSynthesizer().synthesizeType(node.getMCType());
      if (type.isPresentResult()) {
        innerType = type.getResult();
      }
    } else if (node.isPresentExpression()) {
      TypeCheckResult type = this.getDeriver().deriveType(node.getExpression());
      if (type.isPresentResult()
          && type.getResult().isGenericType()
          && ((SymTypeOfGenerics) type.getResult()).sizeArguments() == 1) {
        innerType = ((SymTypeOfGenerics) type.getResult()).getArgument(0);
      }
    }
    if (innerType == null) {
      Log.error(INNER_TYPE_NOT_DERIVED_ERROR, node.get_SourcePositionStart());
    }
    if (node.isPresentExpression()) {
      for (ASTInDeclarationVariable var : node.getInDeclarationVariableList()) {
        TypeSymbol type = innerType.getTypeInfo();
        String name = var.getName();
        this.getPrinter().print("for(");
        this.getPrinter().print(type.getFullName());
        this.getPrinter().print(" ");
        this.getPrinter().print(name);
        this.getPrinter().print(" : ");
        node.getExpression().accept(getTraverser());
        this.getPrinter().println(") {");
        this.getPrinter().indent();
      }
    } else {
      Log.error(
          UNEXPECTED_STATE_AST_NODE, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }

  @Override
  public void handle(ASTLetinExpression node) {
    printExpressionBeginLambda(getDeriver().deriveType(node));
    for(ASTOCLVariableDeclaration var : node.getOCLVariableDeclarationList()) {
      Optional<OCLInvariantTreeNode> treeNode = oCLInvariantTree.findNode(var.getName(), var.getEnclosingScope());
      if (treeNode.isPresent() && treeNode.get().getParameterDeclaration().isPresent()) {
        String type = treeNode.get().getParameterDeclaration().get().getMCType().printType();
        if(treeNode.get().getParameterDeclaration().get().isPresentExpression()) {
          Optional<OCLInvariantTreeNode> expressionNode = oCLInvariantTree.findMatchingBranch(getExpressionCall(treeNode.get().getParameterDeclaration().get().getExpression()), var.getEnclosingScope());
          if(expressionNode.isPresent()) {
            OCLInvariantTreeNode deepestNode = expressionNode.get().findDeepestNodeForCallBranch();
            if(deepestNode.hasAssociation() && deepestNode.getAssociation().get().getRight().getCDCardinality().isMult()) {
              //type = "List<" + type + ">";
            }
          }
        }
        this.getPrinter().print(type);
        this.getPrinter().print(" ");
        this.getPrinter().print(var.getName());
        this.getPrinter().print(" = ");
        var.getExpression().accept(this.getTraverser());
        this.getPrinter().println(";");
      } else {
        super.handle(var);
      }
    }
    this.getPrinter().print("return ");
    node.getExpression().accept(getTraverser());
    this.getPrinter().println(";");
    printExpressionEndLambda();
  }

  @Override
  public void handle(ASTOCLVariableDeclaration node) {
  }

  public static List<String> getExpressionCall(ASTExpression expression) {
    if(expression instanceof ASTFieldAccessExpression) {
      return getInListNameForVariableInASTFieldAccessExpressionTree((ASTFieldAccessExpression) expression, new ArrayList<>());
    } else if(expression instanceof ASTNameExpression) {
      return Collections.singletonList(((ASTNameExpression) expression).getName());
    }

    return new ArrayList<>();
  }

  private static List<String> getInListNameForVariableInASTFieldAccessExpressionTree(ASTFieldAccessExpression expression, List<String> result) {
    result.add(0, expression.getName());
    if (expression.getExpression() instanceof ASTFieldAccessExpression) {
      return getInListNameForVariableInASTFieldAccessExpressionTree((ASTFieldAccessExpression) expression.getExpression(), result);
    } else if (expression.getExpression() instanceof ASTNameExpression) {
      result.add(0, ((ASTNameExpression) expression.getExpression()).getName());
      return result;
    }
    return result;
  }

  private String getValidationResultListDeclaration(String type, String call) {
    return "List<" + type + "ValidationResult> validationResultList" + currentForallLevel + " = " + call;
  }

  private String getValidationResultCall(String type, String name) {
    return type + "ValidationResult " + name + "ValidationResult = new " + type + "ValidationResult();";
  }

  private String getAddToValidationResultListCall(String name) {
    return "validationResultList"+ currentForallLevel + ".add(" + name + "ValidationResult);";
  }

  private static Optional<OCLInvariantTreeNode> traceBackCallFromParamDeclarationExpressionName(OCLInvariantTree oCLInvariantTree, List<String> expressionName, IBasicSymbolsScope enclosingScope) {
    Optional<OCLInvariantTreeNode> treeNode = oCLInvariantTree.findMatchingBranch(expressionName, enclosingScope);
    if(treeNode.isPresent() && treeNode.get().hasParameterDeclaration()) {
      if(treeNode.get().getParameterDeclaration().get().isPresentExpression()) {
        ASTExpression expression = treeNode.get().getParameterDeclaration().get().getExpression();
        List<String> expressionCall = getExpressionCall(expression);
        if(expressionCall.size() == 1) {
          return traceBackCallFromParamDeclarationExpressionName(oCLInvariantTree, expressionCall, findBasicSymbolsScope(expression.getEnclosingScope()));
        } else {
          return treeNode;
        }
      } else {
        return treeNode;
      }
    }
    return Optional.empty();
  }

  public static String getVRCallForCallBranch(OCLInvariantTree oCLInvariantTree, OCLInvariantTreeNode node, int currentForallLevel) {
    StringBuilder call = new StringBuilder();
    node.getParameterDeclaration().ifPresent(d -> {
      if(d.isPresentExpression()) {
        List<String> expressionCall = getExpressionCall(d.getExpression());
        OCLInvariantTreeNode branch = oCLInvariantTree.findMatchingBranch(expressionCall, findBasicSymbolsScope(d.getExpression().getEnclosingScope())).get();
        if(expressionCall.size() == 1) {
          branch = traceBackCallFromParamDeclarationExpressionName(oCLInvariantTree, expressionCall, findBasicSymbolsScope(d.getExpression().getEnclosingScope())).get();
        }
        call.append(getVRCallForCallBranch(oCLInvariantTree, branch, currentForallLevel));
      } else {
        call.append(d.getName());
        if (d.getMCType().printType().contains("List")) {
          call.append("ValidationResultList").append(currentForallLevel);
        } else {
          call.append("ValidationResult");
        }
      }
    });
    if(node.getChildren().isEmpty()) {
      return call.toString();
    } else if(node.getChildren().size() > 1) {
      throw new RuntimeException("In a branch representing a call each node must must only have at most one child!");
    } else if(node.getChildren().get(0).hasParameterDeclaration()) {
      return call.toString();
    }
    while(node.getChildren().size() > 0) {
      if(node.getChildren().size() > 1) {
        throw new RuntimeException("In a branch representing a call each node must must only have at most one child!");
      }
      node = node.getChildren().get(0);
      if(node.hasParameterDeclaration()) {
        return call.toString();
      }
      if(node.hasAssociation()) {
        call.append(".get");
        ASTCDAssociation assoc = node.getAssociation().get();
        String actualAssocName = StringTransformations.capitalize(node.getAssociation().get().getName());
        String assocName = assoc.getLeft().getMCQualifiedType().printType().equals(assoc.getRight().getMCQualifiedType().printType()) ? "Right" + actualAssocName : actualAssocName;
        if(node.getAssociation().get().getRight().getCDCardinality().isMult()) {
          call.append(assocName).append("ValidationResults();");
          return call.toString();
        } else {
          call.append(assocName).append("ValidationResult()");
          if(!node.getChildren().isEmpty()) {
            return call + ";";
          }
        }
      }
    }
    return call.toString();
  }

  public static IBasicSymbolsScope findBasicSymbolsScope(IExpressionsBasisScope expressionsBasisScope) {
    while(!(expressionsBasisScope instanceof IBasicSymbolsScope)) {
      expressionsBasisScope = expressionsBasisScope.getEnclosingScope();
    }
    return (IBasicSymbolsScope) expressionsBasisScope;
  }
}
