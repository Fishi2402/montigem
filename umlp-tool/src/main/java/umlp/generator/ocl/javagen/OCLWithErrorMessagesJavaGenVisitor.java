package umlp.generator.ocl.javagen;

import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.ocl.ocl._ast.ASTOCLContextDefinition;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import oclwitherrormessages._ast.ASTOCLInvariant;
import oclwitherrormessages._visitor.OCLWithErrorMessagesHandler;
import oclwitherrormessages._visitor.OCLWithErrorMessagesTraverser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OCLWithErrorMessagesJavaGenVisitor implements OCLWithErrorMessagesHandler {
  protected OCLWithErrorMessagesTraverser traverser;
  protected IndentPrinter printer;
  protected int anonymousInvCounter = 0;
  protected Map<ASTOCLInvariant, Integer> anonymousInvToNumber = new HashMap<>();

  public OCLWithErrorMessagesJavaGenVisitor(IndentPrinter printer) {
    this.printer = printer;
  }


  @Override
  public OCLWithErrorMessagesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(OCLWithErrorMessagesTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTOCLInvariant node) {
    // check method
    printMethodSignature(node, "boolean", "check");
    printer.println("{");
    printer.indent();

    printer.println("try {");
    printer.indent();
    printer.println();

    printer.print("return ");
    node.getExpression().accept(getTraverser());
    printer.println(";");
    printer.unindent();

    printer.println("} catch(Exception __e) {");
    printer.indent();
    printer.println("return false;");
    printer.unindent();
    printer.println("}");

    printer.unindent();
    printer.println("}");

    if (node.isPresentInvErrorMessages()) {
      printErrorMsgMethod(node);
    }
  }

  protected void printErrorMsgMethod(ASTOCLInvariant node) {
    printer.println();
    printMethodSignature(node, "Optional<ValidationError>", "getError");
    printer.println("{");
    printer.indent();

    printer.print("if(!check");
    if (node.isPresentName()) {
      printer.print(node.getName());
    } else {
      printer.print("Inv" + anonymousInvToNumber.computeIfAbsent(node, n -> anonymousInvCounter++));
    }
    printer.print("(");

    List<ASTOCLContextDefinition> oclContextDefinitionList = node.getOCLContextDefinitionList();
    for (int i = 0; i < oclContextDefinitionList.size(); i++) {
      ASTOCLContextDefinition t1 = oclContextDefinitionList.get(i);
      if (i != 0) {
        printer.print(", ");
      }

      if (t1.isPresentMCType()) {
        // Anonymous parameter
        printer.print(" _this");
      } else if (t1.isPresentOCLParamDeclaration()) {
        printer.print(t1.getOCLParamDeclaration().getName());
      } else {
        throw new IllegalStateException("Can not print parameter for OCLWithErrorMessages");
      }
    }

    printer.println(")){");
    printer.indent();

    printer.println("return Optional.of(new ValidationError(");
    printer.indent();

    node.getInvErrorMessages().getShortError().accept(getTraverser());
    printer.print(", ");
    printer.println();
    node.getInvErrorMessages().getFullError().accept(getTraverser());

    printer.unindent();
    printer.println("));");
    printer.unindent();
    printer.println("}");

    printer.println("return Optional.empty();");
    printer.unindent();
    printer.println("}");
  }

  private void printMethodSignature(ASTOCLInvariant node, String returnType, String prefix) {
    printer.print("public static " + returnType + " " + prefix);
    if (node.isPresentName()) {
      printer.print(node.getName());
    } else {
      printer.print("Inv" + anonymousInvToNumber.computeIfAbsent(node, n -> anonymousInvCounter++));
    }

    printer.print("(");

    List<ASTOCLContextDefinition> oclContextDefinitionList = node.getOCLContextDefinitionList();
    for (int i = 0; i < oclContextDefinitionList.size(); i++) {
      ASTOCLContextDefinition t1 = oclContextDefinitionList.get(i);
      if (i != 0) {
        printer.print(", ");
      }
      t1.accept(getTraverser());

      // Anonymous parameter
      if (t1.isPresentMCType()) {
        printer.print(SymTypeExpression2JavaConverter.getJavaTypePrint(TypeCheck3.symTypeFromAST(t1.getMCType())));
        printer.print(" _this");
      }
    }

    printer.println(")");
  }
}
