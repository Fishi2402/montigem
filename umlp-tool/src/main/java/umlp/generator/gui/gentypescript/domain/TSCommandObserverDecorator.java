/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import umlp.generator.domain.decorator.CommandObserverDecorator;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.gui.gentypescript.decorator.TSImportHelper;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.nio.file.Path;

public class TSCommandObserverDecorator extends CommandObserverDecorator {

  protected final Path outPath;
  protected final MCPath hwPath;

  public TSCommandObserverDecorator(GlobalExtensionManagement glex, MCPath hwPath, Path outPath) {
    super(glex);
    this.hwPath = hwPath;
    this.outPath = outPath;
  }

  @Override
  protected ASTCDClass createEmptyClass(ASTCDClass domainClass, String classname) {
    ASTCDClass emptyClass = super.createEmptyClass(domainClass, classname);

    // replace interface via mixin
    glex.replaceTemplate("umlp.gui2ts.TSClassImplements",
        emptyClass, new TemplateHookPoint("umlp.gui2ts.TSClassExtendsMixin"));
    glex.addAfterTemplate("umlp.gui2ts.TSEmptyMixin",
        emptyClass, new StringHookPoint("Object"));

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

    String astcdPackage = getPackageName(origCD, DEFAULT_PACKAGE);
    TSImportHelper importHelper = new TSImportHelper(glex, clazz, hwPath, outPath);
    String importerQName = astcdPackage + "." + clazz.getName();

    for (ASTCDType t : UMLPSymbolTables.getAttributesInHierarchyWithDefiningType(domainClass).values()) {
      ASTCDCompilationUnit def = UMLPSymbolTables.getDiagramFor(t);
      importHelper.addImport(getPackageName(def, DEFAULT_PACKAGE) + "." + t.getName(), importerQName);
    }

    String obs = domainClass.getName() + ObserverDecorator.SUFFIX + TSObserverMixinDecorator.SUFFIX;
    importHelper.addImport(getPackageName(origCD, DEFAULT_PACKAGE) + "." + obs, importerQName);
  }

}
