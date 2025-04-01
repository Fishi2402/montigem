/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import umlp.generator.domain.decorator.GemListAssocDecorator;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;

import java.nio.file.Path;

public class TSGemListAssocDecorator extends GemListAssocDecorator {

  protected final Path outPath;
  protected final MCPath hwPath;

  public TSGemListAssocDecorator(GlobalExtensionManagement glex, MCPath hwPath, Path outPath) {
    super(glex);
    this.hwPath = hwPath;
    this.outPath = outPath;
  }

  @Override
  protected void addConstructors(ASTCDClass ast, String className) {
    glex.replaceTemplate(GUI2ComponentTemplates.CONSTRUCTOR, ast,
        new TemplateHookPoint(getConstructorTemplate(), className));
  }

  @Override
  protected String getConstructorEmptyTemplate() {
    return "umlp.gui2ts.gemlist.assocs.TSConstructorEmpty";
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.gemlist.assocs.TSConstructor";
  }

  @Override
  protected String getInternalRemoveTemplate() {
    return "umlp.gui2ts.gemlist.assocs.TSInternalRemove";
  }

  @Override
  protected String getInternalSetTemplate() {
    return "umlp.gui2ts.gemlist.assocs.TSInternalSet";
  }

  @Override
  protected String getInternalAddTemplate() {
    return "umlp.gui2ts.gemlist.assocs.TSInternalAdd";
  }

  @Override
  protected String getGetOwnerTemplate() {
    return "umlp.gui2ts.gemlist.assocs.TSGetOwner";
  }

  @Override
  protected String getLoadObjectsTemplate() {
    return "umlp.gui2ts.gemlist.TSLoadObjects";
  }

  @Override
  protected String getDeepEqualsTemplate() {
    return "umlp.gui2ts.gemlist.TSDeepEquals";
  }

  @Override
  protected void markDeepEqualsBackendOnly(ASTCDType targetType, ASTCDMethodSignature m) {
    // Ignore
  }
}
