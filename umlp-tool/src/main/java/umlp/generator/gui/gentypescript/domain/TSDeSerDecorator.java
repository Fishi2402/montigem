/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd.methodtemplates.CD4CTemplateHelper;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import umlp.generator.domain.decorator.BaseClassObjectMapperDecorator;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.domain.decorator.DeSerDecorator;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.util.PathUtil;

import java.util.Set;

public class TSDeSerDecorator extends DeSerDecorator {

  protected String astcdPackage;
  protected ASTCDCompilationUnit origCD;

  public TSDeSerDecorator(GlobalExtensionManagement glex, MCPath hwPath,
      Set<String> supportedDesers) {
    super(glex, hwPath, supportedDesers);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE).getName();
    this.origCD = origCD;
    super.transform(origCD, decoratedCD);
  }

  @Override
  protected void addDeSerAddons(ASTCDClass deSer, ASTCDClass cdClass) {
    CD4CTemplateHelper th = new CD4CTemplateHelper();
    th.method("protected void serialize_Addons(" + cdClass.getName() + " obj, any jsonPrinter)");
    deSer.addCDMember(th.getMethod().get());
    th.method("protected void deserialize_Addons(" + cdClass.getName() + "Builder builder, any json)");
    deSer.addCDMember(th.getMethod().get());
  }

  @Override
  protected String getSerializeTemplate() {
    return "umlp.gui2ts.deser.TSserialize";
  }

  @Override
  protected String getDeserializeTemplate() {
    return "umlp.gui2ts.deser.TSdeserialize";
  }

  @Override
  protected String getBuildJsonTemplate() {
    return "umlp.gui2ts.deser.TSBuildJson";
  }

  @Override
  protected String getFromJsonTemplate() {
    return "umlp.gui2ts.deser.TSFromJson";
  }

  @Override
  protected String getSerializeSimpleFieldTemplate() {
    return "umlp.gui2ts.deser.TSserializeSimpleField";
  }

  @Override
  protected String getDeserializeSimpleFieldTemplate() {
    return "umlp.gui2ts.deser.TSdeserializeSimpleField";
  }

  @Override
  protected String getSerializeGemListFieldTemplate() {
    return "umlp.gui2ts.deser.TSserializeGemListField";
  }

  @Override
  protected String getDeserializeGemListFieldTemplate() {
    return "umlp.gui2ts.deser.TSdeserializeGemListField";
  }

  @Override
  protected String getSerializeGemFieldTemplate() {
    return "umlp.gui2ts.deser.TSserializeGemField";
  }

  @Override
  protected String getDeserializeGemFieldTemplate() {
    return "umlp.gui2ts.deser.TSdeserializeGemField";
  }

  @Override
  protected void addImports(ASTCDType domainType, ASTCDType clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("Gem"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("JsonDeSer"));
    getCD4C().addImport(clazz, this.astcdPackage + "." + domainType.getName());
    getCD4C().addImport(clazz, this.astcdPackage + "."
        + domainType.getName() + BuilderDecorator.BUILDER_SUFFIX);
    String name = origCD.getCDDefinition().getName();
    getCD4C().addImport(clazz, this.astcdPackage + "." + name + ManagerDecorator.SUFFIX);
    getCD4C().addImport(clazz, this.astcdPackage + "." + name + BaseClassObjectMapperDecorator.SUFFIX);

    for (ASTCDClass c : this.origCD.getCDDefinition().getCDClassesList()) {
      if (!c.getModifier().isDerived()) {
        getCD4C().addImport(clazz, this.astcdPackage + "." + c.getName());
        if (!(c.getName() + DeSerDecorator.SUFFIX).equals(clazz.getName())) {
          getCD4C().addImport(clazz, this.astcdPackage + "."
              + c.getName() + DeSerDecorator.SUFFIX);
        }
      }
    }
  }
}
