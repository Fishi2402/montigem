/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.util;

import org.jsweet.transpiler.OverloadScanner;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.ArrayAccessElement;
import org.jsweet.transpiler.model.AssignmentElement;
import org.jsweet.transpiler.model.AssignmentWithOperatorElement;
import org.jsweet.transpiler.model.BinaryOperatorElement;
import org.jsweet.transpiler.model.CaseElement;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ForeachLoopElement;
import org.jsweet.transpiler.model.IdentifierElement;
import org.jsweet.transpiler.model.ImportElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.model.NewArrayElement;
import org.jsweet.transpiler.model.NewClassElement;
import org.jsweet.transpiler.model.TypeCastElement;
import org.jsweet.transpiler.model.UnaryOperatorElement;
import org.jsweet.transpiler.util.Util;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Skips delegate adapter whenever condition is met, otherwise calls the method
 * of the delegate.
 */
public abstract class ConditionalAdapter extends PrinterAdapter {

  protected boolean skip = false;
  protected final PrinterAdapter grandparent;

  public ConditionalAdapter(PrinterAdapter parent) {
    super(parent);
    if (parent.getParentAdapter() == null) {
      throw new RuntimeException("0x00032 Attempted to apply a " + this.getClass().getSimpleName()
          + " to the default PrinterAdapter. The default PrinterAdapter must be unwrapped.");
    }
    this.grandparent = parent.getParentAdapter();
  }

  abstract boolean skipCondition();

  @Override
  public void beforeCompilationUnit() {
    skip = skipCondition();
    if (skip) {
      this.grandparent.beforeCompilationUnit();
      return;
    }
    super.beforeCompilationUnit();
  }

  @Override
  public void addTypeMapping(BiFunction<ExtendedElement, String, Object> mappingFunction) {
    if (skip) {
      this.grandparent.addTypeMapping(mappingFunction);
      return;
    }
    super.addTypeMapping(mappingFunction);
  }

  @Override
  public PrinterAdapter print(ExtendedElement element) {
    if (skip) {
      return this.grandparent.print(element);
    }
    return super.print(element);
  }

  @Override
  public PrinterAdapter print(String string) {
    if (skip) {
      return this.grandparent.print(string);
    }
    return super.print(string);
  }

  @Override
  public PrinterAdapter print(Name name) {
    if (skip) {
      return this.grandparent.print(name);
    }
    return super.print(name);
  }

  public PrinterAdapter println() {
    if (skip) {
      return this.grandparent.println();
    }
    return super.println();
  }

  public PrinterAdapter printArgList(List<? extends ExtendedElement> args) {
    if (skip) {
      return this.grandparent.printArgList(args);
    }
    return super.printArgList(args);
  }

  public PrinterAdapter printIdentifierList(String prefix, int count) {
    if (skip) {
      return this.grandparent.printIdentifierList(prefix, count);
    }
    return super.printIdentifierList(prefix, count);
  }

  public void print(String exprStr, ExtendedElement expr) {
    if (skip) {
      this.grandparent.print(exprStr, expr);
      return;
    }
    super.print(exprStr, expr);
  }

  public PrinterAdapter printIndent() {
    if (skip) {
      return this.grandparent.printIndent();
    }
    return super.printIndent();
  }

  public boolean substituteArrayAccess(ArrayAccessElement arrayAccess) {
    if (skip) {
      return this.grandparent.substituteArrayAccess(arrayAccess);
    }
    return super.substituteArrayAccess(arrayAccess);
  }

  public boolean substituteBinaryOperator(BinaryOperatorElement binaryOperator) {
    if (skip) {
      return this.grandparent.substituteBinaryOperator(binaryOperator);
    }
    return super.substituteBinaryOperator(binaryOperator);
  }

  public boolean substituteUnaryOperator(UnaryOperatorElement unaryOperator) {
    if (skip) {
      return this.grandparent.substituteUnaryOperator(unaryOperator);
    }
    return super.substituteUnaryOperator(unaryOperator);
  }

