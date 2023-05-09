/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._symboltable.BuiltInTypes;
import umlp.generator.gui.genjava.decorator.DomainRefDecorator;
import umlp.generator.gui.gentypescript.printer.TypeScriptTypes;

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

  @Override
  protected String getConstructorWithBuilderTemplate() {
    return "umlp.gui2ts.ConstructorWithBuilder";
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.ConstructorTS";
  }

  @Override
  protected void addImportForGUIParameter(ASTCDClass clazz, String qName) {
    if (BuiltInTypes.EVENT.getName().equals(qName)) {
      getCD4C().addImport(clazz, qName);
      return;
    }
    super.addImportForGUIParameter(clazz, qName);
  }
}
