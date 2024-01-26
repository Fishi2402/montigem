package umlp.generator.validator.ocl.visitors;

import de.monticore.ocl.codegen.util.VariableNaming;
import de.monticore.ocl.codegen.visitors.SetExpressionsPrinter;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehension;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehensionItem;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import de.se_rwth.commons.StringTransformations;

public class CustomSetExpressionsPrinter extends SetExpressionsPrinter {
  public CustomSetExpressionsPrinter(IndentPrinter printer, VariableNaming naming, IDerive deriver, ISynthesize syntheziser) {
    super(printer, naming, deriver, syntheziser);
  }


  @Override
  public void handle(ASTSetComprehension node) {
    printExpressionBeginLambda(getDeriver().deriveType(node));
    printDerivedType(node);
    getPrinter().print(" ");
    getPrinter().print(getNaming().getName(node));
    getPrinter().print(" = ");
    if (node.isSet()) {
      getPrinter().println("new java.util.HashSet<>();");
    } else {
      getPrinter().println("new java.util.LinkedList<>();");
    }

    if (node.getLeft().isPresentGeneratorDeclaration()) {
      node.getLeft().accept(getTraverser());
    }
    for (ASTSetComprehensionItem item : node.getSetComprehensionItemList()) {
      item.accept(getTraverser());
    }

    getPrinter().print(getNaming().getName(node));
    getPrinter().print(".add(");
    if (node.getLeft().isPresentGeneratorDeclaration()) {
      getPrinter().print(node.getLeft().getGeneratorDeclaration().getName());
    } else if (node.getLeft().isPresentExpression()) {
      node.getLeft().getExpression().accept(getTraverser());
    } else {
      node.getLeft().accept(getTraverser());
    }
    getPrinter().println(");");

    for (ASTSetComprehensionItem item : node.getSetComprehensionItemList()) {
      if (!item.isPresentSetVariableDeclaration()) {
        getPrinter().println("}");
        getPrinter().unindent();
      }
    }
    if (node.getLeft().isPresentGeneratorDeclaration()) {
      getPrinter().println("}");
      getPrinter().unindent();
    }

    getPrinter().print(StringTransformations.uncapitalize(getNaming().getName(node).substring(1)));
    getPrinter().print(".addAll(");
    getPrinter().print(getNaming().getName(node));
    getPrinter().println(");");

    getPrinter().print("return ");
    getPrinter().print(getNaming().getName(node));
    getPrinter().println(";");

    printExpressionEndLambda();
  }
}
