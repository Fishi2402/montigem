/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.ast.ASTNode;
import de.monticore.cd.codegen.TopDecorator;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIArgValue;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.guidsl._ast.ASTGUIRangeIterateControl;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import de.monticore.guidsl._prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.guidsl._symboltable.BuiltInTypes;
import de.monticore.guidsl._symboltable.GUIComponentDeclarationSymbol;
import de.monticore.guidsl._symboltable.GUIParamSymbol;
import de.monticore.guidsl._symboltable.IGUIDSLArtifactScope;
import de.monticore.guidsl._symboltable.IGUIDSLScope;
import de.monticore.io.paths.MCPath;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclaration;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import org.gradle.internal.impldep.org.apache.commons.lang.ArrayUtils;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.gui.gentypescript.printer.TSImportMap;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GUIGeneratorUtil {

  // Fully qualified Name of the classdiagram, e.g. "hardy.Flensburg"
  private List<String> DOMAIN_MODELS;
  // Fully qualified Name of a gui model, e.g. "stanley.PersonCreate"
  private List<String> CREATE_MODELS;

  protected final String TPL_INTERN = "TPL_INTERN";
  protected final String TPL_SUFFIX = "TPL";

  protected static GUIGeneratorUtil instance;

  protected GUIDSLFullPrettyPrinter printer;

  private GUIGeneratorUtil() {
    printer = new GUIDSLFullPrettyPrinter(new IndentPrinter());
  }

  public static GUIGeneratorUtil getInstance() {
    if (instance == null) {
      instance = new GUIGeneratorUtil();
    }
    return instance;
  }

  public void init(List<String> domainModels, List<String> createModels) {
    DOMAIN_MODELS = domainModels;
    CREATE_MODELS = createModels;
  }

  public final String print(ASTNode node) {
    printer.setPrintComments(false);
    return printer.prettyprint(node);
  }

  // TODO AGe: unreliable
  public final boolean isTop(String name) {
    return name.endsWith(TopDecorator.TOP_SUFFIX);
  }

  public final boolean isInputGui(ASTGUICompilationUnit guiAst, List<ASTGUIComponent> inputComponents) {
    return !inputComponents.isEmpty() &&
        guiAst.getGUIComponentDeclaration().getGUIParamList().stream()
            .filter(p -> isDomainType(p)).findAny().isPresent();
  }

  public final boolean isDomainType(ASTGUIParam astguiParam) {
    return isDomainType(astguiParam.getSymbol().getType());
  }

  public final boolean isDomainType(SymTypeExpression type) {
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

  public final boolean isDomainImport(ASTMCImportStatement i) {
    for (String cd : DOMAIN_MODELS) {
      if (i.getQName().startsWith(cd)) {
        return true;
      }
    }
    return false;
  }

  public final boolean isGuiImport(ASTMCImportStatement i) {
    return GUIDSLMill.globalScope().resolveGUIComponentDeclaration(i.getQName()).isPresent();
  }

  // TODO AGe: indicator of "main" model after cdmerge is built in
  public final Optional<String> getMainDomainModelQName() {
    if (!DOMAIN_MODELS.isEmpty()) {
      return Optional.of(DOMAIN_MODELS.get(0));
    }
    return Optional.empty();
  }

  public final Optional<String> getMainDomainModelSimpleName() {
    Optional<String> qName = getMainDomainModelQName();
    if (qName.isPresent()) {
      String[] parts = qName.get().split("\\.");
      return Optional.of(parts[parts.length - 1]);
    }
    return Optional.empty();
  }

  public final boolean isCreateModel(String componentQName) {
    return CREATE_MODELS.contains(componentQName);
  }

  public boolean isInputComponent(ASTGUIComponent guiComponent) {
    Optional<GUIComponentDeclarationSymbol> guiComponentSymbol = guiComponent.getEnclosingScope()
        .resolveGUIComponentDeclaration(guiComponent.getQualifiedName());
    return !guiComponentSymbol.isPresent() || guiComponent.getGUINamedArgs().getGUINamedArgList().stream()
        .anyMatch(a -> isInputArgument(guiComponentSymbol.get(), a));
  }

  public boolean isInputArgument(ASTGUIComponent guiComponent, ASTGUINamedArg arg) {
    Optional<GUIComponentDeclarationSymbol> guiComponentSymbol = guiComponent.getEnclosingScope()
        .resolveGUIComponentDeclaration(guiComponent.getQualifiedName());
    return !guiComponentSymbol.isPresent() || isInputArgument(guiComponentSymbol.get(), arg);
  }

  public boolean isComponentArgument(ASTGUIComponent guiComponent, ASTGUIArgValue arg) {
    return arg.isPresentGUIComponent();
  }

  public List<ASTGUIComponent> getComponentAsArguments(ASTGUIComponent guiComponent) {
    return guiComponent.getGUINamedArgs().getGUINamedArgList().stream().map(a -> a.getGUIArgValue()).filter(a -> isComponentArgument(guiComponent, a))
        .map(a -> a.getGUIComponent()).collect(Collectors.toList());
  }

/*  public  List<ASTGUINamedArg> getComponentArguments(ASTGUIComponent guiComponent) {
    return guiComponent.getGUINamedArgs().getGUINamedArgList().stream().filter(a -> isComponentArgument(guiComponent, a))
       .collect(Collectors.toList());
  }*/

  public List<ASTGUIArgValue> getComponentArguments(ASTGUIComponent guiComponent) {
    List<ASTGUIArgValue> values = new ArrayList<>();
    values.addAll(guiComponent.getGUINamedArgs().getGUINamedArgList().stream().map(a -> a.getGUIArgValue())
        .filter(a -> isComponentArgument(guiComponent, a)).collect(Collectors.toList()));
    values.addAll(guiComponent.getGUINamedArgs().getGUINamedArgList().stream().map(a -> a.getGUIArgValue())
        .filter(a -> a.isPresentGUIList()).flatMap(a -> a.getGUIList().getGUIArgValueList().stream())
        .filter(v -> isComponentArgument(guiComponent, v)).collect(Collectors.toList()));
    return values;
  }

  public List<ASTGUIArgValue> getComponentListArguments(ASTGUIComponent guiComponent, ASTGUIArgValue arg) {
    List<ASTGUIArgValue> values = new ArrayList<>();
    if (!arg.isPresentGUIList()) {
      return values;
    }
    values.addAll(arg.getGUIList().getGUIArgValueList().stream().filter(v -> isComponentArgument(guiComponent, v)).collect(Collectors.toList()));
    return values;
  }

  public Optional<GUIParamSymbol> resolveGUIParamSym(
      IGUIDSLScope scope, String compName, String argName
  ) {
    Optional<GUIComponentDeclarationSymbol> declSym = scope.resolveGUIComponentDeclaration(compName);
    if (declSym.isPresent()) {
      return declSym.get().getSpannedScope().resolveGUIParam(argName);
    }
    return Optional.empty();
  }

  public String gui2cdAttrName(String domainParamName) {
    return getHTMLName(domainParamName);
  }

  public String gui2cdAttrName(String componentId, String argName) {
    return getHTMLName(componentId + StringTransformations.capitalize(argName));
  }

  public String gui2cdAttrGetterName(String domainParamName) {
    return getHTMLName("get" + StringTransformations.capitalize(domainParamName));
  }

  public String gui2cdAttrSetterName(String domainParamName) {
    return getHTMLName("set" + StringTransformations.capitalize(domainParamName));
  }

  public String gui2cdAttrGetterName(String componentId, String argName) {
    return getHTMLName("get"
        + StringTransformations.capitalize(componentId)
        + StringTransformations.capitalize(argName));
  }

  public String gui2cdAttrSetterName(String componentId, String argName) {
    return getHTMLName("set"
        + StringTransformations.capitalize(componentId)
        + StringTransformations.capitalize(argName));
  }

  public String gui2cdEventCallName(ASTGUIComponent guiComponent, String eventName) {
    // TODO AGe: connected to GUI2HtmlTrafo problem
    //  setting the name in trafo makes derivation of method names incorrect
    return getHTMLName(StringTransformations.uncapitalize(
        getComponentActualName(guiComponent.getId().getName())
    ) + StringTransformations.capitalize(eventName));
  }

  public String getIteratorItemName(ASTGUIIterate iterate) {
    return getHTMLName("i" + iterate.getId().getName());
  }

  // TODO AGe: remove when problem with RangeControl is resolved
  public String getShortIteratorItemName(ASTGUIIterate iterate) {
    if (iterate.getIterateControl() instanceof ASTGUIShortIterateControl) {
      return getHTMLName(
          ((ASTGUIShortIterateControl) iterate.getIterateControl()).getItem().getName());
    }
    Log.error("0x00004 iterate construct " + iterate.getId().getName()
        + " was not processed: range iterate control not supported yet");
    return "";
  }

  public String getIteratorCounterName(ASTGUIIterate iterate) {
    return getHTMLName("c" + iterate.getId().getName());
  }

  // TODO AGe: remove when problem with RangeControl is resolved
  public String getShortIteratorIterable(ASTGUIIterate iterate) {
    if (iterate.getIterateControl() instanceof ASTGUIShortIterateControl) {
      return getHTMLName(
          print(((ASTGUIShortIterateControl) iterate.getIterateControl()).getIterable()));
    }
    Log.error("0x00004 iterate construct " + iterate.getId().getName()
        + " was not processed: range iterate control not supported yet");
    return "";
  }

  public String getIteratorIterable(ASTGUIIterate iterate) {
    if (iterate.getIterateControl() instanceof ASTGUIRangeIterateControl) {
      ASTGUIRangeIterateControl control = (ASTGUIRangeIterateControl) iterate.getIterateControl();
      IterableExtractVisitor visitor = IterableExtractVisitor.getInstance();
      control.getEnd().accept(visitor.getTraverser());
      Optional<ASTExpression> res = visitor.getOut();
      if (res.isPresent()) {
        return getHTMLName(print(res.get()));
      }
    }
    Log.error("0x00003 iterate construct " + iterate.getId().getName()
        + " was not processed: no range iterate control found");
    return "";
  }

  public String getHTMLName(String name) {
    return name.replaceAll(TPL_INTERN, "").replaceAll(TPL_SUFFIX, "");
  }

  public boolean isComponentListArgument(ASTGUIComponent guiComponent, ASTGUIArgValue arg) {
    return arg.isPresentGUIList() && arg.getGUIList().getGUIArgValueList()
        .stream().anyMatch(a -> a.isPresentGUIComponent());
  }

  public boolean isEventComponent(ASTGUIComponent guiComponent) {
    return guiComponent.getGUINamedArgs().getGUINamedArgList().stream()
        .anyMatch(a -> isEventArgument(guiComponent, a));
  }

  public boolean isCreateClass(ASTGUICompilationUnit ast) {
    return CREATE_MODELS.contains(ast.getComponentQName());
  }

  public boolean isInputArgument(GUIComponentDeclarationSymbol guiComponent, ASTGUINamedArg arg) {
    Optional<GUIParamSymbol> argSymbol = guiComponent.getSpannedScope().resolveGUIParam(arg.getName());
    return argSymbol.isPresent() && argSymbol.get().isIsReference();
  }

  public boolean isEventArgument(ASTGUIComponent guiComponent, ASTGUINamedArg arg) {
    Optional<GUIParamSymbol> paramSym = resolveGUIParamSym(
        arg.getEnclosingScope(), guiComponent.getQualifiedName(), arg.getName());
    return paramSym.isPresent() && isEventArgument(paramSym.get());
  }

  public boolean isEventArgument(GUIParamSymbol paramSym) {
    return paramSym.getType().getTypeInfo().getName().equals(BuiltInTypes.EVENT.getName());
  }

  public boolean isStringLiteral(ASTGUIArgValue argValue) {
    return argValue.getExpression() instanceof ASTLiteralExpression
        && ((ASTLiteralExpression) argValue.getExpression()).getLiteral() instanceof ASTStringLiteral;
  }

  public List<ASTGUINamedArg> getInputArguments(ASTGUIComponent guiComponent) {
    Optional<GUIComponentDeclarationSymbol> guiComponentSymbol = guiComponent.getEnclosingScope().resolveGUIComponentDeclaration(guiComponent.getQualifiedName());
    if (guiComponentSymbol.isEmpty()) {
      return new ArrayList<>();
    }
    return guiComponent.getGUINamedArgs().getGUINamedArgList().stream().filter(a -> isInputArgument(guiComponentSymbol.get(), a)).collect(Collectors.toList());
  }

  public List<ASTGUINamedArg> getEventArguments(ASTGUIComponent guiComponent) {
    return guiComponent.getGUINamedArgs().getGUINamedArgList().stream()
        .filter(a -> isEventArgument(guiComponent, a)).collect(Collectors.toList());
  }

  public List<ASTGUIParam> getGUIViewElementParameters(ASTGUICompilationUnit gui) {
    return gui.getGUIComponentDeclaration().getGUIParamList().stream()
        .filter(p -> isViewElementsCollection(p)).collect(Collectors.toList());
  }

  public boolean isViewElementsCollection(ASTGUIParam param) {
    return param.isPresentSymbol() && isViewElementsCollection(param.getSymbol().getType());
  }

  public boolean hasGetter(ASTGUIComponent comp, ASTGUINamedArg arg) {
    Optional<GUIParamSymbol> paramSym = resolveGUIParamSym(
        arg.getEnclosingScope(), comp.getQualifiedName(), arg.getName());
    return paramSym.isPresent() && hasGetter(paramSym.get().getType());
  }

  public boolean hasGetter(SymTypeExpression ste) {
    String type = ste.getTypeInfo().getName();
    return !isViewElement(ste)
        && !isViewElementsCollection(ste)
        && !type.equals(BuiltInTypes.EVENT.getName());
  }

  public boolean isViewElementsCollection(ASTGUIComponent comp, ASTGUINamedArg arg) {
    Optional<GUIParamSymbol> paramSym = resolveGUIParamSym(
        arg.getEnclosingScope(), comp.getQualifiedName(), arg.getName());
    return paramSym.isPresent() && isViewElementsCollection(paramSym.get().getType());
  }

  public boolean isViewElement(ASTGUIComponent comp, ASTGUINamedArg arg) {
    Optional<GUIParamSymbol> paramSym = resolveGUIParamSym(
        arg.getEnclosingScope(), comp.getQualifiedName(), arg.getName());
    return paramSym.isPresent() && isViewElement(paramSym.get().getType());
  }

  public boolean isViewElementsCollection(SymTypeExpression ste) {
    boolean res = false;
    if (ste.isGenericType()) {
      List<SymTypeExpression> arguments = ((SymTypeOfGenerics) ste).getArgumentList();
      for (SymTypeExpression arg : arguments) {
        res = res
            || isViewElement(arg)
            || isViewElementsCollection(arg);
      }
    }
    return res;
  }

  public boolean isViewElement(SymTypeExpression ste) {
    return ste.getTypeInfo().getName().equals(BuiltInTypes.GUI_VIEW_ELEMENT.getName());
  }

  public boolean isNgTemplate(String compName) {
    return compName.endsWith(TPL_INTERN);
  }

  public String getNgTemplateName(String name) {
    return name + TPL_INTERN;
  }

  public String getNgTemplateActualName(String internName) {
    return getComponentActualName(internName) + TPL_SUFFIX;
  }

  public String getComponentActualName(String internName) {
    return internName.replace(TPL_INTERN, "");
  }

  public String getComponentNameFromCamelCaseName(String camelCaseName) {
    return String.join("-", splitCamelCase(camelCaseName)).toLowerCase();
  }

  public String[] splitCamelCase(String str) {
    if (str == null) {
      return null;
    }
    else if (str.length() == 0) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    else {
      char[] c = str.toCharArray();
      List<String> list = new ArrayList();
      int tokenStart = 0;
      int currentType = Character.getType(c[tokenStart]);

      for (int pos = tokenStart + 1; pos < c.length; ++pos) {
        int type = Character.getType(c[pos]);
        if (type != currentType) {
          if (type == 2 && currentType == 1) {
            int newTokenStart = pos - 1;
            if (newTokenStart != tokenStart) {
              list.add(new String(c, tokenStart, newTokenStart - tokenStart));
              tokenStart = newTokenStart;
            }
          }
          else {
            list.add(new String(c, tokenStart, pos - tokenStart));
            tokenStart = pos;
          }

          currentType = type;
        }
      }

      list.add(new String(c, tokenStart, c.length - tokenStart));
      return list.toArray(new String[list.size()]);
    }
  }

  public String getModuleName(String qualifiedModulName) {
    String moduleName = qualifiedModulName;
    if (qualifiedModulName.lastIndexOf(".") > 0) {
      moduleName = qualifiedModulName.substring(qualifiedModulName.lastIndexOf(".") + 1);
    }
    return moduleName;
  }

  public String getModuleClassName(String compName) {
    return StringTransformations.capitalize(compName.toLowerCase()) + "Module";
  }

  public List<String> getRoutingIds(ASTGUICompilationUnit decoratedAst) {
    List<String> ids = new ArrayList<>();
    if (!isCreateModel(decoratedAst.getComponentQName())) {
      for (ASTGUIParam p : decoratedAst.getGUIComponentDeclaration().getGUIParamList()) {
        if (p.isPresentSymbol()) {
          if (!GenService.isList(p.getSymbol().getType()) && isDomainType(p)) {
            ids.add(p.getName() + "RoutingId");
          }
        }
        else {
          Log.warn("0x00008 parameter " + p.getName()
              + " of component " + decoratedAst.getComponentName()
              + " was not found. Routing information may be incomplete.");
        }
      }
    }
    return ids;
  }

  public String getGenFileFolder(ASTMCPackageDeclaration pDecl) {
    return String.join("/", pDecl.getMCQualifiedName().getPartsList());
  }

  public String getIterateName(String iterateCompName) {
    return "getIterate" + StringTransformations.capitalize(iterateCompName) + "It()" + ".get(idx)";
  }

  public String getIterateIndex() {
    return "int idx";
  }

  public String getQualifiedNameForModule(ASTGUIComponent node) {
    String qualifiedModuleName = node.getName();
    Optional<GUIComponentDeclarationSymbol> symbol = node.getEnclosingScope().resolveGUIComponentDeclaration(node.getName());
    if (symbol.isPresent() && symbol.get().getFullName().lastIndexOf(".") > 0) {
      String fullName = symbol.get().getFullName();
      qualifiedModuleName = fullName.substring(0, fullName.lastIndexOf("."));
    }
    return qualifiedModuleName;
  }

  public String gui2htmlArgValue(ASTGUIArgValue node) {
    if (node.isPresentGUIComponent()) {
      return node.getGUIComponent().getId().getName();
    }
    if (node.isPresentGUIGuard()) {
      return node.getGUIGuard().getId().getName();
    }
    if (node.isPresentGUIIterate()) {
      return node.getGUIIterate().getId().getName();
    }
    if (node.isPresentExpression()) {
      return print(node) + TPL_SUFFIX;
    }
    if (node.isPresentGUIList()) {
      String res = "[";
      for (ASTGUIArgValue v : node.getGUIList().getGUIArgValueList()) {
        res += gui2htmlArgValue(v) + ", ";
      }
      res += "]";
      return res;
    }
    Log.error("0x00005 corrupt ASTGUIArgValue node " + node
        + ": missing actual value");
    return "";
  }

  public String java2tsModule(String javaPackage) {
    String res = "@src/" + qName2path(javaPackage) + "Module";

    for (TSImportMap type : TSImportMap.values()) {
      if (javaPackage.startsWith(type.getJavaPackage())) {
        res = type.getTSPackage();
      }
    }

    return res;
  }

  public Optional<String> getRelativeHandwrittenPath(
      MCPath targetPath, Path outPath, String qualifiedName, String fileExtension) {
    String hwFile = Names.getPathFromPackage(qualifiedName) + "." + fileExtension;
    Optional<URL> hwFilePath = targetPath.find(hwFile);
    Optional<String> res = Optional.empty();
    if (hwFilePath.isPresent()) {
      Optional<Path> hwPath = MCPath.toPath(hwFilePath.get());
      res = Optional.of(outPath.resolve(hwFile).getParent()
          .toAbsolutePath()
          .normalize()
          .relativize(hwPath.get().toAbsolutePath())
          .toString()
          .replaceAll("\\\\", "/"));

      hwPath.ifPresent(path -> Reporting.reportUseHandwrittenCodeFile(path,
          Paths.get(hwFile)));
    }
    Reporting.reportHWCExistenceCheck(targetPath, Paths.get(hwFile), hwFilePath);
    return res;
  }

  /*
  public Optional<String> getRelativeHandwrittenPath(
      MCPath hwPath, Path outPath, ASTMCPackageDeclaration packageDecl, String fileName) {
    Collection<Path> hwFilePaths = new ArrayList<>();
    for (Path p : hwPath.getEntries()) {
      hwFilePaths.add(getFilePath(p, packageDecl));
    }
    String localOutPath = getGenFileFolder(packageDecl);
    Path outFolder = outPath.resolve(localOutPath);
    return getRelativeHandwrittenPath(hwPath, hwFilePaths, outFolder, fileName);
  }

  public Path getFilePath(Path input, ASTMCPackageDeclaration packageDecl) {
    String localPath = String.join("/", packageDecl.getMCQualifiedName().getPartsList());
    return Paths.get(input.toString(), localPath);
  }

  public Optional<String> getRelativeHandwrittenPath(
      MCPath hwMCPath,
      Collection<Path> hwPath,
      Path outputFolder,
      String fileName
  ) {
    Iterator<Path> targetPathIt = hwPath.iterator();
    while (targetPathIt.hasNext()) {
      Path it = targetPathIt.next();
      Path hwFile = it.resolve(fileName);
      if (Files.exists(hwFile)) {
        Reporting.reportHWCExistenceCheck(hwMCPath, Paths.get(hwHtmlPath.get()).toAbsolutePath(), hwFilePath);
        String path = outputFolder
            .toAbsolutePath()
            .normalize()
            .relativize(hwFile.toAbsolutePath())
            .toString();
        path = path.replace("\\", "/");
        return Optional.of(path);
      }
    }
    return Optional.empty();
  }
  /**/

  public String removeExtension(String filePath) {
    int index = filePath.lastIndexOf(".");
    if (index > 0) {
      return filePath.substring(0, index);
    }
    return filePath;
  }

  public String qName2path(String parts) {
    return parts.replaceAll("\\.", "/");
  }

  // TODO: remove this method after package names fixed!!
  public String removeDiagramName(String name) {
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
