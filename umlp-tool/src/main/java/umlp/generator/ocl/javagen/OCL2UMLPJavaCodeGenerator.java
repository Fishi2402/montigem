package umlp.generator.ocl.javagen;

import de.monticore.codegen.AbstractCodeGenVisitor;
import de.monticore.codegen.javagen.JavaGenSymTypeExpressionConverter;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.expressions.bitexpressions.codegen.javagen.BitExpressionsJavaGenVisitor;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions.codegen.javagen.CommonExpressionsJavaGenVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis.codegen.javagen.ExpressionsBasisJavaGenVisitor;
import de.monticore.literals.mccommonliterals.codegen.javagen.MCCommonLiteralsJavaGenVisitor;
import de.monticore.ocl.types3.OCLCollectionSymTypeRelations;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import oclwitherrormessages.OCLWithErrorMessagesMill;
import oclwitherrormessages._visitor.OCLWithErrorMessagesTraverser;
import umlp.generator.ocl.TypeUtil;
import umlp.generator.ocl.javagen.field_access.FieldAccessInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

public class OCL2UMLPJavaCodeGenerator extends AbstractCodeGenVisitor {
  OCLWithErrorMessagesTraverser traverser;
  protected String fullPackage;

  public OCL2UMLPJavaCodeGenerator(IndentPrinter printer) {
    super(printer);
    init();
  }

  public OCL2UMLPJavaCodeGenerator(IndentPrinter printer, String fullPackage) {
    super(printer);
    this.fullPackage = fullPackage;
    init();
  }

