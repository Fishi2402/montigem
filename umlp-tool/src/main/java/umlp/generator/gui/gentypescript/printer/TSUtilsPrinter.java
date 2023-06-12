package umlp.generator.gui.gentypescript.printer;

import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TSUtilsPrinter extends CdUtilsPrinter {

  protected final TypeScriptTypesFullPrettyPrinter printer;

  public TSUtilsPrinter() {
    printer = new TypeScriptTypesFullPrettyPrinter(new IndentPrinter());
  }

  /**
   * Print the string of a ASTModifier type, e.g. abstract private final static
   *
   * @param modifier the ASTModifier object
   * @return a string, e.g. abstract private final static
   */
  public String printModifier(ASTModifier modifier) {
    return printer.prettyprint(modifier);
  }

  /**
   * Converts a list of import statements to a string list.
   *
   * @param importStatements the list of import statements
   * @return a string list of all import statements
   */
  public String printImportList(Collection<ASTMCImportStatement> importStatements) {
    StringBuilder sb = new StringBuilder();
    importStatements.forEach(i -> sb.append(printer.prettyprint(i)).append("\n"));
    return sb.toString();
  }

  /**
   * Converts a list of enum constants to a string list of enum constants
   *
   * @param enumConstants list of enum constants
   * @return a string list of enum constants
   */
  public String printEnumConstants(List<ASTCDEnumConstant> enumConstants) {
    checkNotNull(enumConstants);
    return Joiner.on(",").join(Collections2.transform(enumConstants, ASTCDEnumConstant::getName));
  }

  /**
   * Prints an ASTType
   *
   * @param type an ASTType
   * @return String representation of the ASTType
   */
  public String printType(ASTMCType type) {
    return printer.prettyprint(type);
  }

  public String printType(ASTMCReturnType type) {
    return printer.prettyprint(type);
  }

  // TODO AGe
  public boolean isWildCardGeneric(ASTMCReturnType type) {
    return type.printType().contains("? extends");
  }

  // TODO AGe
  public boolean isAbstract(ASTCDClass type) {
    boolean res = false;
    for (ASTCDMethod m : type.getCDMethodList()) {
      res |= m.getModifier().isAbstract();
    }
    return res || type.getModifier().isAbstract();
  }

  public String printExpression(ASTExpression expr) {
    return printer.prettyprint(expr);
  }

  public String printCDParametersDecl(List<ASTCDParameter> parameterList) {
    checkNotNull(parameterList);
    return Joiner.on(",")
        .join(
            Collections2.transform(
                parameterList,
                arg0 ->
                    arg0.getAnnotation().map(a -> a + " ").orElse("")
                        + arg0.getName()
                        + ": "
                        + printType(arg0.getMCType())
            ));
  }

}