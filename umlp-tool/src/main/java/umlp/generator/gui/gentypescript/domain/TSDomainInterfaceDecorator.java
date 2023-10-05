/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cd.codegen.methods.MutatorDecorator;
import de.monticore.cd.codegen.methods.mutator.ListMutatorDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.DomainInterfaceDecorator;
import umlp.generator.domain.decorator.methods.MethodGenService;
import umlp.generator.domain.decorator.methods.UMLPListMutatorDecorator;
import umlp.generator.domain.decorator.methods.UMLPMutatorDecorator;
import umlp.generator.gui.gentypescript.method.TSAccessorDecorator;
import umlp.generator.gui.gentypescript.method.TSAssocAccessorDecorator;
import umlp.generator.gui.gentypescript.method.TSAssocMutatorDecorator;
import umlp.generator.gui.gentypescript.method.TSOptionalMutatorDecorator;
import umlp.generator.gui.gentypescript.method.TSSimpleMethodMutatorDecorator;
import umlp.generator.util.PathUtil;

import java.util.List;

public class TSDomainInterfaceDecorator extends DomainInterfaceDecorator {

  public TSDomainInterfaceDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("GemIdentifiable"));
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("Gem"));
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("GemList"));
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("GemBuildable"));
  }

  @Override
  protected void createGetterSetterForAttrs(ASTCDType domainType, ASTCDType cdType,
      List<ASTCDAttribute> attrList, boolean isInterface) {
    CDGenService service = new MethodGenService();
    MethodDecorator methodDecorator = new MethodDecorator(glex,
        new TSAccessorDecorator(glex, service),
        new UMLPMutatorDecorator(glex, new TSSimpleMethodMutatorDecorator(glex, service, domainType),
            new TSOptionalMutatorDecorator(glex, service),
            new UMLPListMutatorDecorator(glex, service, domainType)), service);
    for (ASTCDAttribute attribute : attrList) {
      List<ASTCDMethod> methods = methodDecorator.decorate(attribute);
      methods.forEach(m -> m.getModifier().setAbstract(isInterface));
      cdType.addAllCDMembers(methods);
      getCD4C().addMethod(cdType, getSetPresentTemplate(), service.getNativeAttributeName(attribute.getName()));
    }
  }

  @Override
  protected String getSetPresentTemplate() {
    return "umlp.gui2ts.domain.TSSetPresent";
  }

  @Override
  protected void createGetterSetterForAssocs(ASTCDType cdType,
      List<ASTCDAttribute> attrList, boolean isInterface) {
    CDGenService service = new MethodGenService();
    MethodDecorator methodDecorator = new MethodDecorator(glex,
        new TSAssocAccessorDecorator(glex, service, cdType),
        new TSAssocMutatorDecorator(glex, service, cdType), service);
    for (ASTCDAttribute attribute : attrList) {
      List<ASTCDMethod> methods = methodDecorator.decorate(attribute);
      methods.forEach(m -> m.getModifier().setAbstract(isInterface));
      cdType.addAllCDMembers(methods);
    }
  }

}
