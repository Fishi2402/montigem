/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.guidsl._ast.*;
import de.monticore.guidsl._symboltable.GUIComponentDeclarationSymbol;
import de.monticore.guidsl._symboltable.GUIParamSymbol;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static umlp.generator.gui.GUIAbstractDecorator.CREATE_INPUT_VALUE_SUFFIX;

public class GUIGeneratorUtil {

  public static final String STRING = "String";

  public static final String LONG = "Long";

  public static final List<String> UMLP_TYPES = Collections.unmodifiableList(Lists.newArrayList(STRING, LONG));

  private GUIGeneratorUtil() {}

  public static final boolean isInputGui(ASTGUICompilationUnit guiAst, List<ASTGUIComponent> inputComponents) {
    return !inputComponents.isEmpty() &&
        guiAst.getGUIComponentDeclaration().getGUIParamList().stream()
            .filter(p -> isDomainType(p)).findAny().isPresent();
  }

  public static final boolean isDomainType(ASTGUIParam astguiParam) {
    return isDomainType(astguiParam.getSymbol().getType());
  }

  public static final boolean isDomainType(SymTypeExpression type) {
    String typeName = type.getTypeInfo().getName();
    String fullName = type.getTypeInfo().getFullName();
    return !type.isGenericType()
        && !BasicSymbolsMill.PRIMITIVE_LIST.contains(typeName)
        && !UMLP_TYPES.contains(typeName);
  }

  public static boolean isInputComponent(ASTGUIComponent guiComponent) {
    Optional<GUIComponentDeclarationSymbol> guiComponentSymbol = guiComponent.getEnclosingScope()
        .resolveGUIComponentDeclaration(guiComponent.getQualifiedName());
    return !guiComponentSymbol.isPresent() || guiComponent.getGUINamedArgs().getGUINamedArgList().stream()
        .filter(a -> isInputArgument(guiComponentSymbol.get(), a)).findAny().isPresent();
  }

  public static boolean isEventComponent(ASTGUIComponent guiComponent) {
    Optional<GUIComponentDeclarationSymbol> guiComponentSymbol = guiComponent.getEnclosingScope()
        .resolveGUIComponentDeclaration(guiComponent.getQualifiedName());
    //Optional<FunctionSymbol> guiComponentSymbol = GUIDSLMill.globalScope().resolveFunction(guiComponent.getQualifiedName());
    return !guiComponentSymbol.isPresent() || guiComponent.getGUINamedArgs().getGUINamedArgList().stream()
        .filter(a -> isEventArgument(guiComponentSymbol.get(), a)).findAny().isPresent();
  }

  public static boolean isCreateClass(ASTCDClass decoratedClass) {
    return decoratedClass.getName().endsWith(CREATE_INPUT_VALUE_SUFFIX);
  }

  public static boolean isInputArgument(GUIComponentDeclarationSymbol guiComponent, ASTGUINamedArg arg) {
    Optional<GUIParamSymbol> argSymbol = guiComponent.getSpannedScope().resolveGUIParam(arg.getName());
    return argSymbol.isPresent() && argSymbol.get().isIsReference();
  }

  public static boolean isEventArgument(GUIComponentDeclarationSymbol guiComponent, ASTGUINamedArg arg) {
    Optional<GUIParamSymbol> argSymbol = guiComponent.getSpannedScope().resolveGUIParam(arg.getName());
    return argSymbol.isPresent() && argSymbol.get().getType().getTypeInfo().getName().equals("Event");
  }

  public static boolean isStringLiteral(ASTGUIArgValue argValue) {
    return argValue.getExpression() instanceof ASTLiteralExpression
        && ((ASTLiteralExpression) argValue.getExpression()).getLiteral() instanceof ASTStringLiteral;
  }

  public static List<ASTGUINamedArg> getInputArguments(ASTGUIComponent guiComponent) {
    Optional<GUIComponentDeclarationSymbol> guiComponentSymbol = guiComponent.getEnclosingScope().resolveGUIComponentDeclaration(guiComponent.getQualifiedName());
    if (!guiComponentSymbol.isPresent()) {
      return new ArrayList<>();
    }
    return guiComponent.getGUINamedArgs().getGUINamedArgList().stream().filter(a -> isInputArgument(guiComponentSymbol.get(), a)).collect(Collectors.toList());
  }

  public static List<ASTGUINamedArg> getEventArguments(ASTGUIComponent guiComponent) {
    Optional<GUIComponentDeclarationSymbol> guiComponentSymbol = guiComponent.getEnclosingScope().resolveGUIComponentDeclaration(guiComponent.getQualifiedName());
    if (!guiComponentSymbol.isPresent()) {
      return new ArrayList<>();
    }
    return guiComponent.getGUINamedArgs().getGUINamedArgList().stream().filter(a -> isEventArgument(guiComponentSymbol.get(), a)).collect(Collectors.toList());
  }

}
