/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.io.paths.MCPath;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.domain.decorator.DeSerBuilderDecorator;
import umlp.generator.domain.decorator.ErrorCollectorDecorator;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.util.PathUtil;

public class TSDeSerBuilderDecorator extends DeSerBuilderDecorator {

  protected String astcdPackage;
  protected ASTCDCompilationUnit origCD;

  public TSDeSerBuilderDecorator(final GlobalExtensionManagement glex, MCPath hwPath) {
    super(glex, hwPath);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE).getName();
    this.origCD = origCD;
    super.transform(origCD, decoratedCD);
  }

  @Override
  protected void addOverride(ASTCDMethodSignature m) {
    // don't
  }

  protected String getConstructorTemplate() {
    return "umlp.gui2ts.deser_builder.TSConstructor";
  }

  protected String getsetSimpleTemplate() {
    return "umlp.gui2ts.deser_builder.TSsetSimple";
  }

  protected String getsetGemTemplate() {
    return "umlp.gui2ts.deser_builder.TSsetGem";
  }

  protected String getsetGemListIdsTemplate() {
    return "umlp.gui2ts.deser_builder.TSsetGemListIds";
  }

  @Override
  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("Gem"));
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("GemList"));
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("GemBuildable"));
    getCD4C().addImport(cdType, this.astcdPackage + "." + domainType.getName() + BuilderDecorator.BUILDER_SUFFIX);
    if (!domainType.getSuperclassList().isEmpty()) {
      getCD4C().addImport(cdType, this.astcdPackage + "." + domainType.getSuperclassList().get(0).printType());
    }
    for (ASTCDClass c : this.origCD.getCDDefinition().getCDClassesList()) {
      if (c.getName() != domainType.getName()) {
        getCD4C().addImport(cdType, this.astcdPackage + "." + c.getName());
        if (!c.getModifier().isDerived()) {
          getCD4C().addImport(cdType, this.astcdPackage + "."
              + c.getName() + BuilderDecorator.BUILDER_SUFFIX);
          getCD4C().addImport(cdType, this.astcdPackage + "."
              + c.getName() + DeSerBuilderDecorator.DESER_BUILDER_SUFFIX);
        }
      }
    }
    for (ASTCDEnum c : this.origCD.getCDDefinition().getCDEnumsList()) {
      if (c.getName() != domainType.getName()) {
        getCD4C().addImport(cdType, this.astcdPackage + "." + c.getName());
      }
    }
    for (ASTCDInterface c : this.origCD.getCDDefinition().getCDInterfacesList()) {
      if (c.getName() != domainType.getName()) {
        getCD4C().addImport(cdType, this.astcdPackage + "." + c.getName());
      }
    }
  }

}