  public boolean substituteIdentifier(IdentifierElement identifier) {
    if (skip) {
      return this.grandparent.substituteIdentifier(identifier);
    }
    return super.substituteIdentifier(identifier);
  }

  public boolean substituteExtends(TypeElement type) {
    if (skip) {
      return this.grandparent.substituteExtends(type);
    }
    return super.substituteExtends(type);
  }

  public boolean substituteImplements(TypeElement type) {
    if (skip) {
      return this.grandparent.substituteImplements(type);
    }
    return super.substituteImplements(type);
  }

  public boolean substituteNewClass(NewClassElement newClass) {
    if (skip) {
      return this.grandparent.substituteNewClass(newClass);
    }
    return super.substituteNewClass(newClass);
  }

  public boolean substituteAssignedExpression(TypeMirror type, ExtendedElement assignedExpression) {
    if (skip) {
      return this.grandparent.substituteAssignedExpression(type, assignedExpression);
    }
    return super.substituteAssignedExpression(type, assignedExpression);
  }

  public boolean substituteOverloadMethodBody(TypeElement parentTypeElement, OverloadScanner.Overload overload) {
    if (skip) {
      return this.grandparent.substituteOverloadMethodBody(parentTypeElement, overload);
    }
    return super.substituteOverloadMethodBody(parentTypeElement, overload);
  }

  public boolean substituteMethodBody(TypeElement parentTypeElement, ExecutableElement method) {
    if (skip) {
      return this.grandparent.substituteMethodBody(parentTypeElement, method);
    }
    return super.substituteMethodBody(parentTypeElement, method);
  }

  public String getVariableInitialValue(VariableElement variable) {
    if (skip) {
      return this.grandparent.getVariableInitialValue(variable);
    }
    return super.getVariableInitialValue(variable);
  }

