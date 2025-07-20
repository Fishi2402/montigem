/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.se_rwth.commons.Joiners;
import umlp.generator.domain.decorator.CommandObjectMapperDecorator;
import umlp.generator.domain.decorator.CommandResultDeSerDecorator;
import umlp.generator.domain.decorator.commands.AggregatedCommandDeSerDecorator;
import umlp.generator.gui.gentypescript.decorator.TSImportHelper;
import umlp.generator.util.PathUtil;

import java.nio.file.Path;

public class TSCommandObjectMapperDecorator extends CommandObjectMapperDecorator {

  protected String astcdPackage;
  protected ASTCDCompilationUnit origCD;

  protected final Path outPath;
  protected final MCPath hwPath;

  public TSCommandObjectMapperDecorator(GlobalExtensionManagement glex, MCPath hwPath, Path outPath) {
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

  @Override
  protected String getInitTemplate() {
    return "umlp.gui2ts.commandobjectmapper.TSInit";
  }

  @Override
  protected String getSerializeCommandTemplate() {
    return "umlp.gui2ts.commandobjectmapper.TSSerializeCommand";
  }

  @Override
  protected String getDeserializeCommandTemplate() {
    return "umlp.gui2ts.commandobjectmapper.TSDeserializeCommand";
  }

  @Override
  protected String getSerializeResponseTemplate() {
    return "umlp.gui2ts.commandobjectmapper.TSSerializeResponse";
  }

  @Override
  protected String getDeserializeResponseTemplate() {
    return "umlp.gui2ts.commandobjectmapper.TSDeserializeResponse";
  }

  @Override
  protected void addImportsDeSer(
      ASTCDType mapperClass, DiagramSymbol superDiagram,
      String commandDeSerName, String commandResultDeSerName
  ) {
    TSImportHelper importHelper = new TSImportHelper(glex, mapperClass, hwPath, outPath);
    String importerQName = this.astcdPackage + "." + mapperClass.getName();

    String packageName = getPackageName(superDiagram, DEFAULT_PACKAGE);
    importHelper.addImport(Joiners.DOT.join(packageName, commandDeSerName), importerQName);
    importHelper.addImport(Joiners.DOT.join(packageName, commandResultDeSerName), importerQName);
  }

  @Override
  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandObjectMapper"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandResult"));

    String name = origCD.getCDDefinition().getName();
    TSImportHelper importHelper = new TSImportHelper(glex, clazz, hwPath, outPath);
    String importerQName = this.astcdPackage + "." + clazz.getName();

    importHelper.addImport(this.astcdPackage + "."
        + name + CommandResultDeSerDecorator.SUFFIX, importerQName);
    importHelper.addImport(this.astcdPackage + "."
        + name + AggregatedCommandDeSerDecorator.SUFFIX, importerQName);
  }


}