  public void init() {
    traverser = OCLWithErrorMessagesMill.traverser();
    Map<String, Stack<SymTypeExpression>> knowTypeOfVar = new HashMap<>();

    JavaGenSymTypeExpressionConverter.init();
    SymTypeExpression2UMLPJavaConverter.init();
    OCL2JavaOperationPrinter.init();

    // Literals

    MCCommonLiteralsJavaGenVisitor visMCCommonLiterals = new MCCommonLiteralsJavaGenVisitor(getPrinter());
    traverser.setMCCommonLiteralsHandler(visMCCommonLiterals);

    // Expressions

    BitExpressionsJavaGenVisitor visBitExpressions = new BitExpressionsJavaGenVisitor(getPrinter());
    traverser.setBitExpressionsHandler(visBitExpressions);

    CommonExpressionsJavaGenVisitor visCommonExpressions = new CommonExpressionsJavaGenVisitor(getPrinter()) {
      @Override
      public void handle(ASTCallExpression node) {
        boolean isFieldAccess = OCLWithErrorMessagesMill.typeDispatcher().isCommonExpressionsASTFieldAccessExpression(node.getExpression());
        boolean handled = false;

        if (isFieldAccess) {
          ASTFieldAccessExpression fa = OCLWithErrorMessagesMill.typeDispatcher().asCommonExpressionsASTFieldAccessExpression(node.getExpression());
          if ("asSet".equals(fa.getName())) {
            getPrinter().print("new HashSet<>(");
            fa.getExpression().accept(getTraverser());
            getPrinter().print(")");
            handled = true;
          } else if ("asList".equals(fa.getName())) {
            getPrinter().print("new ArrayList<>(");
            fa.getExpression().accept(getTraverser());
            getPrinter().print(")");
            handled = true;
          }
        }

        if (!handled) {
          startParentheses();
          node.getExpression().accept(getTraverser());
          startParentheses();
          boolean first = true;
          for (ASTExpression arg : node.getArguments().getExpressionList()) {
            if(!first){
              getPrinter().print(", ");
            }
            arg.accept(getTraverser());
            first = false;
          }
          endParentheses();
          endParentheses();
        }
      }

      @Override
      public void handle(ASTFieldAccessExpression node) {
        // functions should not be replaced by getter name conversion
        if(TypeCheck3.typeOf(node).isFunctionType()){
          node.getExpression().accept(getTraverser());
          printer.print(".");
          printer.print(node.getName());
          return;
        }

        // UMLP specific field access
        node.getExpression().accept(getTraverser());

        FieldAccessInfo info = FieldAccessInfo.from(node);

        SymTypeExpression lhsType = TypeCheck3.typeOf(node.getExpression());
        SymTypeExpression fullType = TypeCheck3.typeOf(node);
        if (OCLCollectionSymTypeRelations.isOCLCollection(lhsType) && OCLCollectionSymTypeRelations.isOCLCollection(fullType)) {
          // flattening

          // check if source type
          boolean isCollectionType = false;
          boolean isDomainType = false;
          boolean isBoolean = false;
          SymTypeExpression varType = null;
          Optional<ISymbol> sourceSymbolOpt = fullType.getSourceInfo().getSourceSymbol();
          if (sourceSymbolOpt.isEmpty()) {
            Log.warn("Can not convert field access since source symbol is not available", node.get_SourcePositionStart(), node.get_SourcePositionEnd());
            return;
          }
          ISymbol sourceSymbol = sourceSymbolOpt.get();
          if (BasicSymbolsMill.typeDispatcher().isBasicSymbolsVariable(sourceSymbol)) {
            VariableSymbol varSymbol = BasicSymbolsMill.typeDispatcher().asBasicSymbolsVariable(sourceSymbol);
            varType = varSymbol.getType();
            isCollectionType = TypeUtil.getInstance().isCollectionType(varType);
            isDomainType = TypeUtil.getInstance().isDomainType(varType);
            isBoolean = SymTypeRelations.isBoolean(varType);
          }

          // - variable with simple type
          if (!isCollectionType && !isDomainType) {
            printFlatteningMap(node.getName(), isBoolean ? "is" : "get", lhsType);
          }

          // - variable with collection simple type
          if (isCollectionType && !isDomainType) {
            Log.warn("Not translating flattening for attribute list of simple type");
            return;
          }

          // - association with opt or one cardinality
          if (!isCollectionType && isDomainType) {
            // TODO: opt needs extra check
            printFlatteningMap(node.getName(), "get", lhsType);
          }

          // - association with opt mult cardinality
          if (isCollectionType && isDomainType) {
            printer.print(".stream().flatMap(it -> it.");
            printer.print("get");
            printer.print(StringTransformations.capitalize(node.getName()));
            printer.print("List().stream()).to");
            printCollectorFor(lhsType);
          }
        } else {
          // normal access
          printFieldAccess(info, node.getName());
        }
      }

      private void printFlatteningMap(String fieldName, String accessorPrefix, SymTypeExpression lhsType) {
        printer.print(".stream().map(it -> it.");
        printer.print(accessorPrefix);
        printer.print(StringTransformations.capitalize(fieldName));
        printer.print("()).to");
        printCollectorFor(lhsType);
      }

      private void printCollectorFor(SymTypeExpression lhsType) {
        if (OCLCollectionSymTypeRelations.isList(lhsType)) {
          printer.print("List()");
        } else if (OCLCollectionSymTypeRelations.isSet(lhsType)) {
          printer.print("Set()");
        } else {
          printer.print("Collection()");
        }
      }

      private void printFieldAccess(FieldAccessInfo info, String nodeName) {
        switch (info.getKind()) {
          case TO_ONE_DOMAIN -> printer.print(".get" + StringTransformations.capitalize(nodeName) + "()");
          case TO_MANY_DOMAIN, COLLECTION_OF_SIMPLE_VALUE ->
              printer.print(".get" + StringTransformations.capitalize(nodeName) + "List()");
          case SIMPLE_VALUE -> {
            if (SymTypeRelations.isBoolean(info.getFieldType())) {
              printer.print(".is" + StringTransformations.capitalize(nodeName) + "()");
            } else {
              printer.print(".get" + StringTransformations.capitalize(nodeName) + "()");
            }
          }
        }
      }
    };
    traverser.setCommonExpressionsHandler(visCommonExpressions);

    ExpressionsBasisJavaGenVisitor visExpressionBasis = new ExpressionsBasisJavaGenVisitor(getPrinter()) {
      @Override
      public void handle(ASTNameExpression node) {
        Optional<VariableSymbol> symbol = ((IBasicSymbolsScope) node.getEnclosingScope()).resolveVariableMany(node.getName()).stream().findAny();
        Optional<TypeSymbol> type = ((IBasicSymbolsScope) node.getEnclosingScope()).resolveTypeMany(node.getName()).stream().findAny();
        if (symbol.isPresent() && OOSymbolsMill.typeDispatcher().isOOSymbolsField(symbol.get()) && ((FieldSymbol) symbol.get()).isIsStatic()) {
          getPrinter().print(symbol.get().getFullName());
        } else if (type.isPresent() && OOSymbolsMill.typeDispatcher().isOOSymbolsOOType(type.get())) {
          getPrinter().print(type.get().getFullName());
        } else {
          // Handle scoped casts, e.g. from type if expression
          if (!knowTypeOfVar.getOrDefault(node.getName(), new Stack<>()).isEmpty()) {
            getPrinter().print("((");
            getPrinter().print(SymTypeExpression2JavaConverter.printModelTypeAsJavaType(knowTypeOfVar.get(node.getName()).peek()));
            getPrinter().print(")");
            getPrinter().print(node.getName());
            getPrinter().print(")");
          } else {
            if ("this".equals(node.getName())) {
              getPrinter().print("_this");
            } else {
              getPrinter().print(node.getName());
            }
          }
        }
      }
    };
    traverser.setExpressionsBasisHandler(visExpressionBasis);

    OCLExpressionsJavaGenVisitor visOCLExpressions = new OCLExpressionsJavaGenVisitor(getPrinter(), knowTypeOfVar);
    traverser.setOCLExpressionsHandler(visOCLExpressions);

    OptionalOperatorsJavaGenVisitor visOptionalOperators = new OptionalOperatorsJavaGenVisitor(getPrinter());
    traverser.setOptionalOperatorsHandler(visOptionalOperators);

    SetExpressionsJavaGenVisitor visSetExpressions = new SetExpressionsJavaGenVisitor(getPrinter());
    traverser.setSetExpressionsHandler(visSetExpressions);

    // OCL
    OCLJavaGenVisitor oclJavaGenVisitor = new OCLJavaGenVisitor(getPrinter(), fullPackage);
    traverser.setOCLHandler(oclJavaGenVisitor);

    // OCLWithErrorMessages
    OCLWithErrorMessagesJavaGenVisitor oclWithErrorMessagesJavaGenVisitor = new OCLWithErrorMessagesJavaGenVisitor(getPrinter());
    traverser.setOCLWithErrorMessagesHandler(oclWithErrorMessagesJavaGenVisitor);
  }

  @Override
  public ITraverser getTraverser() {
    return traverser;
  }
}
