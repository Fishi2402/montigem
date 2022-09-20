package umlp.generator.gui.decorator;

import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.AbstractDecorator;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

public abstract class GUIComponentDecorator extends AbstractDecorator {

  protected static final String INPUT_ARG_NAME = "value";

  public GUIComponentDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public abstract void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent);

  protected void addComponentErrorAttribute(ASTCDClass decoratedClass, ASTGUIComponent guiComponent) {
    String componentAttributeName = StringTransformations.uncapitalize(guiComponent.getName()) + "Error";
    ASTCDAttribute compError = getCD4C().addAttribute(decoratedClass, "protected String " + componentAttributeName + " = \"\";");
    // TODO GV: add getMethod for compError
  }

  protected void addNotifyMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String attrName) {
    ASTCDMethod notify = CDMethodFacade.getInstance().createMethod(CDModifier.PUBLIC.build(),
        "notify" + StringTransformations.capitalize(attrName) + "Invalid",
        CDParameterFacade.getInstance().createParameter("String", "msg"));

    glex.replaceTemplate(EMPTY_BODY, notify, new StringHookPoint(StringTransformations.uncapitalize(guiComponent.getName())
        + "Error = msg;"));
    decoratedClass.addCDMember(notify);
  }

  protected void addGetComponentMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String returnExpr, String returnType) {
    ASTCDMethod getComponent = CDMethodFacade.getInstance().createMethod(CDModifier.PUBLIC.build(), returnType, "get" + guiComponent.getName());
    glex.replaceTemplate(EMPTY_BODY, getComponent, new StringHookPoint("return " + returnExpr + ";"));
    decoratedClass.addCDMember(getComponent);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
  }
}
