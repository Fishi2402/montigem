/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.CommandObserverDecorator;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class TSCommandObserverDecorator extends CommandObserverDecorator {

  public TSCommandObserverDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected ASTCDClass createEmptyClass(ASTCDClass domainClass, String classname) {
    ASTCDClass emptyClass = super.createEmptyClass(domainClass, classname);
    emptyClass.setCDExtendUsage(
        getExtendUsageFacade().createCDExtendUsage(domainClass.getName() + "ObserverMixin"));
    glex.replaceTemplate("umlp.gui2ts.TSClassExtends",
        emptyClass, new TemplateHookPoint("umlp.gui2ts.TSClassExtendsMixin"));
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

    String obs = domainClass.getName() + ObserverDecorator.SUFFIX;
    getCD4C().addImport(clazz, getPackageName(origCD, DEFAULT_PACKAGE) + "." + obs);

    List<String> pathParts = new ArrayList<>();
    if (!"".equals(DEFAULT_PACKAGE)) {
      pathParts.add(DEFAULT_PACKAGE);
    }
    for (String s : origCD.getCDPackageList()) {
      if (!"".equals(s)) {
        pathParts.add(s);
      }
    }
    pathParts.add(origCD.getCDDefinition().getName().toLowerCase());
    glex.addAfterTemplate("umlp.gui2ts.TSEmptyImports", clazz,
        new StringHookPoint(
            "import { " + obs + "Mixin" + " } from '"
            + "@src/" + String.join("/", pathParts) + "/" + obs
            + "';"
        ));
  }

}
