package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.ValidationResultDecorator;
import umlp.generator.util.PathUtil;

public class TSValidationResultDecorator extends ValidationResultDecorator {
  protected String astcdPackage;
  protected ASTCDCompilationUnit origCD;
  public TSValidationResultDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE).getName();
    this.origCD = origCD;
    super.transform(origCD, decoratedCD);
  }

  protected String getListInitializer() {
    return "= []";
  }

  protected String getGetAssociationDomainValidationResultsTemplate() {
    return "umlp.gui2ts.validator.validationResult.TSGetAssociationDomainValidationResults";
  }
  protected String getAddAssociationDomainValidationResultTemplate() {
    return "umlp.gui2ts.validator.validationResult.TSAddAssociationDomainValidationResult";
  }

  protected String getConstructorTemplate() {
    return "umlp.gui2ts.validator.validationResult.TSConstructor";
  }
  protected String getIdConstructorTemplate() {
    return "umlp.gui2ts.validator.validationResult.TSIdConstructor";
  }
  protected String getHasErrorTemplate() {
    return "umlp.gui2ts.validator.validationResult.TSHasErrors";
  }
  protected String getGetDomainTypeTemplate() {
    return "umlp.gui2ts.validator.validationResult.TSGetDomainType";
  }
  protected String getAddValidationMessageTemplate() {
    return "umlp.gui2ts.validator.validationResult.TSAddValidationMessage";
  }
  protected String getGetValidationMessagesTemplate() {
    return "umlp.gui2ts.validator.validationResult.TSGetValidationMessages";
  }
  protected String getGetAssociationDomainValidationResultTemplate() {
    return "umlp.gui2ts.validator.validationResult.TSGetAssociationDomainValidationResult";
  }
  protected String getSetAssociationDomainValidationResultTemplate() {
    return "umlp.gui2ts.validator.validationResult.TSSetAssociationDomainValidationResult";
  }
  protected String getAttributeHasErrorTemplate() {
    return "umlp.gui2ts.validator.validationResult.TSAttributeHasError";
  }
  protected String getHasAssociationValidationResultTemplate() {
    return "umlp.gui2ts.validator.validationResult.TSHasAssociationDomainValidationResult";
  }
  protected String getMergeTemplate() {
    return "umlp.gui2ts.validator.validationResult.TSMerge";
  }
  protected String getShallowCloneTemplate() {
    return "umlp.gui2ts.validator.validationResult.TSShallowClone";
  }
  protected String getGetDomainObjectIdTemplate() {
    return "umlp.gui2ts.validator.validationResult.TSGetDomainObjectId";
  }

  @Override
  protected void addImports(ASTCDClass validatorClass) {
    getCD4C().addImport(validatorClass, PathUtil.getBackendRTECommonPath("ValidationMessage"));
    getCD4C().addImport(validatorClass, PathUtil.getBackendRTECommonPath("ValidationStatus"));
    getCD4C().addImport(validatorClass, PathUtil.getBackendRTECommonPath("DomainValidationResult"));

    for (ASTCDClass c : this.origCD.getCDDefinition().getCDClassesList()) {
      if(!(c.getName() + SUFFIX).equals(validatorClass.getName())) {
        getCD4C().addImport(validatorClass, this.astcdPackage + "."
                + c.getName() + SUFFIX);
      }
    }

  }

  @Override
  protected void annotateWithOverride(ASTCDMethodSignature method) {
  }
}
