package umlp.generator.validator.ocl.visitors;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.codegen.util.VariableNaming;
import de.monticore.ocl.codegen.visitors.OCLPrinter;
import de.monticore.ocl.ocl._ast.ASTOCLInvariant;
import de.monticore.ocl.ocl._ast.ASTOCLParamDeclaration;
import de.monticore.ocl.setexpressions._ast.ASTGeneratorDeclaration;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehension;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehensionItem;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.validator.ocl.associationTree.OCLInvariantTree;
import umlp.generator.validator.ocl.associationTree.OCLInvariantTreeNode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CustomOCLPrinter extends OCLPrinter {
  private final OCLInvariantTree oCLInvariantTree;
  public CustomOCLPrinter(IndentPrinter printer, VariableNaming naming, IDerive deriver, ISynthesize syntheziser, OCLInvariantTree oCLInvariantTree) {
      super(printer, naming, deriver, syntheziser);
      this.oCLInvariantTree = oCLInvariantTree;
  }

  @Override
  public void handle(ASTOCLInvariant node) {
    String oclMessage = node.getStereotypeList().stream().filter(s -> !s.getValue("message").isEmpty()).findFirst().map(s -> s.getValue("message")).orElse("");

    // The use with multiple context definitions requires additional implementation. This is not supported yet.
    ASTOCLParamDeclaration dec = node.getOCLContextDefinitionList().get(0).getOCLParamDeclaration();

    Map<ASTSetComprehension, String> setComprehensionContext = new LinkedHashMap<>();
    for(ASTSetComprehension setComprehension : oCLInvariantTree.findSetComprehensions()) {
      setComprehensionContext.put(setComprehension, "setComprehension" + setComprehensionContext.size());
    }
    String type = dec.getMCType().printType();
    String name = StringTransformations.uncapitalize(dec.getMCType().printType().substring(0,1));
    this.getPrinter().indent();
    this.getPrinter().print(type);
    this.getPrinter().print("ValidationResult ");
    this.getPrinter().print(name);
    this.getPrinter().print("ValidationResult = new ");
    this.getPrinter().print(type);
    this.getPrinter().println("ValidationResult();");

    this.getPrinter().print("String message = \"");
    this.getPrinter().print(oclMessage);
    this.getPrinter().println("\";");

    Map<ASTSetComprehension, OCLInvariantTreeNode> setComprehensionsInScope = new LinkedHashMap<>();
    for(ASTSetComprehension setComprehension : setComprehensionContext.keySet()) {
      ASTNode setNode = setComprehension.getEnclosingScope().getAstNode();
      if(oCLInvariantTree.getInvariant().equals(setNode) && setComprehension.getLeft().isPresentGeneratorDeclaration()) {
        ASTExpression genDecExpression = setComprehension.getLeft().getGeneratorDeclaration().getExpression();
        List<String> call = CustomOCLExpressionsPrinter.getExpressionCall(genDecExpression);
        Optional<OCLInvariantTreeNode> treeNode = oCLInvariantTree.findMatchingBranch(call, setComprehension.getEnclosingScope());
        treeNode.ifPresent(oclInvariantTreeNode -> setComprehensionsInScope.put(setComprehension, oclInvariantTreeNode));
      }
    }
    for(ASTSetComprehension setComprehension : setComprehensionsInScope.keySet()) {
      String compType = deriver.deriveType(setComprehension).getResult().printFullName();
      String compName = setComprehensionContext.get(setComprehension);
      printer.print(compType);
      printer.print(" ");
      printer.print(compName);
      printer.println(" = new HashSet<>();");
    }

    printer.print("boolean value = ");
    node.getExpression().accept(this.getTraverser());
    this.getPrinter().println(";");
    printSetComprehension(printer, oCLInvariantTree, setComprehensionContext, setComprehensionsInScope);
    Optional<OCLInvariantTreeNode> treeNode = oCLInvariantTree.findNode(dec);
    if(treeNode.isPresent()) {
      Set<String> finishingCalls = treeNode.get().getFinishingCalls(true, "value");
      for (String call : finishingCalls) {
        this.getPrinter().print(call);
      }
    }
    this.getPrinter().print("return ");
    this.getPrinter().print(name);
    this.getPrinter().println("ValidationResult;");
  }


  private static void printSetComprehension(IndentPrinter printer, OCLInvariantTree oCLInvariantTree, Map<ASTSetComprehension, String> setComprehensionContext, Map<ASTSetComprehension, OCLInvariantTreeNode> setComprehensionsInScope) {
    for(ASTSetComprehension setComprehension : setComprehensionsInScope.keySet()) {
      OCLInvariantTreeNode treeNode = setComprehensionsInScope.get(setComprehension);
      String name = setComprehension.getLeft().getGeneratorDeclaration().getName();
      String setCompName = setComprehensionContext.get(setComprehension);
      List<String> expressionCall = CustomOCLExpressionsPrinter.getExpressionCall(setComprehension.getLeft().getGeneratorDeclaration().getExpression());
      String call = CustomOCLExpressionsPrinter.transformToActualCall(expressionCall);
      OCLInvariantTreeNode deepestNodeForCall = treeNode.findDeepestNodeForCall(expressionCall).get();
      String type = deepestNodeForCall.getAssociation().get().getRight().getMCQualifiedType().printType();
      String listName = treeNode.findDeepestNodeForCall(expressionCall).get().getAssociation().get().getName();
      if (deepestNodeForCall.getAssociation().get().getLeft().getMCQualifiedType().printType().equals(deepestNodeForCall.getAssociation().get().getRight().getMCQualifiedType().printType())) {
        listName = "Right" + StringTransformations.capitalize(listName);
      }
      String vRCall = CustomOCLExpressionsPrinter.getVRCallForCallBranch(oCLInvariantTree, treeNode, 0);
      printer.print("for(");
      printer.print(type);
      printer.print(" ");
      printer.print(setComprehension.getLeft().getGeneratorDeclaration().getName());
      printer.print(" : ");
      printer.print(call);
      printer.println(") {");
      printer.indent();
      printer.print("int i = ");
      printer.print(call);
      printer.print(".indexOf(");
      printer.print(name);
      printer.println(");");
      printer.print("boolean v");
      printer.print(" = ");
      printer.print(setCompName);
      printer.print(".contains(");
      printer.print(setComprehension.getLeft().getGeneratorDeclaration().getName());
      printer.print(") ? ");
      printer.print("value");
      printer.print(" : !value");
      printer.println(";");
      vRCall = vRCall.substring(0, vRCall.lastIndexOf(".")) + ".get" + StringTransformations.capitalize(listName) + "ValidationResults()";
      printer.print("if(");
      printer.print(vRCall);
      printer.print(".size() > i");
      printer.println(") {");
      printer.indent();
      printer.print(vRCall);
      printer.print(".get(i).addThisObjectRelatedValidationMessage(v");
      printer.println(" ? ValidationMessage.ok() : ValidationMessage.error(message));");
      printer.unindent();
      printer.println("} else {");
      printer.indent();
      printer.print(vRCall);
      printer.print(".add(new ");
      printer.print(type);
      printer.println("ValidationResult());");
      printer.print(vRCall);
      printer.print(".get(i).addThisObjectRelatedValidationMessage(v");
      printer.println(" ? ValidationMessage.ok() : ValidationMessage.error(message));");
      printer.unindent();
      printer.println("}");
      printer.unindent();
      printer.println("}");
    }
  }

  private static Optional<ASTGeneratorDeclaration> findGeneratorDeclarationForSetComprehension(ASTSetComprehension setComprehension) {
    if(setComprehension.getLeft().isPresentGeneratorDeclaration()) {
      return Optional.of(setComprehension.getLeft().getGeneratorDeclaration());
    }
    for(ASTSetComprehensionItem item : setComprehension.getSetComprehensionItemList()) {
      if(item.isPresentGeneratorDeclaration()) {
        return Optional.of(item.getGeneratorDeclaration());
      }
    }
    return Optional.empty();
  }
}