  public String needsImport(ImportElement importElement, String qualifiedName) {
    if (skip) {
      return this.grandparent.needsImport(importElement, qualifiedName);
    }
    return super.needsImport(importElement, qualifiedName);
  }

  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    if (skip) {
      return this.grandparent.substituteMethodInvocation(invocation);
    }
    return super.substituteMethodInvocation(invocation);
  }

  public boolean substituteNewArrayWithVariableLength(NewArrayElement newArray) {
    if (skip) {
      return this.grandparent.substituteNewArrayWithVariableLength(newArray);
    }
    return super.substituteNewArrayWithVariableLength(newArray);
  }

  public boolean substituteType(TypeElement type) {
    if (skip) {
      return this.grandparent.substituteType(type);
    }
    return super.substituteType(type);
  }

  public boolean substituteExecutable(ExecutableElement executable) {
    if (skip) {
      return this.grandparent.substituteExecutable(executable);
    }
    return super.substituteExecutable(executable);
  }

  public boolean substituteVariable(VariableElement variable) {
    if (skip) {
      return this.grandparent.substituteVariable(variable);
    }
    return super.substituteVariable(variable);
  }

  public boolean substituteAssignment(AssignmentElement assignment) {
    if (skip) {
      return this.grandparent.substituteAssignment(assignment);
    }
    return super.substituteAssignment(assignment);
  }

  public boolean substituteAssignmentWithOperator(AssignmentWithOperatorElement assignment) {
    if (skip) {
      return this.grandparent.substituteAssignmentWithOperator(assignment);
    }
    return super.substituteAssignmentWithOperator(assignment);
  }

  /*
  public AbstractTreePrinter getPrinter() {
    if (skip) {
      return this.delegate.getPrinter();
    }
    return super.getPrinter();
  }

  public void setPrinter(AbstractTreePrinter printer) {
    if (skip) {
      this.delegate.setPrinter(printer);
      return;
    }
    super.setPrinter(printer);
  }
 /**/

  public Set<String> getErasedTypes() {
    if (skip) {
      return this.grandparent.getErasedTypes();
    }
    return super.getErasedTypes();
  }

  public boolean substituteForEachLoop(ForeachLoopElement foreachLoop, boolean targetHasLength, String indexVarName) {
    if (skip) {
      return this.grandparent.substituteForEachLoop(foreachLoop, targetHasLength, indexVarName);
    }
    return super.substituteForEachLoop(foreachLoop, targetHasLength, indexVarName);
  }

  public boolean eraseSuperClass(TypeElement type, TypeElement superClass) {
    if (skip) {
      return this.grandparent.eraseSuperClass(type, superClass);
    }
    return super.eraseSuperClass(type, superClass);
  }

  public boolean eraseSuperInterface(TypeElement type, TypeElement superInterface) {
    if (skip) {
      return this.grandparent.eraseSuperInterface(type, superInterface);
    }
    return super.eraseSuperInterface(type, superInterface);
  }

  public boolean isSubstituteSuperTypes() {
    if (skip) {
      return this.grandparent.isSubstituteSuperTypes();
    }
    return super.isSubstituteSuperTypes();
  }

  public boolean substituteInstanceof(String exprStr, ExtendedElement expr, TypeMirror type) {
    if (skip) {
      return this.grandparent.substituteInstanceof(exprStr, expr, type);
    }
    return super.substituteInstanceof(exprStr, expr, type);
  }

  public boolean substituteTypeCast(TypeCastElement castExpression) {
    if (skip) {
      return this.grandparent.substituteTypeCast(castExpression);
    }
    return super.substituteTypeCast(castExpression);
  }

  public boolean substituteCaseStatementPattern(CaseElement caseStatement, ExtendedElement pattern) {
    if (skip) {
      return this.grandparent.substituteCaseStatementPattern(caseStatement, pattern);
    }
    return super.substituteCaseStatementPattern(caseStatement, pattern);
  }

  public boolean substituteSwitchStatementSelector(ExtendedElement selector) {
    if (skip) {
      return this.grandparent.substituteSwitchStatementSelector(selector);
    }
    return super.substituteSwitchStatementSelector(selector);
  }

  public void afterType(TypeElement type) {
    if (skip) {
      this.grandparent.afterType(type);
      return;
    }
    super.afterType(type);
  }

  public void beforeTypeBody(TypeElement type) {
    if (skip) {
      this.grandparent.beforeTypeBody(type);
      return;
    }
    super.beforeTypeBody(type);
  }

  public void afterTypeBody(TypeElement type) {
    if (skip) {
      this.grandparent.afterTypeBody(type);
      return;
    }
    super.afterTypeBody(type);
  }

  public String adaptDocComment(Element element, String commentText) {
    if (skip) {
      return this.grandparent.adaptDocComment(element, commentText);
    }
    return super.adaptDocComment(element, commentText);
  }

  public PrinterAdapter getParentAdapter() {
    if (skip) {
      return this.grandparent.getParentAdapter();
    }
    return super.getParentAdapter();
  }

  public void setParentAdapter(PrinterAdapter parentAdapter) {
    if (skip) {
      this.grandparent.setParentAdapter(parentAdapter);
      return;
    }
    super.setParentAdapter(parentAdapter);
  }

  public Types types() {
    if (skip) {
      return this.grandparent.types();
    }
    return super.types();
  }

  public Util util() {
    if (skip) {
      return this.grandparent.util();
    }
    return super.util();
  }

  public Comparator<ExtendedElement> getClassMemberComparator() {
    if (skip) {
      return this.grandparent.getClassMemberComparator();
    }
    return super.getClassMemberComparator();
  }

  public boolean substituteVariableDeclarationKeyword(VariableElement variable) {
    if (skip) {
      return this.grandparent.substituteVariableDeclarationKeyword(variable);
    }
    return super.substituteVariableDeclarationKeyword(variable);
  }

  public CompilationUnitElement getCompilationUnit() {
    if (skip) {
      return this.grandparent.getCompilationUnit();
    }
    return super.getCompilationUnit();
  }

  public TypeMirror mapType(TypeMirror type) {
    if (skip) {
      return this.grandparent.mapType(type);
    }
    return super.mapType(type);
  }

}
