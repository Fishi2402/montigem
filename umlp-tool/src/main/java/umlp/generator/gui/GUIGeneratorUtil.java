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
import org.gradle.internal.impldep.org.apache.commons.lang.ArrayUtils;

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

  public static boolean isInputArgument(ASTGUIComponent guiComponent, ASTGUINamedArg arg) {
   Optional<GUIComponentDeclarationSymbol> guiComponentSymbol = guiComponent.getEnclosingScope()
        .resolveGUIComponentDeclaration(guiComponent.getQualifiedName());
    return !guiComponentSymbol.isPresent() || isInputArgument(guiComponentSymbol.get(), arg);
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

  public static boolean isEventArgument(ASTGUIComponent guiComponent, ASTGUINamedArg arg) {
    Optional<GUIComponentDeclarationSymbol> guiComponentSymbol = guiComponent.getEnclosingScope()
        .resolveGUIComponentDeclaration(guiComponent.getQualifiedName());
    return !guiComponentSymbol.isPresent() || isEventArgument(guiComponentSymbol.get(), arg);
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

  public static String getComponentNameFromCamelCaseName(String camelCaseName) {
    return String.join("-", splitCamelCase(camelCaseName)).toLowerCase();
  }

  public static String[] splitCamelCase(String str) {
    if (str == null) {
      return null;
    } else if (str.length() == 0) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    } else {
      char[] c = str.toCharArray();
      List list = new ArrayList();
      int tokenStart = 0;
      int currentType = Character.getType(c[tokenStart]);

      for(int pos = tokenStart + 1; pos < c.length; ++pos) {
        int type = Character.getType(c[pos]);
        if (type != currentType) {
          if (type == 2 && currentType == 1) {
            int newTokenStart = pos - 1;
            if (newTokenStart != tokenStart) {
              list.add(new String(c, tokenStart, newTokenStart - tokenStart));
              tokenStart = newTokenStart;
            }
          } else {
            list.add(new String(c, tokenStart, pos - tokenStart));
            tokenStart = pos;
          }

          currentType = type;
        }
      }

      list.add(new String(c, tokenStart, c.length - tokenStart));
      return (String[])((String[])list.toArray(new String[list.size()]));
    }
  }

  public static String getModuleName(String qualifiedModulName) {
    String moduleName = qualifiedModulName;
    if (!qualifiedModulName.isEmpty() && qualifiedModulName.lastIndexOf(".") > 0) {
      moduleName = qualifiedModulName.substring(qualifiedModulName.lastIndexOf(".") + 1);
    }
    return moduleName;
  }

  public static String getQualifiedNameForModule(ASTGUIComponent node) {
    String qualifiedModulName = node.getName();
    Optional<GUIComponentDeclarationSymbol> symbol = node.getEnclosingScope().resolveGUIComponentDeclaration(node.getName());
    if (symbol.isPresent() && symbol.get().getFullName().lastIndexOf(".") > 0) {
      String fullName = symbol.get().getFullName();
      qualifiedModulName = symbol.get().getFullName().substring(0, symbol.get().getFullName().lastIndexOf("."));
    }
    return qualifiedModulName;
  }
}
