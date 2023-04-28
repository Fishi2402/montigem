/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.guidsl._ast.*;
import de.monticore.guidsl._symboltable.GUIComponentDeclarationSymbol;
import de.monticore.guidsl._symboltable.GUIParamSymbol;
import de.monticore.guidsl._symboltable.IGUIDSLArtifactScope;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.StringTransformations;
import org.gradle.internal.impldep.org.apache.commons.lang.ArrayUtils;
import umlp.generator.domain.decorator.GenService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GUIGeneratorUtil {

  public static final String STRING = "String";

  public static final String LONG = "Long";

  public static final String VIEW_ELEMENT = "GUIViewElement";

  public static final List<String> UMLP_TYPES = Collections.unmodifiableList(Lists.newArrayList(STRING, LONG));

  // Fully qualified Name of the classdiagram, e.g. "hardy.Flensburg"
  private static List<String> DOMAIN_MODELS;
  private static List<String> CREATE_MODELS;

  private GUIGeneratorUtil() {}

  public static void init(List<String> domainModels, List<String> createModels) {
    DOMAIN_MODELS = domainModels;
    CREATE_MODELS = createModels;
  }

  public static final boolean isInputGui(ASTGUICompilationUnit guiAst, List<ASTGUIComponent> inputComponents) {
    return !inputComponents.isEmpty() &&
        guiAst.getGUIComponentDeclaration().getGUIParamList().stream()
            .filter(p -> isDomainType(p)).findAny().isPresent();
  }

  public static final boolean isDomainType(ASTGUIParam astguiParam) {
    return isDomainType(astguiParam.getSymbol().getType());
  }

  public static final boolean isDomainType(SymTypeExpression type) {
    if (!type.hasTypeInfo()) {
      return false;
    }
    TypeSymbol ts = type.getTypeInfo();
    if (!(ts.getEnclosingScope() instanceof IGUIDSLArtifactScope)) {
      return false;
    }
    IGUIDSLArtifactScope as = (IGUIDSLArtifactScope) ts.getEnclosingScope();
    return DOMAIN_MODELS.contains(as.getFullName());
  }

  public static final boolean isDomainImport(ASTMCImportStatement i) {
    for (String cd : DOMAIN_MODELS) {
      if (i.getQName().startsWith(cd)) {
        return true;
      }
    }
    return false;
  }

  public static final boolean isCreateModel(String componentQName) {
    return CREATE_MODELS.contains(componentQName);
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

  public static boolean isComponentArgument(ASTGUIComponent guiComponent, ASTGUIArgValue arg) {
    return arg.isPresentGUIComponent();
  }

  public static List<ASTGUIComponent> getComponentAsArguments(ASTGUIComponent guiComponent) {
    return guiComponent.getGUINamedArgs().getGUINamedArgList().stream().map(a -> a.getGUIArgValue()).filter(a -> isComponentArgument(guiComponent, a))
        .map(a -> a.getGUIComponent()).collect(Collectors.toList());
  }

/*  public static List<ASTGUINamedArg> getComponentArguments(ASTGUIComponent guiComponent) {
    return guiComponent.getGUINamedArgs().getGUINamedArgList().stream().filter(a -> isComponentArgument(guiComponent, a))
       .collect(Collectors.toList());
  }*/

  public static List<ASTGUIArgValue> getComponentArguments(ASTGUIComponent guiComponent) {
    List<ASTGUIArgValue> values = new ArrayList<>();
    values.addAll(guiComponent.getGUINamedArgs().getGUINamedArgList().stream().map(a -> a.getGUIArgValue())
        .filter(a -> isComponentArgument(guiComponent, a)).collect(Collectors.toList()));
    values.addAll(guiComponent.getGUINamedArgs().getGUINamedArgList().stream().map(a -> a.getGUIArgValue())
        .filter(a -> a.isPresentGUIList()).flatMap(a -> a.getGUIList().getGUIArgValueList().stream())
        .filter(v -> isComponentArgument(guiComponent, v)).collect(Collectors.toList()));
    return values;
  }

  public static List<ASTGUIArgValue> getComponentListArguments(ASTGUIComponent guiComponent, ASTGUIArgValue arg) {
    List<ASTGUIArgValue> values = new ArrayList<>();
    if (!arg.isPresentGUIList()) {
      return values;
    }
    values.addAll(arg.getGUIList().getGUIArgValueList().stream().filter(v -> isComponentArgument(guiComponent, v)).collect(Collectors.toList()));
    return values;
  }

  public static ASTGUIComponent getNestedComponent(ASTGUIArgValue arg) {
    return arg.getGUIComponent();
  }

  public static boolean isComponentListArgument(ASTGUIComponent guiComponent, ASTGUIArgValue arg) {
    return arg.isPresentGUIList() && arg.getGUIList().getGUIArgValueList()
        .stream().filter(a -> a.isPresentGUIComponent()).findAny().isPresent();
  }

  public static boolean isEventComponent(ASTGUIComponent guiComponent) {
    Optional<GUIComponentDeclarationSymbol> guiComponentSymbol = guiComponent.getEnclosingScope()
        .resolveGUIComponentDeclaration(guiComponent.getQualifiedName());
    //Optional<FunctionSymbol> guiComponentSymbol = GUIDSLMill.globalScope().resolveFunction(guiComponent.getQualifiedName());
    return !guiComponentSymbol.isPresent() || guiComponent.getGUINamedArgs().getGUINamedArgList().stream()
        .filter(a -> isEventArgument(guiComponentSymbol.get(), a)).findAny().isPresent();
  }

  public static boolean isCreateClass(ASTGUICompilationUnit ast) {
    return CREATE_MODELS.contains(ast.getComponentQName());
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

  public static List<ASTGUIParam> getGUIViewElementParameters(ASTGUICompilationUnit gui) {
    return gui.getGUIComponentDeclaration().getGUIParamList().stream().filter(p -> isViewElementsCollection(p)).collect(Collectors.toList());
  }

  public static boolean isViewElementsCollection(ASTGUIParam param) {
    return GenService.isList(param.getMCType()) && GenService.getInnerType(param.getMCType()).equals(VIEW_ELEMENT);
  }

  public static boolean isViewElement(GUIParamSymbol param) {
    return param.getType().getTypeInfo().getName().equals(VIEW_ELEMENT);
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

  public static String getIterateName(String iterateCompName) {
    return "getIterate" + StringTransformations.capitalize(iterateCompName) + "It()" + ".get(idx)";
  }

  public static String getIterateIndex() {
    return "int idx";
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

  // TODO: remove this method after package names fixed!!
  public static String removeDiagramName(String name) {
    int index1 = name.lastIndexOf(".");
    if (index1 == -1) {
      return name;
    }
    String res = name.substring(0, index1);
    int index2 = res.lastIndexOf(".");
    if (index2 == -1) {
      return name;
    }
    return name.substring(0, index2) + name.substring(index1, name.length());
  }
}
