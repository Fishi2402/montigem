/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import umlp.generator.domain.decorator.DiagramObserverDecorator;
import umlp.generator.gui.gentypescript.decorator.TSImportHelper;
import umlp.generator.util.PathUtil;

import java.nio.file.Path;

public class TSDiagramObserverDecorator extends DiagramObserverDecorator {

  protected String astcdPackage;

  protected ASTCDCompilationUnit origCD;

  protected final Path outPath;
  protected final MCPath hwPath;

  public TSDiagramObserverDecorator(GlobalExtensionManagement glex, MCPath hwPath, Path outPath) {
    super(glex);
    this.hwPath = hwPath;
    this.outPath = outPath;
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE).getName();
    this.origCD = origCD;
    super.transform(origCD, decoratedCD);
  }

  protected ASTCDInterface createInterface(ASTCDCompilationUnit origCD) {
    ASTCDInterface clazz = super.createInterface(origCD);

    TSImportHelper importHelper = new TSImportHelper(glex, clazz, hwPath, outPath);
    String importerQName = this.astcdPackage + "." + clazz.getName();

    for (ASTCDClass c : this.origCD.getCDDefinition().getCDClassesList()) {
      importHelper.addImport(this.astcdPackage + "." + c.getName(), importerQName);
    }
    for (ASTCDEnum c : this.origCD.getCDDefinition().getCDEnumsList()) {
      importHelper.addImport(this.astcdPackage + "."
          + c.getName(), importerQName);
    }
    for (ASTCDInterface c : this.origCD.getCDDefinition().getCDInterfacesList()) {
      importHelper.addImport(this.astcdPackage + "."
          + c.getName(), importerQName);
    }

    return clazz;
  }

  @Override
  protected ASTCDClass createEmptyImpl(ASTCDCompilationUnit origCD, ASTCDInterface i) {
    ASTCDClass clazz = super.createEmptyImpl(origCD, i);
    glex.replaceTemplate("umlp.gui2ts.TSClassDeclaration", clazz, new TemplateHookPoint("umlp.gui2ts.TSClass2Mixin"));

    // TODO: something like this:
    // Methods with default impl
    getConcreteDomainClasses(origCD).forEach(c -> {
      getCD4C().addMethod(clazz, "umlp.diagram.MaybeNotifyAdd", c.getName());
      getCD4C().addMethod(clazz, "umlp.diagram.MaybeNotifyDelete", c.getName());
    });
    getCD4C().addMethod(clazz, "umlp.common_observer.GetScopeGlobal");

    TSImportHelper importHelper = new TSImportHelper(glex, clazz, hwPath, outPath);
    String importerQName = this.astcdPackage + "." + clazz.getName();

    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("NotificationScope"));
    importHelper.addImport(this.astcdPackage + "." + i.getName(), importerQName);

    for (ASTCDClass c : this.origCD.getCDDefinition().getCDClassesList()) {
      importHelper.addImport(this.astcdPackage + "." + c.getName(), importerQName);
    }
    for (ASTCDEnum c : this.origCD.getCDDefinition().getCDEnumsList()) {
      importHelper.addImport(this.astcdPackage + "."
          + c.getName(), importerQName);
    }
    for (ASTCDInterface c : this.origCD.getCDDefinition().getCDInterfacesList()) {
      importHelper.addImport(this.astcdPackage + "."
          + c.getName(), importerQName);
    }

    return clazz;
  }
}
