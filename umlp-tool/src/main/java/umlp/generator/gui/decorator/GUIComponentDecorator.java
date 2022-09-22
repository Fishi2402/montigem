package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.AbstractDecorator;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class GUIComponentDecorator extends AbstractDecorator {

  protected static final String INPUT_ARG_NAME = "value";

  public GUIComponentDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public abstract void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent);

  protected void addComponentErrorAttribute(ASTCDClass decoratedClass, ASTGUIComponent guiComponent) {
    String componentAttributeName = StringTransformations.uncapitalize(guiComponent.getName()) + "Error";
    getCD4C().addAttribute(decoratedClass, true, false, "protected String " + componentAttributeName + " = \"\";");
  }

  protected void addGetComponentMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String returnExpr, String returnType) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.components.GetComponent", "get" + guiComponent.getName(),
        returnType, returnExpr);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
  }

  protected static List<ASTGUINamedArg> filterArgWithPredicate(List<ASTGUINamedArg> components,
                                                               Predicate<ASTGUINamedArg> predicate) {
    return components.stream().filter(predicate).collect(Collectors.toList());
  }
}
