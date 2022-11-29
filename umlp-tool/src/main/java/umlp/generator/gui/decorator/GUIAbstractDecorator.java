package umlp.generator.gui.decorator;

import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._symboltable.GUIVariableSymbol;
import de.monticore.guidsl.prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import umlp.generator.AbstractDecorator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class GUIAbstractDecorator extends AbstractDecorator {

  //public static final Predicate<ASTGUIComponent> PREDICATE_BUTTON = c -> c.getQualifiedName().equals("umlp.gui.Button");
  public static final Predicate<ASTGUIComponent> PREDICATE_TABLE = c -> c.getQualifiedName().equals("umlp.gui.Table");
  public static final Predicate<ASTGUIComponent> PREDICATE_PIE = c -> c.getQualifiedName().equals("umlp.gui.Pie");
  public static final Predicate<ASTGUIComponent> PREDICATE_NOT_TABLE = c -> !c.getQualifiedName().equals("umlp.gui.Table");
  //public static final Predicate<ASTGUIComponent> PREDICATE_NOT_BUTTON = c -> !c.getQualifiedName().equals("umlp.gui.Button");

  public static final String COMPONENT_SUFFIX = "";
  public static final String CREATE_INPUT_VALUE_SUFFIX = "Create";
  public static final String EDIT_INPUT_VALUE_SUFFIX = "Edit";

  public static MCBasicTypesFullPrettyPrinter basicTypesPrinter;
  public static GUIDSLFullPrettyPrinter guiDSLPrinter;

  public GUIAbstractDecorator(final GlobalExtensionManagement glex) {
    super(glex);
    basicTypesPrinter = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
    guiDSLPrinter = new GUIDSLFullPrettyPrinter();
  }

  public static String getComponentClassName(ASTGUIComponentDeclaration guiAst, boolean isInputGui) {
    return guiAst.getName() + (isInputGui ? EDIT_INPUT_VALUE_SUFFIX : "");
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

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
  }

  // TODO MB
  public Optional<VariableSymbol> resolveAttribute(Optional<GUIVariableSymbol> guiVariableSymbol, String name) {
    if (guiVariableSymbol.isEmpty()) {
      return Optional.empty();
    }
    TypeSymbol typeInfo = guiVariableSymbol.get().getType().getTypeInfo();
    Optional<VariableSymbol> res = typeInfo.getSpannedScope().resolveVariable(name);
    if (res.isEmpty() && typeInfo.isPresentSuperClass()) {
      res = typeInfo.getSuperClass().getTypeInfo().getSpannedScope().resolveVariable(name);
    }
    return res;
  }
}
