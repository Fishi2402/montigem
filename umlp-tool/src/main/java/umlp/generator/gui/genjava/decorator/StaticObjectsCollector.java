package umlp.generator.gui.genjava.decorator;

import de.monticore.ast.ASTNode;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIArgValue;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._ast.ASTGUIList;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import de.monticore.guidsl._symboltable.BuiltInTypes;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StaticObjectsCollector implements GUIDSLVisitor2, CommonExpressionsVisitor2, ExpressionsBasisVisitor2 {

  protected GUIDSLTraverser traverser;

  protected final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

  protected static StaticObjectsCollector instance;

  protected int counter = 0;
  protected String curComp = "";
  protected String curArg = "";
  protected Optional<ASTGUINamedArg> lastArg = Optional.empty();
  protected Optional<ASTCallExpression> lastCall = Optional.empty();
  protected Map<ASTGUIList, SymTypeExpression> listTypes = new HashMap<>();

  protected Map<ASTNode, ASTCDAttribute> node2Attr = new HashMap<>();

  public static StaticObjectsCollector getInstance() {
    if (instance == null) {
      instance = new StaticObjectsCollector();
    }
    return instance;
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  private StaticObjectsCollector() {
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
    traverser.add4CommonExpressions(this);
    traverser.add4ExpressionsBasis(this);
  }

  public void init(ASTGUIComponentDeclaration node) {
    counter = 0;
    if (node.isPresentGUIBlock()) {
      node.getGUIBlock().accept(getTraverser());
    }
  }

  public Map<ASTNode, ASTCDAttribute> getNode2Attr() {
    return node2Attr;
  }

  @Override
  public void visit(ASTGUIComponent node) {
    curComp = node.getId().getName();
  }

  @Override
  public void visit(ASTGUIIterate node) {
    curComp = node.getId().getName();
  }

  @Override
  public void visit(ASTGUIShortIterateControl node) {
    curArg = GUI2JavaClassDecorator.ITERATE_ARG;
  }

  @Override
  public void visit(ASTGUIGuard node) {
    curComp = node.getId().getName();
    curArg = GUI2JavaClassDecorator.GUARD_ARG;
  }

  @Override
  public void visit(ASTGUINamedArg node) {
    curArg = node.getName();
    lastArg = Optional.of(node);
  }

  @Override
  public void visit(ASTGUIList node) {
    if (!listTypes.containsKey(node)
        && lastArg.isPresent()
        && lastArg.get().getGUIArgValue().isPresentGUIList()
        && lastArg.get().getGUIArgValue().getGUIList().equals(node)) {
      if (!lastArg.get().isPresentNameSymbol()) {
        Log.error("0x00012 Unable to find defining symbol for argument '"
            + lastArg.get().getName() + "'.");
        return;
      }
      recordTypes(node, lastArg.get().getNameSymbol().getType());
    }
    String type = printType(node);
    String name = curComp + StringTransformations.capitalize(curArg) + "_" + counter++;
    ASTCDAttribute attr = CDAttributeFacade.getInstance().createAttribute(
        CDModifier.PROTECTED.build(), type, name);
    node2Attr.put(node, attr);
  }

  @Override
  public void visit(ASTCallExpression node) {
    lastCall = Optional.of(node);
  }

  @Override
  public void visit(ASTNameExpression node) {
    if (lastCall.isPresent() && lastCall.get().getExpression().equals(node)) {
      boolean isConstructor = false;
      if (node.getEnclosingScope() instanceof IOOSymbolsScope) {
        IOOSymbolsScope scope = (IOOSymbolsScope) node.getEnclosingScope();
        Optional<OOTypeSymbol> sym = scope.resolveOOType(node.getName());
        isConstructor = sym.isPresent() && sym.get().isIsClass();
      }
      else {
        Log.error("0x0000C Attempted to resolve type of '"
            + GUIGeneratorUtil.getInstance().print(node) + "' from outside of an OOSymbolsScope,"
            + " but expression conversion is restricted to OOSymbols context.");
      }
      if (isConstructor) {
        String type = node.getName();
        String name = curComp + StringTransformations.capitalize(curArg) + "_" + counter++;
        ASTCDAttribute attr = CDAttributeFacade.getInstance().createAttribute(
            CDModifier.PROTECTED.build(), type, name);
        node2Attr.put(lastCall.get(), attr);
      }
    }
  }

  protected void recordTypes(ASTGUIList node, SymTypeExpression ste) {
    if (!listTypes.containsKey(node)) {
      listTypes.put(node, ste);
    }
    for (ASTGUIArgValue val : node.getGUIArgValueList()) {
      if (val.isPresentGUIList()) {
        if (!ste.isGenericType()
            || !ste.asGenericType().getTypeConstructorFullName().equals(BuiltInTypes.LIST.getQName())) {
          Log.error("0x00013 Unexpected non-generic type for a list '" + util.print(node) + "'.");
          return;
        }
        recordTypes(val.getGUIList(), ste.asGenericType().getArgument(0));
      }
    }
  }

  protected String printType(ASTGUIList node) {
    if (listTypes.containsKey(node)) {
      return listTypes.get(node).print();
    }
    StringBuilder sb = new StringBuilder(BuiltInTypes.LIST.getName());
    if (!node.getGUIArgValueList().isEmpty()) {
      sb.append("<");
      sb.append(printType(node.getGUIArgValue(0)));
      sb.append(">");
    }
    return sb.toString();
  }

  protected String printType(ASTExpression node) {
    SymTypeExpression ste = util.getType(node);
    if (ste.isPrimitive()) {
      return ste.asPrimitive().getBoxedPrimitiveName();
    }
    return ste.print();
  }

  protected String printType(ASTGUIArgValue node) {
    if (node.isPresentGUIList()) {
      printType(node.getGUIList());
    }
    if (node.isPresentGUIComponent() || node.isPresentGUIGuard() || node.isPresentGUIIterate()) {
      return BuiltInTypes.GUI_VIEW_ELEMENT.getName();
    }
    if (node.isPresentExpression()) {
      return printType(node.getExpression());
    }
    Log.error("0x00011 Unexpected empty ASTGUIArgValue");
    return "";
  }

}
