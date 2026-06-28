package umlp.generator.ocl.javagen;

import de.monticore.codegen.CodeGenSymTypeExpressionConverter;
import de.monticore.codegen.javagen.JavaGenSymTypeRelations;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.ocl.oclexpressions._ast.ASTTypeIfExpression;
import de.monticore.ocl.oclexpressions.codegen.javagen.OCLExpressionsJavaGenVisitor;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class OCLTypeIfJavaGenVisitor extends OCLExpressionsJavaGenVisitor {
  protected final OCLTypeNarrowingContext typeNarrowingContext;

  public OCLTypeIfJavaGenVisitor(JavaGenVisitorState state, OCLTypeNarrowingContext typeNarrowingContext) {
    super(state);
    this.typeNarrowingContext = typeNarrowingContext;
  }

  @Override
  public void traverse(ASTTypeIfExpression node) {
    SymTypeExpression typeIfType = SymTypeRelations.normalize(TypeCheck3.typeOf(node));
    SymTypeExpression narrowedType = SymTypeRelations.normalize(TypeCheck3.symTypeFromAST(node.getMCType()));
    String varName = getVarName(node);

    if (!JavaGenSymTypeRelations.generatesToJavaRuntimeIdentifiableType(narrowedType)) {
      Log.error(
          "0x6785C typeif cannot check type " + narrowedType.printFullName()
              + " at runtime",
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }

    state.printExpressionBeginLambda(typeIfType);
    getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(typeIfType));
    getPrinter().print(" ");
    getPrinter().print(varName);
    state.endStatement();

    getPrinter().print("if (");
    getPrinter().print(node.getName());
    getPrinter().print(" instanceof ");
    getPrinter().print(SymTypeExpression2JavaConverter.getTypeErasedJavaTypePrint(narrowedType));
    getPrinter().println(") {");
    getPrinter().indent();
    getPrinter().print(varName);
    getPrinter().print(" = ");
    typeNarrowingContext.push(node.getName(), narrowedType);
    CodeGenSymTypeExpressionConverter.printConverted(
        getPrinter(),
        typeIfType,
        TypeCheck3.typeOf(node.getThenExpression().getExpression()),
        printer -> node.getThenExpression().accept(getTraverser()));
    typeNarrowingContext.pop(node.getName());
    state.endStatement();
    getPrinter().unindent();

    getPrinter().println("} else {");
    getPrinter().indent();
    getPrinter().print(varName);
    getPrinter().print(" = ");
    CodeGenSymTypeExpressionConverter.printConverted(
        getPrinter(),
        typeIfType,
        TypeCheck3.typeOf(node.getElseExpression()),
        printer -> node.getElseExpression().accept(getTraverser()));
    state.endStatement();
    getPrinter().unindent();
    getPrinter().println("}");

    getPrinter().print("return ");
    getPrinter().print(varName);
    state.endStatement();
    state.printExpressionEndLambda();
  }
}
