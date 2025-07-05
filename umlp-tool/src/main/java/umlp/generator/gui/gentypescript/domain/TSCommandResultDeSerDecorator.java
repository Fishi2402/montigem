/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.se_rwth.commons.Joiners;
import umlp.generator.domain.decorator.CommandResultDeSerDecorator;
import umlp.generator.domain.decorator.DeSerDecorator;
import umlp.generator.domain.decorator.ValidationResultDeSerDecorator;
import umlp.generator.domain.decorator.ValidationResultDecorator;
import umlp.generator.gui.gentypescript.decorator.TSImportHelper;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.nio.file.Path;

public class TSCommandResultDeSerDecorator extends CommandResultDeSerDecorator {

  protected String astcdPackage;
  protected ASTCDCompilationUnit origCD;

  protected final Path outPath;
  protected final MCPath hwPath;

  public TSCommandResultDeSerDecorator(GlobalExtensionManagement glex, MCPath hwPath, Path outPath) {
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

  /*
  @Override
  protected List<String> getClassNames(ASTCDCompilationUnit origCD) {
    List<String> classNames = new ArrayList<>();
    for (ASTCDClass c :  origCD.getCDDefinition().getCDClassesList()) {
      if (canSerialize(c)) {
        classNames.add(c.getName());
      }
    }
    return classNames;
  }
  /**/

  @Override
  protected String getSerSingleValueTemplate() {
    return "umlp.gui2ts.commandresultdeser.TSSerSingleValue";
  }

  @Override
  protected String getDeserSingleValueTemplate() {
    return "umlp.gui2ts.commandresultdeser.TSDeserSingleValue";
  }

  @Override
  protected String getFromJsonTemplate() {
    return "umlp.gui2ts.commandresultdeser.TSFromJson";
  }

  @Override
  protected String getBuildJsonTemplate() {
    return "umlp.gui2ts.commandresultdeser.TSBuildJson";
  }

  @Override
  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("BaseClassObjectMapper"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("LoadingState"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandResultDeSer"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("ValidationResult"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandResult"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("SimpleResult"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("ListResult"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("DomainValidationResult"));

    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("BaseClassObjectMapper"));

    TSImportHelper importHelper = new TSImportHelper(glex, clazz, hwPath, outPath);
    String importerQName = this.astcdPackage + "." + clazz.getName();

    for (ASTCDClass c : this.origCD.getCDDefinition().getCDClassesList()) {
      if (canSerialize(c)) {
        importHelper.addImport(this.astcdPackage + "." + c.getName(), importerQName);
        importHelper.addImport(this.astcdPackage + "."
            + c.getName() + DeSerDecorator.SUFFIX, importerQName);
        if(hasOCL) {
          importHelper.addImport(this.astcdPackage + "."
                  + c.getName() + ValidationResultDecorator.SUFFIX, importerQName);
          importHelper.addImport(this.astcdPackage + "."
                  + c.getName() + ValidationResultDeSerDecorator.SUFFIX, importerQName);
        }
      }
    }

    // extended CDs
    DiagramSymbol originalDiagram = origCD.getCDDefinition().getSymbol();
    var extensionHierarchy = UMLPSymbolTables.getExtensionDiamond(originalDiagram).get(originalDiagram);
    for (DiagramSymbol diagramSymbol : extensionHierarchy) {
      String curPackName = getPackageName(diagramSymbol, DEFAULT_PACKAGE);
      ASTCDDefinition def = ((ASTCDDefinition)diagramSymbol.getAstNode());
      for (ASTCDClass astcdClass : def.getCDClassesList()) {
        if(canSerialize(astcdClass)) {
          importHelper.addImport(Joiners.DOT.join(curPackName, astcdClass.getName()), importerQName);
          importHelper.addImport(Joiners.DOT.join(curPackName, astcdClass.getName() + DeSerDecorator.SUFFIX), importerQName);
        }
      }
    }
  }
}
