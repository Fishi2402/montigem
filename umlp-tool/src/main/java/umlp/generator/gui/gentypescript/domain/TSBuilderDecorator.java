/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.util.PathUtil;

import java.util.List;

import static umlp.generator.domain.decorator.GenService.createType;

public class TSBuilderDecorator extends BuilderDecorator {

  public TSBuilderDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  protected void addImports(ASTCDType domainType, ASTCDType clazz) {
    super.addImports(domainType, clazz);
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("Gem"));
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

      }
      else {
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
