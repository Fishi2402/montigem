/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.genjava.decorator.GUI2JavaComponentDecorator;
import umlp.generator.gui.gentypescript.printer.TypeScriptTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GUI2TypeScriptComponentDecorator extends GUI2JavaComponentDecorator {

  public GUI2TypeScriptComponentDecorator(final GlobalExtensionManagement glex,
      ASTGUICompilationUnit guiAst, GUIComponentsVisitor viewComponent) {
    super(glex, guiAst, viewComponent);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
  }

  // TODO AGe: error concept is not ready, leave that out for now
  /*
  protected void addComponentErrorAttribute(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName, Optional<ASTGUIIterate> iterate) {
    String componentAttributeName = StringTransformations.uncapitalize(guiComponent.getName()) + "ValueError";
    getCD4C().addAttribute(decoratedClass, true, false, "protected String " + componentAttributeName + " = \"\";");
  }
  /**/

  @Override
  protected String convertType(String mcType) {
    String paramType = TypeScriptTypes.toTSType(mcType);
    // TODO AGe: detect underspecified types
    if (paramType.equals("Array")) {
      paramType = "Array<any>";
    }
    return paramType;
  }

  @Override
  protected String unknownCollectionType() {
    return "any";
  }

  @Override
  protected void setDefaultListValue(ASTCDAttribute attr) {
    glex.replaceTemplate("cd2java.Value", attr,
        new StringHookPoint(" = []"));
  }

  @Override
  public List<String> getArgs(Map<String, String> args) {
    List<String> res = new ArrayList<>();
    for (Map.Entry<String, String> arg : args.entrySet()) {
      res.add(arg.getValue() + " " + arg.getKey());
    }
    return res;
  }

  @Override
  protected String getInitListsTemplate() {
    return "umlp.gui2ts.TSInitLists";
  }

  @Override
  protected String getGetterTemplate() {
    return "umlp.gui2ts.TSGetComponentReplaced";
  }

  @Override
  protected String getSetterTemplate() {
    return "umlp.gui2ts.TSSetComponent";
  }

  @Override
  protected String getSetterReplacedTemplate() {
    return "umlp.gui2ts.TSSetComponentReplaced";
  }

  @Override
  protected Function<String, String> dateToStringConversion() {
    return s -> s + "?.toDateString()";
  }

  @Override
  protected Function<String, String> stringToDateConversion() {
    return s -> "new Date(" + s + ")";
  }

  @Override
  protected Function<String, String> numberToStringConversion() {
    return s -> s + "+ ''";
  }

  @Override
  protected Function<String, String> stringToNumberConversion(String type) {
    return s -> "+" + s;
  }

  @Override
  protected Function<String, String> gemlistToArrayConversion() {
    return s -> s + ".toArray()";
  }

}
