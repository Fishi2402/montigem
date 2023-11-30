/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.guidsl._prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.guidsl._symboltable.GUIParamSymbol;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class GUIAbstractDecorator extends AbstractDecorator {

  //public static final Predicate<ASTGUIComponent> PREDICATE_BUTTON = c -> c.getQualifiedName().equals("umlp.gui.Button");
  public static final Predicate<ASTGUIComponent> PREDICATE_TABLE = c -> c.getQualifiedName().endsWith("table.GemTable");
  public static final Predicate<ASTGUIComponent> PREDICATE_PIE = c -> c.getQualifiedName().equals("umlp.gui.Pie");
  public static final Predicate<ASTGUIComponent> PREDICATE_NOT_TABLE = c -> !c.getQualifiedName().endsWith("table.GemTable");
  public static final Predicate<ASTGUIComponent> PREDICATE_NOT_BUTTON = c -> !c.getQualifiedName().equals("io.GemButton");

  public static final String COMPONENT_SUFFIX = "Component";

  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();
  public static MCBasicTypesFullPrettyPrinter basicTypesPrinter;
  public static GUIDSLFullPrettyPrinter guiDSLPrinter;

  public GUIAbstractDecorator(final GlobalExtensionManagement glex) {
    super(glex);
    basicTypesPrinter = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    guiDSLPrinter = new GUIDSLFullPrettyPrinter(new IndentPrinter(), false);
  }

  public static String getPackageName(ASTGUICompilationUnit guiAst, String subPackage) {
    String origPackage = Names.constructQualifiedName(guiAst.getMCPackageDeclaration().getMCQualifiedName().getPartsList());
    return (subPackage.isEmpty() ? origPackage : Joiners.DOT.join(origPackage, subPackage)).toLowerCase();
  }

  protected static List<ASTGUIComponent> filterComponentsWithPredicate(Collection<ASTGUIComponent> components,
      Predicate<ASTGUIComponent> predicate) {
    return components.stream().filter(predicate).collect(Collectors.toList());
  }

  protected static List<ASTGUIComponent> filterComponentsWithAllPredicates(Collection<ASTGUIComponent> components,
      Collection<Predicate<ASTGUIComponent>> predicate) {
    List<ASTGUIComponent> toFilter = Lists.newArrayList(components);
    List<ASTGUIComponent> filtered = new ArrayList<>();
    predicate.forEach(p -> {
      filtered.clear();
      toFilter.stream().filter(p).forEach(filtered::add);
      toFilter.clear();
      toFilter.addAll(filtered);
    });
    return filtered;
  }

  protected static List<ASTGUINamedArg> filterArgumentsWithPredicate(List<ASTGUINamedArg> components,
      Predicate<ASTGUINamedArg> predicate) {
    return components.stream().filter(predicate).collect(Collectors.toList());
  }

  public Optional<VariableSymbol> resolveAttribute(Optional<GUIParamSymbol> guiVariableSymbol, String name) {
    if (guiVariableSymbol.isEmpty()) {
      return Optional.empty();
    }
    return guiVariableSymbol.get().getType().getTypeInfo().getSpannedScope().resolveVariable(name);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
  }

  protected boolean isModelImportInGUI(ASTMCImportStatement i) {
    return isCDModelImportInGUI(i) || isGUIModelImportInGUI(i);
  }

  protected boolean isGUIModelImportInGUI(ASTMCImportStatement i) { // TODO MB  guisym
    return GUIDSLMill.globalScope()
        .resolveGUIComponentDeclaration(i.getQName()).isPresent();
  }

  protected boolean isCDModelImportInGUI(ASTMCImportStatement i) { // TODO MB umlpsym UMLPDefSymbol
    return util.isDomainImport(i) || !isGUIModelImportInGUI(i);
  }

  protected ASTMCType transformTypeForAttr(ASTCDClass clazz, ASTGUIParam param) {
    if (util.isDomainListType(param)) {
      String inner = util.getDomainListInnerType(param);
      getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("GemList"));
      return MCTypeFacade.getInstance().createBasicGenericTypeOf("GemList", inner);
    }
    return param.getMCType();
  }

  protected String getParam(SymTypeExpression type, String name) {
    if (util.isDomainListType(type)) {
      return PathUtil.getBackendRTECommonPath("GemList") + " " + name;
    }
    return type.getTypeInfo().getName() + " " + name;
  }
}
