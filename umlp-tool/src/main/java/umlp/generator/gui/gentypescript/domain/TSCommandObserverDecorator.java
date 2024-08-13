/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import umlp.generator.domain.decorator.CommandObserverDecorator;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

public class TSCommandObserverDecorator extends CommandObserverDecorator {

  public TSCommandObserverDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected ASTCDClass createEmptyClass(ASTCDClass domainClass, String classname) {
    ASTCDClass emptyClass = super.createEmptyClass(domainClass, classname);

    // replace interface via mixin
    glex.replaceTemplate("umlp.gui2ts.TSClassImplements",
        emptyClass, new TemplateHookPoint("umlp.gui2ts.TSClassExtendsMixin"));
    glex.addBeforeTemplate("umlp.gui2ts.TSEmptyMixin",
        emptyClass, new StringHookPoint(
            domainClass.getName() + ObserverDecorator.SUFFIX + TSObserverMixinDecorator.SUFFIX + "("
        ));
    glex.addAfterTemplate("umlp.gui2ts.TSEmptyMixin",
        emptyClass, new StringHookPoint(")"));

    return emptyClass;
  }

  @Override
  protected void setOverrideAnnotation(ASTCDMethodSignature m) {
    // don't
  }

  @Override
  protected void addImports(ASTCDCompilationUnit origCD, ASTCDType domainClass, ASTCDType clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandManager"));

    for (ASTCDType t : UMLPSymbolTables.getAttributesInHierarchyWithDefiningType(domainClass).values()) {
      ASTCDCompilationUnit def = UMLPSymbolTables.getDiagramFor(t);
      getCD4C().addImport(clazz, getPackageName(def, DEFAULT_PACKAGE) + "." + t.getName());
    }

    String obs = domainClass.getName() + ObserverDecorator.SUFFIX + TSObserverMixinDecorator.SUFFIX;
    getCD4C().addImport(clazz, getPackageName(origCD, DEFAULT_PACKAGE) + "." + obs);
  }

}
