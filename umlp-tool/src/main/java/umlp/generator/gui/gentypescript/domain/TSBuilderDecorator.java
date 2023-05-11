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
    return "umlp.gui2ts.builder.Constructor";
  }

  protected String getBuildTemplate() {
    return "umlp.gui2ts.builder.Build";
  }

  protected String getIsBuildableTemplate() {
    return "umlp.gui2ts.builder.IsBuildable";
  }

  protected void createSetAndCheckMethods(ASTCDClass domainClass, ASTCDClass builderClass,
      List<ASTCDAttribute> attrList,
      List<ASTCDAssocSide> assocList) {
    for (ASTCDAttribute attribute : attrList) {
      getCD4C().addMethod(builderClass, "umlp.gui2ts.builder.Attr",
          attribute.getName(), getCDPrinter().prettyprint(createType(attribute)),
          getClassName(domainClass.getName()));
      getCD4C().addMethod(builderClass, "umlp.gui2ts.builder.Check",
          attribute.getName(), domainClass.getName(),
          !GenService.isList(attribute) && !GenService.isOptional(attribute));
    }
    for (ASTCDAssocSide assoc : assocList) {
      getCD4C().addMethod(builderClass, "umlp.gui2ts.builder.Check",
          assoc.getName(), domainClass.getName(),
          !GenService.isOptional(assoc));
      if (GenService.isList(assoc)) {
        getCD4C().addMethod(builderClass, "umlp.gui2ts.builder.AssocList",
            assoc.getName(), GenService.getType(assoc.getSymbol().getType()),
            getClassName(domainClass.getName()));
        getCD4C().addMethod(builderClass, "umlp.gui2ts.builder.AssocAdd",
            assoc.getName(), GenService.getType(assoc.getSymbol().getType()),
            getClassName(domainClass.getName()));
        getCD4C().addMethod(builderClass, "umlp.gui2ts.builder.AssocGemList",
            assoc.getName(),
            getClassName(domainClass.getName()));

      }
      else {
        getCD4C().addMethod(builderClass, "umlp.gui2ts.builder.AssocOne",
            assoc.getName(), GenService.getType(assoc.getSymbol().getType()),
            getClassName(domainClass.getName()));
        getCD4C().addMethod(builderClass, "umlp.gui2ts.builder.AssocOneGem",
            assoc.getName(), GenService.getType(assoc.getSymbol().getType()),
            getClassName(domainClass.getName()));
      }
    }
  }

}
