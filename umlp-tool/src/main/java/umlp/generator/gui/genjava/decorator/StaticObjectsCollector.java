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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
  protected Map<ASTNode, Method> node2Method = new HashMap<>();

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
    reset();
    CallTypeDeriver.getInstance().init(node);
    node.accept(getTraverser());
  }

  public void reset() {
    counter = 0;
    curComp = "";
    curArg = "";
    lastArg = Optional.empty();
    lastCall = Optional.empty();
    listTypes = new HashMap<>();
    node2Attr = new HashMap<>();
    node2Method = new HashMap<>();
  }

  public Map<ASTNode, ASTCDAttribute> getNode2Attr() {
    return node2Attr;
  }

  public Map<ASTNode, Method> getNode2Method() {
    return node2Method;
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
    if (!listTypes.containsKey(node) && lastArg.isPresent()) {
      if (!lastArg.get().isPresentNameSymbol()) {
        Log.error("0x00012 Unable to find defining symbol for argument '"
            + lastArg.get().getName() + "'.");
        return;
      }
      SymTypeExpression ste = lastArg.get().getNameSymbol().getType();
      if (GUIGeneratorUtil.getInstance().isViewElementsCollection(ste)) {
        return;
      }
      if (lastArg.get().getGUIArgValue().isPresentGUIList()
          && lastArg.get().getGUIArgValue().getGUIList().equals(node)) {
        recordTypes(node, ste);
      }
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
        if (sym.isPresent()) {
          isConstructor = !sym.get().getMethodList(node.getName()).isEmpty();
        }
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
      else if (CallTypeDeriver.getInstance().hasReturnType(lastCall.get())) {
        SymTypeExpression returnType = CallTypeDeriver.getInstance().getReturnType(lastCall.get());
        List<SymTypeExpression> paramTypes = new ArrayList<>();
        for (ASTExpression exp : lastCall.get().getArguments().getExpressionList()) {
          SymTypeExpression argSTE = util.getType(exp);
          if (argSTE.isObscureType()) {
            Log.error("0x00015 Unable to derive the type of expression '"
                + util.print(node)
                + "' at " + node.get_SourcePositionStart().toString()
                + " - " + node.get_SourcePositionEnd().toString()
            );
          }
          paramTypes.add(argSTE);
        }

        Method guiMethod = new Method(returnType, node.getName(), paramTypes);
        for (ASTNode n : node2Method.keySet()) {
          Method m = node2Method.get(n);
          if (guiMethod.getName().equals(m.getName())) {
            if (!guiMethod.compatibleParamTypes(m) && guiMethod.sameParamTypesErasure(m)) {
              Log.error("0x00018 Found methods with the same type erasure: '"
                  + util.print(n) + "' at " + n.get_SourcePositionStart().toString()
                  + " - " + node.get_SourcePositionEnd().toString()
                  + " and "
                  + util.print(lastCall.get())
                  + "' at " + lastCall.get().get_SourcePositionStart().toString()
                  + " - " + lastCall.get().get_SourcePositionEnd().toString()
                  + ". Please rename one of the methods."
              );
            }
            else if (guiMethod.compatibleReturnType(m) && guiMethod.compatibleParamTypes(m)) {
              guiMethod = m;
              break;
            }
            else if (!guiMethod.compatibleReturnType(m) && guiMethod.compatibleParamTypes(m)) {
              Log.error("0x00019 Found methods with the same return types: '"
                  + util.print(n) + "' at " + n.get_SourcePositionStart().toString()
                  + " - " + node.get_SourcePositionEnd().toString()
                  + " and "
                  + util.print(lastCall.get())
                  + "' at " + lastCall.get().get_SourcePositionStart().toString()
                  + " - " + lastCall.get().get_SourcePositionEnd().toString()
                  + ". Please rename one of the methods."
              );
            }
          }
        }
        node2Method.put(lastCall.get(), guiMethod);
      }
      else {
        Log.error("0x00014 Unable to derive the type of call "
            + util.print(lastCall.get())
            + "' at " + lastCall.get().get_SourcePositionStart().toString()
            + " - " + lastCall.get().get_SourcePositionEnd().toString()
            + ". Please make sure that the call is simple, e.g., doStuff(),"
            + " a direct value of"
            + " a GUI component parameter, "
            + " a GUI component argument, "
            + " a guard, "
            + " an iterable item, "
            + " or is inside a block."
        );
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
