package umlp.generator.ocl.javagen;

import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.ocl.ocl._ast.*;
import de.monticore.ocl.ocl._visitor.OCLHandler;
import de.monticore.ocl.ocl._visitor.OCLTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class OCLJavaGenVisitor implements OCLHandler {
  private OCLTraverser traverser;
  private IndentPrinter printer;
  int anonymousInvCounter = 0;

  public OCLJavaGenVisitor(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public OCLTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(OCLTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void traverse(ASTOCLCompilationUnit node) {
    if(node.isPresentPackage()){
      printer.println("package " + node.getPackage() + ";");
      printer.println();
    }

    // Skip model import statements

    // Default imports
    printer.println("import java.util.List;");
    printer.println("import java.util.Set;");
    printer.println("import java.util.HashSet;");
    printer.println("import java.util.Objects;");
    printer.println("import java.util.function.Function;");
    printer.println();

    if (null != node.getOCLArtifact()) {
      node.getOCLArtifact().accept(getTraverser());
    }
  }

  @Override
  public void handle(ASTOCLArtifact node) {
    printer.print("public class ");
    printer.print(node.getName());
    printer.println("{");
    printer.indent();

    OCLHandler.super.handle(node);

    printer.unindent();
    printer.println();
    printer.println("}");
  }

  @Override
  public void handle(ASTOCLInvariant node) {
    printer.print("public static boolean check");
    if(node.isPresentName()) {
       printer.print(node.getName());
    } else {
      printer.print("Inv" + anonymousInvCounter++);
    }

    printer.print("(");

    List<ASTOCLContextDefinition> oclContextDefinitionList = node.getOCLContextDefinitionList();
    for (int i = 0; i < oclContextDefinitionList.size(); i++) {
      ASTOCLContextDefinition t1 = oclContextDefinitionList.get(i);
      if(i != 0){
        printer.print(", ");
      }
      t1.accept(getTraverser());

      // Anonymous parameter
      if(t1.isPresentMCType()){
        printer.print(SymTypeExpression2JavaConverter.printModelTypeAsJavaType(t1.getMCType()));
        printer.print(" _this");
      }
    }

    printer.println(") {");
    printer.indent();
    printer.print("return ");

    node.getExpression().accept(getTraverser());

    printer.println(";");
    printer.unindent();
    printer.println("}");
  }

  @Override
  public void handle(ASTOCLParamDeclaration node) {
    printer.print(SymTypeExpression2JavaConverter.printModelTypeAsJavaType(node.getMCType()));
    printer.print(" ");
    printer.print(node.getName());

    if(node.isPresentExpression()){
      // TODO
      Log.warn("Expression for OCLParamDeclaration is not yet supported",
          node.getExpression().get_SourcePositionStart(), node.getExpression().get_SourcePositionEnd());
    }
  }
}
