package umlp.generator.validator.ocl;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.codegen.OCL2JavaGenerator;
import de.monticore.ocl.codegen.util.VariableNaming;
import de.monticore.ocl.ocl._ast.ASTOCLInvariant;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehension;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.validator.ocl.associationTree.OCLInvariantTree;
import umlp.generator.validator.ocl.associationTree.OCLInvariantTreeNode;
import umlp.generator.validator.ocl.typecheck.CustomTypeCheck3AsOCLDeriver;
import umlp.generator.validator.ocl.typecheck.CustomTypeCheck3AsOCLSynthesizer;
import umlp.generator.validator.ocl.visitors.CustomCommonExpressionsPrinter;
import umlp.generator.validator.ocl.visitors.CustomOCLExpressionsPrinter;
import umlp.generator.validator.ocl.visitors.CustomSetExpressionsPrinter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OCLParser extends OCL2JavaGenerator {

  public OCLParser(IndentPrinter printer, Map<String, List<String>> enumMap, OCLInvariantTree oCLInvariantTree, Map<ASTSetComprehension, String> setComprehensionContext) {
    this(printer, new VariableNaming(), enumMap, oCLInvariantTree, setComprehensionContext);
  }

  protected OCLParser(IndentPrinter printer, VariableNaming naming, Map<String, List<String>> enumMap, OCLInvariantTree oCLInvariantTree, Map<ASTSetComprehension, String> setComprehensionContext) {
    this(printer, naming, new CustomTypeCheck3AsOCLDeriver(enumMap), new CustomTypeCheck3AsOCLSynthesizer(enumMap), enumMap, oCLInvariantTree, setComprehensionContext);
  }

  protected OCLParser(IndentPrinter printer, VariableNaming naming, IDerive deriver, ISynthesize syntheziser, Map<String, List<String>> enumMap, OCLInvariantTree oCLInvariantTree, Map<ASTSetComprehension, String> setComprehensionContext) {
    super(printer, naming, deriver, syntheziser);
    this.traverser.setCommonExpressionsHandler(new CustomCommonExpressionsPrinter(printer, naming, deriver, syntheziser, enumMap));
    CustomOCLExpressionsPrinter customOCLExpressionsPrinter = new CustomOCLExpressionsPrinter(printer, naming, deriver, syntheziser, oCLInvariantTree, setComprehensionContext);
    this.traverser.setOCLExpressionsHandler(customOCLExpressionsPrinter);
    this.traverser.setSetExpressionsHandler(new CustomSetExpressionsPrinter(printer, naming, deriver, syntheziser));
  }

  public static String generate(ASTOCLInvariant invariant, IndentPrinter printer, Map<String, List<String>> enumMap, OCLInvariantTree oCLInvariantTree, Map<ASTSetComprehension, String> setComprehensionContext) {
    Preconditions.checkNotNull(invariant);
    Preconditions.checkNotNull(printer);

    invariant.accept(new OCLParser(printer, enumMap, oCLInvariantTree, setComprehensionContext).getTraverser());
    return printer.getContent();
  }

  public static String generate(ASTExpression expression, IndentPrinter printer, Map<String, List<String>> enumMap, OCLInvariantTree oCLInvariantTree, Map<ASTSetComprehension, String> setComprehensionContext) {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(printer);
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
      String type = new CustomTypeCheck3AsOCLDeriver(enumMap).deriveType(setComprehension).getResult().printFullName();
      String name = setComprehensionContext.get(setComprehension);
      printer.print(type);
      printer.print(" ");
      printer.print(name);
      printer.println(" = new HashSet<>();");
    }
    printer.print("boolean value = ");
    expression.accept(new OCLParser(printer, enumMap, oCLInvariantTree, setComprehensionContext).getTraverser());
    printer.println(";");
    printSetComprehension(printer, oCLInvariantTree, setComprehensionContext, setComprehensionsInScope);

    return printer.getContent();
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
}
