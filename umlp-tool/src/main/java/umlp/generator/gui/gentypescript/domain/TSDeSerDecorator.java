/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import com.google.common.collect.Iterables;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.methodtemplates.CD4CTemplateHelper;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import umlp.generator.domain.decorator.BaseClassObjectMapperDecorator;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.domain.decorator.DeSerDecorator;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.gui.gentypescript.decorator.TSImportHelper;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.nio.file.Path;
import java.util.Set;

public class TSDeSerDecorator extends DeSerDecorator {

  protected String astcdPackage;
  protected ASTCDCompilationUnit origCD;
  protected final Path outPath;

  public TSDeSerDecorator(GlobalExtensionManagement glex, MCPath hwPath, Path outPath,
      Set<String> supportedDesers) {
    super(glex, hwPath, supportedDesers);
    this.outPath = outPath;
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

    TSImportHelper importHelper = new TSImportHelper(glex, clazz, hwPath, outPath);
    String importerQName = this.astcdPackage + "." + clazz.getName();
    importHelper.addImport(this.astcdPackage + "." + getCorrectManager(domainType), importerQName);

    if (!domainType.getModifier().isAbstract()) {
      importHelper.addImport(this.astcdPackage + "." + domainType.getName(), importerQName);
      importHelper.addImport(this.astcdPackage + "."
          + domainType.getName() + BuilderDecorator.BUILDER_SUFFIX, importerQName);
    }
    String name = origCD.getCDDefinition().getName();
    importHelper.addImport(this.astcdPackage + "." + name + ManagerDecorator.SUFFIX, importerQName);
    importHelper.addImport(this.astcdPackage + "." + name + BaseClassObjectMapperDecorator.SUFFIX, importerQName);

    for (ASTCDClass c : this.origCD.getCDDefinition().getCDClassesList()) {
      if (!c.getModifier().isDerived()) {
        importHelper.addImport(this.astcdPackage + "." + c.getName(), importerQName);
        if (!(c.getName() + DeSerDecorator.SUFFIX).equals(clazz.getName())  && !c.getModifier().isAbstract()

        ) {
          importHelper.addImport(this.astcdPackage + "."
              + c.getName() + DeSerDecorator.SUFFIX, importerQName);
        }
      }
    }
    for (ASTCDEnum c : this.origCD.getCDDefinition().getCDEnumsList()) {
      importHelper.addImport(this.astcdPackage + "." + c.getName(), importerQName);
    }
    for (ASTCDInterface c : this.origCD.getCDDefinition().getCDInterfacesList()) {
      importHelper.addImport(this.astcdPackage + "." + c.getName(), importerQName);
    }

    for (ASTCDAssocSide assoc : CDSymbolTables.getAssociationsInHierarchy(domainType)) {
      ASTCDCompilationUnit cdOfAssoc = UMLPSymbolTables.getDiagramFor(assoc.getMCQualifiedType());
      if(!cdOfAssoc.equals(origCD)){
        importHelper.addImport(getPackageName(cdOfAssoc, DEFAULT_PACKAGE) + "." + assoc.getMCQualifiedType().printType(), importerQName);
      }
    }
  }
}
