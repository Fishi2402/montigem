/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.domain.decorator.DerivedClassClientImplDecorator;
import umlp.generator.domain.decorator.commands.DerivedClassCommandDecorator;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

public class TSDerivedClassClientImplDecorator extends DerivedClassClientImplDecorator {

  protected String astcdPackage;
  protected ASTCDCompilationUnit origCD;

  public TSDerivedClassClientImplDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE).getName();
    this.origCD = origCD;
    super.transform(origCD, decoratedCD);
  }

  @Override
  protected void addConstructor(ASTCDClass ast, String classname) {
    glex.replaceTemplate(GUI2ComponentTemplates.CONSTRUCTOR, ast,
        new TemplateHookPoint(getConstructorTemplate()));
  }

  @Override
  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
    getCD4C().addImport(cdType, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), domainType.getName()));
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommandPath("CommandManager"));

    if (!domainType.getSuperclassList().isEmpty()) {
      ASTCDCompilationUnit cdOfSuper = UMLPSymbolTables.getDiagramFor(domainType.getSuperclassList().get(0));
      getCD4C().addImport(cdType, getPackageName(cdOfSuper, DEFAULT_PACKAGE) + "." + domainType.getSuperclassList().get(0).printType());
    }
    for (ASTCDClass c : this.origCD.getCDDefinition().getCDClassesList()) {
      if (!c.getName().equals(domainType.getName())) {
        getCD4C().addImport(cdType, this.astcdPackage + "." + c.getName());
      }
    }
    for (ASTCDEnum c : this.origCD.getCDDefinition().getCDEnumsList()) {
      if (!c.getName().equals(domainType.getName())) {
        getCD4C().addImport(cdType, this.astcdPackage + "." + c.getName());
      }
    }
    for (ASTCDInterface c : this.origCD.getCDDefinition().getCDInterfacesList()) {
      if (!c.getName().equals(domainType.getName())) {
        ASTCDCompilationUnit cdOfSuper = UMLPSymbolTables.getDiagramFor(c);
        getCD4C().addImport(cdType, getPackageName(cdOfSuper, DEFAULT_PACKAGE) + "." + c.getName());
      }
    }
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.derivedclass.TSConstructor";
  }

  protected String getGetTemplate() {
    return "umlp.gui2ts.derivedclass.TSGetClientImpl";
  }

  protected String getSizeTemplate() {
    return "umlp.gui2ts.derivedclass.TSGetSizeImpl";
  }

  protected String getCalcTemplate() {
    return "umlp.gui2ts.derivedclass.TSCalcClientImpl";
  }

  protected String getCalcSizeTemplate() {
    return "umlp.gui2ts.derivedclass.TSCalcSizeImpl";
  }

  @Override
  protected String getInitTemplate() {
    return "umlp.gui2ts.derivedclass.TSInit";
  }

  @Override
  protected String toList(String type) {
    return "Array<" + type + ">";
  }

  @Override
  protected void handleAttribute(ASTCDClass domainClass, String name, String type, ASTCDClass ast, String packageName, boolean isList) {
    super.handleAttribute(domainClass, name, type, ast, packageName, isList);
    // add an attribute, which contains the value (the value is fetched in the init)

    String attrType = type;
    if (attrType.equals("Array<String>")) {
      attrType = "Array<string>";
    }

    getCD4C().addAttribute(ast, "protected " + attrType + " " + name);

    getCD4C().addImport(ast, Joiners.DOT.join(getPackageName(origCD, DerivedClassCommandDecorator.COMMAND_DERIVED_CLASS_PACKAGE),
        StringTransformations.capitalize(domainClass.getName()) + "_" + StringTransformations.capitalize(name) + "_get"));


  }
}
