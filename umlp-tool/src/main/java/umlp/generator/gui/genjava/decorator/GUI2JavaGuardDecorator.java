package umlp.generator.gui.genjava.decorator;

import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIGuard;
import umlp.generator.gui.GUIAbstractDecorator;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GUI2JavaGuardDecorator extends GUIAbstractDecorator {

  protected final ASTGUICompilationUnit guiAst;

  public GUI2JavaGuardDecorator(
      final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst) {
    super(glex);
    this.guiAst = guiAst;
  }

  public void transform(ASTCDClass decoratedClass, ASTGUIGuard guard) {
    // collect args from iterate statement
    GUIIterate2ArgsVisitor it2Arg = new GUIIterate2ArgsVisitor();
    it2Arg.transform(guiAst, guard);

    // replace field access with getters, except iterator args
    GUIGuardConditionReplaceVisitor visitor = new GUIGuardConditionReplaceVisitor();
    visitor.setIgnore(it2Arg.getArgs().keySet());
    visitor.transform(guard);

    String getterName = util.gui2cdAttrGetterName(guard.getId().getName(), "condition");
    String nullCheck = "";
    GUINullCheckVisitor nullCheckVisitor = new GUINullCheckVisitor();
    nullCheckVisitor.transform(visitor.getGetterExpression());
    if (nullCheckVisitor.getNullCheckExpression().isPresent()) {
      nullCheck = guiDSLPrinter.prettyprint(nullCheckVisitor.getNullCheckExpression().get());
    }
    String getterValue = guiDSLPrinter.prettyprint(visitor.getGetterExpression());
    boolean isLiteral = visitor.getGetterExpression() instanceof ASTLiteralExpression;

    addGetComponentMethod(decoratedClass, getterName, "boolean",
        getterValue, prepareArguments(it2Arg.getArgs()), isLiteral, nullCheck);
  }

  protected void addGetComponentMethod(
      ASTCDClass decoratedClass, String methodName, String attrType, String returnValue,
      List<String> arguments, boolean isLiteral, String condition
  ) {
    getCD4C().addMethod(
        decoratedClass, getGetterTemplate(), methodName, attrType,
        returnValue, String.join(", ", arguments), isLiteral, condition
    );
  }

  protected String getGetterTemplate() {
    return "umlp.gui2java.GetComponentReplaced";
  }

  protected List<String> prepareArguments(Map<String, String> arguments) {
    List<String> retList = Lists.newArrayList();
    arguments.forEach((k,v) -> retList.add(v + " " + k));
    return retList;
  }

}
