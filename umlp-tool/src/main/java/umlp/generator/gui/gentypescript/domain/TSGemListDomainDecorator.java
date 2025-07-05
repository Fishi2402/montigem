/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Joiners;
import umlp.generator.domain.decorator.GemListDomainDecorator;
import umlp.generator.gui.gentypescript.decorator.TSImportHelper;
import umlp.generator.util.PathUtil;

import java.nio.file.Path;

import static umlp.generator.domain.decorator.ManagerDecorator.SUFFIX;

public class TSGemListDomainDecorator extends GemListDomainDecorator {

  protected final Path outPath;
  protected final MCPath hwPath;

  public TSGemListDomainDecorator(GlobalExtensionManagement glex, MCPath hwPath, Path outPath) {
    super(glex);
    this.hwPath = hwPath;
    this.outPath = outPath;
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.gemlist.assocs.TSConstructor";
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

  @Override
  protected void addImports(ASTCDCompilationUnit origCD, String domainName, ASTCDClass ast) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("GemListBuilder"));
    getCD4C().addImport(ast, "umlp.backendrte.common.LoadResult");
    getCD4C().addImport(ast, "umlp.backendrte.common.LoadingState");

    String astcdPackage = getPackageName(origCD, GEMLIST_PACKAGE);
    TSImportHelper importHelper = new TSImportHelper(glex, ast, hwPath, outPath);
    String importerQName = astcdPackage + "." + ast.getName();

    importHelper.addImport(Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), domainName), importerQName);
    importHelper.addImport(Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), origCD.getCDDefinition().getName()+SUFFIX), importerQName);
  }
}
