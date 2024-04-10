/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.domain.decorator.ErrorCollectorDecorator;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static umlp.generator.domain.decorator.GenService.createType;

public class TSBuilderDecorator extends BuilderDecorator {

  protected String astcdPackage;
  protected ASTCDCompilationUnit origCD;

  public TSBuilderDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE).getName();
    this.origCD = origCD;
    super.transform(origCD, decoratedCD);
  }

  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("Gem"));
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(cdType, this.astcdPackage + "." + domainType.getName());
    getCD4C().addImport(cdType, this.astcdPackage + "." + domainType.getName() + ErrorCollectorDecorator.SUFFIX);

    String name = origCD.getCDDefinition().getName();
    getCD4C().addImport(cdType, this.astcdPackage + "."
        + name + ManagerDecorator.SUFFIX);
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

    for (ASTCDAssocSide assoc : CDSymbolTables.getAssociationsInHierarchy(domainType)) {
      ASTCDCompilationUnit cdOfAssoc = UMLPSymbolTables.getDiagramFor(assoc.getMCQualifiedType());
      if (!cdOfAssoc.equals(origCD)) {
        getCD4C().addImport(cdType, getPackageName(cdOfAssoc, DEFAULT_PACKAGE) + "." + assoc.getMCQualifiedType().printType());
      }
    }
  }

  @Override
  protected void addConstructor(
      ASTCDClass builderClass, ASTCDClass domainClass,
      String managerName, List<ASTCDAssocSide> attrAssocLists, List<String[]> initialAttributes
  ) {
    Map<ASTCDAssocSide, String> correctManagerToAttrAssocSides = new LinkedHashMap<>();
    attrAssocLists.forEach(assocSide -> correctManagerToAttrAssocSides.put(assocSide,
        getCorrectManager(assocSide.getSymbol().getAssoc().getAssociation().getAstNode())));
    glex.replaceTemplate(GUI2ComponentTemplates.CONSTRUCTOR, builderClass,
        new TemplateHookPoint(getConstructorTemplate(), domainClass.getName(), correctManagerToAttrAssocSides, initialAttributes));
  }

  protected String getConstructorTemplate() {
    return "umlp.gui2ts.builder.TSConstructor";
  }

  protected String getBuildTemplate() {
    return "umlp.gui2ts.builder.TSBuild";
  }

  protected String getIsBuildableTemplate() {
    return "umlp.gui2ts.builder.TSIsBuildable";
  }

  protected void createSetAndCheckMethods(ASTCDClass domainClass, ASTCDClass builderClass,
                                          List<ASTCDAttribute> attrList,
                                          List<ASTCDAssocSide> assocList) {
    for (ASTCDAttribute attribute : attrList) {
      getCD4C().addMethod(builderClass, "umlp.gui2ts.builder.TSAttr",
          attribute.getName(), getCDPrinter().prettyprint(createType(attribute)),
          getClassName(domainClass.getName()));
      getCD4C().addMethod(builderClass, "umlp.gui2ts.builder.TSCheck",
          attribute.getName(), domainClass.getName(),
          !GenService.isList(attribute) && !GenService.isOptional(attribute));
    }
    for (ASTCDAssocSide assoc : assocList) {
      getCD4C().addMethod(builderClass, "umlp.gui2ts.builder.TSCheck",
          assoc.getName(), domainClass.getName(),
          !GenService.isOptional(assoc));
      if (GenService.isList(assoc)) {
        getCD4C().addMethod(builderClass, "umlp.gui2ts.builder.TSAssocList",
            assoc.getName(), GenService.getType(assoc.getSymbol().getType()),
            getClassName(domainClass.getName()));
        getCD4C().addMethod(builderClass, "umlp.gui2ts.builder.TSAssocAdd",
            assoc.getName(), GenService.getType(assoc.getSymbol().getType()),
            getClassName(domainClass.getName()));
        getCD4C().addMethod(builderClass, "umlp.gui2ts.builder.TSAssocGemList",
            assoc.getName(),
            getClassName(domainClass.getName()));

      } else {
        getCD4C().addMethod(builderClass, "umlp.gui2ts.builder.TSAssocOne",
            assoc.getName(), GenService.getType(assoc.getSymbol().getType()),
            getClassName(domainClass.getName()));
        getCD4C().addMethod(builderClass, "umlp.gui2ts.builder.TSAssocOneGem",
            assoc.getName(), GenService.getType(assoc.getSymbol().getType()),
            getClassName(domainClass.getName()));
      }
    }
  }

}
