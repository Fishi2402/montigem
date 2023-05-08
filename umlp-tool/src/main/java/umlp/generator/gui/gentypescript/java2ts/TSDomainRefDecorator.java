/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import umlp.generator.gui.genjava.decorator.DomainRefDecorator;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class TSDomainRefDecorator extends DomainRefDecorator {

  public TSDomainRefDecorator(final GlobalExtensionManagement glex, boolean isInputGui) {
    super(glex, isInputGui);
  }

  @Override
  protected ASTCDMethod addInitMethod() {
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), "ngOnInit");
    glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("umlp.gui2ts.Init"));
    return method;
  }

  @Override
  protected String getBuildTemplate() {
    return "umlp.gui2ts.Build";
  }

  @Override
  protected String getGetterParamTemplate() {
    return "umlp.gui2ts.GetParam";
  }

  protected String getConstructorWithBuilderTemplate() {
    return "umlp.gui2ts.ConstructorWithBuilder";
  }

  protected String getConstructorTemplate() {
    return "umlp.gui2ts.ConstructorTS";
  }

  protected String getParam(String type, String name) {
    return type + " " + name;
  }

}
