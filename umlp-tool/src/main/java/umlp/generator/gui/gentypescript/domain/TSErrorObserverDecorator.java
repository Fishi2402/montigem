/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import umlp.generator.domain.decorator.ErrorObserverDecorator;
import umlp.generator.gui.gentypescript.decorator.TSImportHelper;

import java.nio.file.Path;

public class TSErrorObserverDecorator extends ErrorObserverDecorator {

  protected String astcdPackage;

  protected final Path outPath;
  protected final MCPath hwPath;

  public TSErrorObserverDecorator(final GlobalExtensionManagement glex, MCPath hwPath, Path outPath) {
    super(glex);
    this.hwPath = hwPath;
    this.outPath = outPath;
  }

  @Override
  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
    TSImportHelper importHelper = new TSImportHelper(glex, cdType, hwPath, outPath);
    String importerQName = this.astcdPackage + "." + cdType.getName();

    importHelper.addImport(this.astcdPackage + "." + domainType.getName(), importerQName);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE).getName();
    super.transform(origCD, decoratedCD);
  }

  @Override
  protected String getNotifyInvalidTemplate() {
    return "umlp.gui2ts.errorobserver.TSNotifyInvalid";
  }
}
