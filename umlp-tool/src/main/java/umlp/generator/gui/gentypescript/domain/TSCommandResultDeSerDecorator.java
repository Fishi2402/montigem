/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.se_rwth.commons.Joiners;
import umlp.generator.domain.decorator.CommandResultDeSerDecorator;
import umlp.generator.domain.decorator.DeSerDecorator;
import umlp.generator.domain.decorator.ValidationResultDeSerDecorator;
import umlp.generator.domain.decorator.ValidationResultDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

public class TSCommandResultDeSerDecorator extends CommandResultDeSerDecorator {

  protected String astcdPackage;
  protected ASTCDCompilationUnit origCD;

  public TSCommandResultDeSerDecorator(GlobalExtensionManagement glex) {
    super(glex);
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

    for (ASTCDClass c : this.origCD.getCDDefinition().getCDClassesList()) {
      if (canSerialize(c)) {
        getCD4C().addImport(clazz, this.astcdPackage + "." + c.getName());
        getCD4C().addImport(clazz, this.astcdPackage + "."
            + c.getName() + DeSerDecorator.SUFFIX);
        if(hasOCL) {
          getCD4C().addImport(clazz, this.astcdPackage + "."
                  + c.getName() + ValidationResultDecorator.SUFFIX);
          getCD4C().addImport(clazz, this.astcdPackage + "."
                  + c.getName() + ValidationResultDeSerDecorator.SUFFIX);
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
          getCD4C().addImport(clazz, Joiners.DOT.join(curPackName, astcdClass.getName()));
          getCD4C().addImport(clazz, Joiners.DOT.join(curPackName, astcdClass.getName() + DeSerDecorator.SUFFIX));
        }
      }
    }
  }
}
