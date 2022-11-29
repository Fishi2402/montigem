/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import com.google.common.collect.Lists;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.guidsl._symboltable.GUIFunctionSymbol;
import de.monticore.guidsl._symboltable.GUIVariableSymbol;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GUIGeneratorUtil {

  public static final String STRING = "String";

  public static final String LONG = "Long";

  public static final List<String> UMLP_TYPES = Lists.newArrayList(STRING, LONG);

  private GUIGeneratorUtil() {}

  public static final boolean isInputGui(ASTGUICompilationUnit guiAst, List<ASTGUIComponent> inputComponents) {
    return !inputComponents.isEmpty() &&
        guiAst.getGUIComponentDeclaration().getGUIParamList().stream()
            .filter(p -> isDomainType(p)).findAny().isPresent();
  }

  public static final boolean isDomainType(ASTGUIParam astguiParam) {
    SymTypeExpression type = astguiParam.getSymbol().getType();
    String typeName = type.getTypeInfo().getName();
    return !astguiParam.getSymbol().getType().isGenericType()
        && !BasicSymbolsMill.PRIMITIVE_LIST.contains(typeName)
        && !UMLP_TYPES.contains(typeName);
  }

  public static boolean isInputComponent(ASTGUIComponent guiComponent) {
    Optional<GUIFunctionSymbol> guiComponentSymbol = guiComponent.getEnclosingScope().resolveGUIFunction(guiComponent.getQualifiedName());
    return !guiComponentSymbol.isPresent() || guiComponent.getGUINamedArgs().getGUINamedArgList().stream().filter(a
        -> isInputArgument(guiComponentSymbol.get(), a)).findAny().isPresent();
  }

  public static boolean isEventComponent(ASTGUIComponent guiComponent) {
    Optional<GUIFunctionSymbol> guiComponentSymbol = guiComponent.getEnclosingScope().resolveGUIFunction(guiComponent.getQualifiedName());
    return !guiComponentSymbol.isPresent() || guiComponent.getGUINamedArgs().getGUINamedArgList().stream().filter(a
        -> isEventArgument(guiComponentSymbol.get(), a)).findAny().isPresent();
  }

  public static boolean isInputArgument(GUIFunctionSymbol guiComponent, ASTGUINamedArg arg) {
    Optional<GUIVariableSymbol> argSymbol = guiComponent.getSpannedScope().resolveGUIVariable(arg.getName());
    return argSymbol.isPresent() && argSymbol.get().isIsReference();
  }

  public static boolean isEventArgument(GUIFunctionSymbol guiComponent, ASTGUINamedArg arg) {
    Optional<GUIVariableSymbol> argSymbol = guiComponent.getSpannedScope().resolveGUIVariable(arg.getName());
    return argSymbol.isPresent() && argSymbol.get().getType().getTypeInfo().getName().equals("Event");
  }

  public static List<ASTGUINamedArg> getInputArguments(ASTGUIComponent guiComponent) {
    Optional<GUIFunctionSymbol> guiComponentSymbol = guiComponent.getEnclosingScope().resolveGUIFunction(guiComponent.getQualifiedName());
    if (!guiComponentSymbol.isPresent()) {
      return new ArrayList<>();
    }
    return guiComponent.getGUINamedArgs().getGUINamedArgList().stream().filter(a -> isInputArgument(guiComponentSymbol.get(), a)).collect(Collectors.toList());
  }

  public static List<ASTGUINamedArg> getEventArguments(ASTGUIComponent guiComponent) {
    Optional<GUIFunctionSymbol> guiComponentSymbol = guiComponent.getEnclosingScope().resolveGUIFunction(guiComponent.getQualifiedName());
    if (!guiComponentSymbol.isPresent()) {
      return new ArrayList<>();
    }
    return guiComponent.getGUINamedArgs().getGUINamedArgList().stream().filter(a -> isEventArgument(guiComponentSymbol.get(), a)).collect(Collectors.toList());
  }
}
