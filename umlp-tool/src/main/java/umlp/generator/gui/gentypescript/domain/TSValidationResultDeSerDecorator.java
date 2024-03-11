package umlp.generator.gui.gentypescript.domain;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.ValidationResultDeSerDecorator;
import umlp.generator.domain.decorator.ValidationResultDecorator;
import umlp.generator.util.PathUtil;

public class TSValidationResultDeSerDecorator extends ValidationResultDeSerDecorator {
  protected String astcdPackage;
  protected ASTCDCompilationUnit origCD;
  public TSValidationResultDeSerDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE).getName();
    this.origCD = origCD;
    super.transform(origCD, decoratedCD);
  }

  protected String getSerializeTemplate() {
    return "umlp.gui2ts.validator.deser.TSSerialize";
  }

  protected String getDeserializeTemplate() {
    return "umlp.gui2ts.validator.deser.TSDeserialize";
  }

  protected String getBuildJsonTemplate() {
    return "umlp.gui2ts.validator.deser.TSBuildJson";
  }

  protected String getFromJsonTemplate() {
    return "umlp.gui2ts.validator.deser.TSFromJson";
  }

  @Override
  protected void addImports(ASTCDType domainType, ASTCDType clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("ValidationMessage"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("ValidationMessageDeSer"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("ValidationStatus"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("Gem"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("JsonDeSer"));

    for (ASTCDClass c : this.origCD.getCDDefinition().getCDClassesList()) {
      getCD4C().addImport(clazz, this.astcdPackage + "."
              + c.getName() + ValidationResultDecorator.SUFFIX);
    }

    for (ASTCDClass c : this.origCD.getCDDefinition().getCDClassesList()) {
      if(!c.getName().equals(domainType.getName())) {
        getCD4C().addImport(clazz, this.astcdPackage + "."
                + c.getName() + SUFFIX);
      }
    }

  }

}
