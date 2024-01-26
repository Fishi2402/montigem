package umlp.generator.validator.ocl.visitors;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.ocl.ocl._ast.ASTOCLContextDefinition;
import de.monticore.ocl.ocl._ast.ASTOCLInvariant;
import de.monticore.ocl.ocl._ast.ASTOCLParamDeclaration;
import de.monticore.ocl.ocl._ast.ASTOCLParamDeclarationBuilder;
import de.monticore.ocl.ocl._visitor.OCLVisitor2;
import de.monticore.ocl.oclexpressions._ast.ASTInDeclaration;
import de.monticore.ocl.oclexpressions._ast.ASTInDeclarationVariable;
import de.monticore.ocl.oclexpressions._ast.ASTLetinExpression;
import de.monticore.ocl.oclexpressions._ast.ASTOCLVariableDeclaration;
import de.monticore.ocl.oclexpressions._visitor.OCLExpressionsVisitor2;
import de.monticore.ocl.setexpressions._ast.ASTGeneratorDeclaration;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehension;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsVisitor2;
import de.monticore.ocl.types.check.types3wrapper.TypeCheck3AsOCLDeriver;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedNameBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedTypeBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import umlp.generator.validator.ocl.associationTree.OCLInvariantTree;
import umlp.generator.validator.ocl.associationTree.OCLInvariantTreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OCLInvariantExpressionsVisitor implements OCLVisitor2, CommonExpressionsVisitor2, OCLExpressionsVisitor2, SetExpressionsVisitor2, ExpressionsBasisVisitor2 {
  private final TypeCheck3AsOCLDeriver typeDeriver = new TypeCheck3AsOCLDeriver();
  private ASTOCLInvariant currentInvariant;
  private OCLInvariantTree currentOCLInvariantTree;
  private final ASTCDCompilationUnit origCD;
  private final List<OCLInvariantTree> oCLInvariantTrees;
  private boolean isFirstFieldAccessExpression = true;

  public OCLInvariantExpressionsVisitor(ASTCDCompilationUnit origCD) {
    this.origCD = origCD;
    oCLInvariantTrees = new ArrayList<>();
  }

  @Override
  public void visit(ASTOCLInvariant invariant) {
    this.currentInvariant = invariant;
    currentOCLInvariantTree = new OCLInvariantTree(invariant);
    for(ASTOCLParamDeclaration paramDeclaration : invariant.getOCLContextDefinitionList().stream().map(ASTOCLContextDefinition::getOCLParamDeclaration).collect(Collectors.toList())) {
      currentOCLInvariantTree.addNode(new OCLInvariantTreeNode(paramDeclaration));
    }
    isFirstFieldAccessExpression = true;
  }

  @Override
  public void endVisit(ASTOCLInvariant invariant) {
    this.oCLInvariantTrees.add(currentOCLInvariantTree);
  }

  @Override
  public void visit(ASTFieldAccessExpression node) {
    if(isFirstFieldAccessExpression) {
      isFirstFieldAccessExpression = false;
      backTraceFieldAccessExpressionForAssociationTree(node, findBasicSymbolsScope(node.getEnclosingScope())).ifPresent(currentOCLInvariantTree::addNode);
    }
  }

  @Override
  public void visit(ASTNameExpression node) {
    if(isFirstFieldAccessExpression) {
      Optional<OCLInvariantTreeNode> nodeInTree = currentOCLInvariantTree.findNode(node.getName(), findBasicSymbolsScope(node.getEnclosingScope()));
      if(nodeInTree.isPresent()) {
        nodeInTree.get().addChild(new OCLInvariantTreeNode("thisObjectRelated"));
      }
    } else {
      isFirstFieldAccessExpression = true;
    }
  }

  @Override
  public void visit(ASTCallExpression node) {
    isFirstFieldAccessExpression = false;
    if(node.getExpression() instanceof ASTFieldAccessExpression) {
      ASTFieldAccessExpression expression = (ASTFieldAccessExpression) node.getExpression();
      if(expression.getExpression() instanceof ASTFieldAccessExpression) {
        backTraceFieldAccessExpressionForAssociationTree((ASTFieldAccessExpression) expression.getExpression(), findBasicSymbolsScope(node.getEnclosingScope())).ifPresent(currentOCLInvariantTree::addNode);
      }
    }
  }

  @Override
  public void visit(ASTLetinExpression node) {
    isFirstFieldAccessExpression = false;
    for(ASTOCLVariableDeclaration dec : node.getOCLVariableDeclarationList()) {
      calculateAssociationTreeForOCLVariableDeclaration(dec);
    }
  }

  @Override
  public void visit(ASTInDeclaration node) {
    isFirstFieldAccessExpression = false;
    calculateOCLInvariantTreeNodesForInDeclarations(node);
  }

  @Override
  public void endVisit(ASTInDeclaration node) {
    isFirstFieldAccessExpression = true;
  }

  @Override
  public void visit(ASTSetComprehension node) {

  }

  @Override
  public void visit(ASTGeneratorDeclaration node) {
    isFirstFieldAccessExpression = false;
    if(node.getExpression() instanceof ASTFieldAccessExpression) {
      ASTFieldAccessExpression expression = (ASTFieldAccessExpression) node.getExpression();
      backTraceFieldAccessExpressionForAssociationTree(expression, true, node.getEnclosingScope()).ifPresent(currentOCLInvariantTree::addNode);
    } else {
      Optional<OCLInvariantTreeNode> nodeInTree = currentOCLInvariantTree.findNode(node.getName(), findBasicSymbolsScope(node.getEnclosingScope()));
      if(!nodeInTree.isPresent()) {
        OCLInvariantTreeNode newNode = new OCLInvariantTreeNode(node.getName());
        currentOCLInvariantTree.addNode(newNode);
      }
    }
  }

  public Optional<OCLInvariantTreeNode> backTraceFieldAccessExpressionForAssociationTree(ASTFieldAccessExpression expression, IBasicSymbolsScope enclosingScope) {
    return backTraceFieldAccessExpressionForAssociationTree(expression, false, enclosingScope);
  }

  public Optional<OCLInvariantTreeNode> backTraceFieldAccessExpressionForAssociationTree(ASTFieldAccessExpression expression, boolean noAttribute, IBasicSymbolsScope enclosingScope) {
    return backTraceFieldAccessExpressionForAssociationTree(getCallFromExpression(expression), noAttribute, enclosingScope);
  }

  public Optional<OCLInvariantTreeNode> backTraceFieldAccessExpressionForAssociationTree(List<String> associationTreeNames, boolean noAttribute, IBasicSymbolsScope enclosingScope) {
    Optional<ASTOCLParamDeclaration> domainObjectDeclaration = currentInvariant.getOCLContextDefinitionList().stream()
            .map(ASTOCLContextDefinition::getOCLParamDeclaration)
            .filter(p -> p.getName().equals(associationTreeNames.get(0)))
            .findFirst();
    OCLInvariantTreeNode parentNode;
    if(domainObjectDeclaration.isEmpty()) {
      Optional<OCLInvariantTreeNode> parentNodeInTree = currentOCLInvariantTree.findNode(associationTreeNames.get(0), enclosingScope);
      if(parentNodeInTree.isPresent()) {
        domainObjectDeclaration = parentNodeInTree.get().getParameterDeclaration();
        parentNode = new OCLInvariantTreeNode(domainObjectDeclaration.get());
        parentNode.setTreeLevel(parentNodeInTree.get().getTreeLevel());
      } else {
        return Optional.empty();
      }
    } else {
      parentNode = new OCLInvariantTreeNode(domainObjectDeclaration.get());
    }

    associationTreeNames.remove(0);

    List<ASTCDAssociation> associations = new ArrayList<>();
    Optional<String> attributeName = Optional.empty();
    for(int i = 0; i < associationTreeNames.size(); i++) {
      String rightName = associationTreeNames.get(i);
      Optional<ASTCDAssociation> association;
      Optional<ASTCDType> type;
      if (i == 0) {
        Optional<ASTOCLParamDeclaration> finalDomainObjectDeclaration = domainObjectDeclaration;
        type = origCD.getCDDefinition().getCDClassesList().stream().filter(c -> c.getName().equals(finalDomainObjectDeclaration.get().getMCType().printType())).map(a -> (ASTCDType) a).findFirst();
      } else {
        ASTCDAssociation lastAssociation = associations.get(associations.size() - 1);
        type = origCD.getCDDefinition().getCDClassesList().stream().filter(c -> c.getName().equals(lastAssociation.getRightQualifiedName().getQName())).map(a -> (ASTCDType) a).findFirst();
      }
      if (type.isEmpty()) {
        break;
      }
      association = origCD.getCDDefinition().getCDAssociationsListForType(type.get()).stream()
          .filter(a -> a.getRight().getName().equals(rightName)).findFirst();
      if (association.isPresent()) {
        associations.add(association.get());
        if(!association.get().getRight().getCDCardinality().isMult() && i == associationTreeNames.size() - 1 && !noAttribute) {
          attributeName = Optional.of("thisObjectRelated");
        }
      } else {
        attributeName = Optional.of(associationTreeNames.get(i));
      }
    }

    OCLInvariantTreeNode previousNode = parentNode;
    if (!associations.isEmpty()) {
      for (ASTCDAssociation association : associations) {
        OCLInvariantTreeNode currentNode = new OCLInvariantTreeNode(association);
        previousNode.addChild(currentNode);
        previousNode = currentNode;
      }
    }
    if (attributeName.isPresent()) {
      previousNode.addChild(new OCLInvariantTreeNode(attributeName.get()));
    }

    return Optional.of(parentNode);
  }

  private List<String> getCallFromExpression(ASTFieldAccessExpression expression) {
    List<String> call = getCallFromExpression(expression, new ArrayList<>());
    Collections.reverse(call);
    return call;
  }

  private List<String> getCallFromExpression(ASTFieldAccessExpression expression, List<String> result) {
    result.add(expression.getName());
    if (expression.getExpression() instanceof ASTFieldAccessExpression) {
      return getCallFromExpression((ASTFieldAccessExpression) expression.getExpression(), result);
    } else if (expression.getExpression() instanceof ASTNameExpression) {
      result.add(((ASTNameExpression) expression.getExpression()).getName());
      return result;
    }
    return new ArrayList<>();
  }

  private void calculateOCLInvariantTreeNodesForInDeclarations(ASTInDeclaration inDeclaration) {
    List<ASTOCLParamDeclaration> variableDeclarations = new ArrayList<>();
    for (ASTInDeclarationVariable v : inDeclaration.getInDeclarationVariableList()) {
      if(currentOCLInvariantTree.findNode(v.getName(), v.getEnclosingScope()).isPresent()) {
        throw new RuntimeException("Variable " + v.getName() + " declared multiple times in the OCL rule " + currentOCLInvariantTree.getInvariant().getName() + ".");
      }
      ASTMCType type = getTypeForInDeclaration(inDeclaration);
      ASTOCLParamDeclaration declaration = new ASTOCLParamDeclarationBuilder()
            .setName(v.getName())
            .setMCType(type)
            .build();
      declaration.setEnclosingScope(v.getEnclosingScope());
      variableDeclarations.add(declaration);
    }
    if (!inDeclaration.isPresentExpression()) {
      for(ASTOCLParamDeclaration variableDeclaration : variableDeclarations) {
        currentOCLInvariantTree.addNode(new OCLInvariantTreeNode(variableDeclaration));
      }
    } else {
      addTreeNodeForExpressionAndDeclaration(inDeclaration.getExpression(), variableDeclarations, false);
    }
  }

  private void calculateAssociationTreeForOCLVariableDeclaration(ASTOCLVariableDeclaration node) {
    ASTOCLParamDeclaration declaration = getParamDeclarationForVariableDeclaration(node);
    if(currentOCLInvariantTree.findNode(declaration.getName(), declaration.getEnclosingScope()).isPresent()) {
      throw new RuntimeException("Variable " + declaration.getName() + " declared multiple times in the OCL rule " + currentOCLInvariantTree.getInvariant().getName() + ".");
    }
    if (node.isPresentExpression()) {
      addTreeNodeForExpressionAndDeclaration(node.getExpression(), Collections.singletonList(declaration), true);
    } else {
      currentOCLInvariantTree.addNode(new OCLInvariantTreeNode(declaration));
    }
  }

  private void addTreeNodeForExpressionAndDeclaration(ASTExpression expression, List<ASTOCLParamDeclaration> declarations, boolean fromVariableDeclaration) {
    if (expression instanceof ASTFieldAccessExpression) {
      List<String> call = getCallFromExpression((ASTFieldAccessExpression) expression);
      Optional<OCLInvariantTreeNode> node = backTraceFieldAccessExpressionForAssociationTree((ASTFieldAccessExpression) expression, true, findBasicSymbolsScope(expression.getEnclosingScope()));
      if(node.isPresent()) {
        node.get().findDeepestNodeForCall(call).ifPresent(n -> {
            if(fromVariableDeclaration) {
              declarations.forEach(dec -> {
                dec.setExpression(expression);
                n.addChild(new OCLInvariantTreeNode(dec));
              });
            } else {
              declarations.forEach(dec -> n.addChild(new OCLInvariantTreeNode(dec)));
            }
        });
        currentOCLInvariantTree.addNode(node.get());
      }
    } else if (expression instanceof ASTNameExpression) {
      String name = ((ASTNameExpression) expression).getName();
      Optional<OCLInvariantTreeNode> node = currentOCLInvariantTree.findNode(name, findBasicSymbolsScope(expression.getEnclosingScope()));
      if(node.isPresent()) {
        declarations.forEach(dec -> node.get().addChild(new OCLInvariantTreeNode(dec)));
        currentOCLInvariantTree.addNode(node.get());
      }
    }
  }

  private ASTMCType getTypeForInDeclaration(ASTInDeclaration inDeclaration) {
    ASTMCType type;
    if (inDeclaration.isPresentMCType()) {
      type = inDeclaration.getMCType();
    } else {
      type = getTypeForExpression(inDeclaration.getExpression());
    }
    return type;
  }

  private ASTOCLParamDeclaration getParamDeclarationForVariableDeclaration(ASTOCLVariableDeclaration variableDeclaration) {
    ASTMCType type;
    if (variableDeclaration.isPresentMCType()) {
      type = variableDeclaration.getMCType();
    } else {
      type = getTypeForExpression(variableDeclaration.getExpression());
    }
    ASTOCLParamDeclaration declaration = new ASTOCLParamDeclarationBuilder()
        .setName(variableDeclaration.getName())
        .setMCType(type)
        .build();
    declaration.setEnclosingScope(variableDeclaration.getEnclosingScope());
    return declaration;
  }

  private ASTMCType getTypeForExpression(ASTExpression expression) {
    String type = typeDeriver.deriveType(expression).getResult().printFullName();
    return new ASTMCQualifiedTypeBuilder()
      .setMCQualifiedName(new ASTMCQualifiedNameBuilder()
          .setPartsList(Collections.singletonList(type))
          .build())
      .build();
  }

  public List<OCLInvariantTree> getAssociationTrees() {
    return this.oCLInvariantTrees;
  }

  public IBasicSymbolsScope findBasicSymbolsScope(IExpressionsBasisScope expressionsBasisScope) {
    while(!(expressionsBasisScope instanceof IBasicSymbolsScope)) {
      expressionsBasisScope = expressionsBasisScope.getEnclosingScope();
    }
    return (IBasicSymbolsScope) expressionsBasisScope;
  }
}
