/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cd.codegen.methods.MutatorDecorator;
import de.monticore.cd.codegen.methods.mutator.ListMutatorDecorator;
import de.monticore.cd.codegen.methods.mutator.OptionalMutatorDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import umlp.generator.domain.decorator.DomainClassDecorator;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.methods.MethodGenService;
import umlp.generator.gui.gentypescript.method.TSAssocAccessorDecorator;
import umlp.generator.gui.gentypescript.method.TSAssocMutatorDecorator;
import umlp.generator.gui.gentypescript.method.TSSimpleMethodMutatorDecorator;
import umlp.generator.util.PathUtil;

import java.util.List;
import java.util.stream.Collectors;

public class TSDomainClassDecorator extends DomainClassDecorator {

  public TSDomainClassDecorator(final GlobalExtensionManagement glex, MCPath hwPath) {
    super(glex, hwPath);
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
        new AccessorDecorator(glex, service),
        new MutatorDecorator(glex, new TSSimpleMethodMutatorDecorator(glex, service, domainType),
            new OptionalMutatorDecorator(glex, service),
            new ListMutatorDecorator(glex, service)), service);
    for (ASTCDAttribute attribute : attrList) {
      List<ASTCDMethod> methods = methodDecorator.decorate(attribute);
      methods.forEach(m -> m.getModifier().setAbstract(isInterface));
      cdType.addAllCDMembers(methods);
    }
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

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.domain.TSConstructor";
  }

  @Override
  protected String getBuildTemplate() {
    return "umlp.gui2ts.domain.TSBuild";
  }

  @Override
  protected void addTransientModifier(ASTCDAttribute attrObserver) {
    // TODO AGe: toJSON instead?
  }

  @Override
  protected String defaultListValue() {
    return "= []";
  }

  @Override
  protected void createCheckMethods(ASTCDClass domainClass, ASTCDClass clazz) {
    for (ASTCDAttribute attr : CDSymbolTables.getAttributesInHierarchy(domainClass)
        .stream().filter(GenService::isVisibleNotDerived).collect(Collectors.toList())) {
      getCD4C().addMethod(clazz, "umlp.gui2ts.domain.TSCheck",
          domainClass.getName(), attr.getName(),
          !GenService.isList(attr) && !GenService.isOptional(attr));
    }
    for (ASTCDAssocSide assoc : CDSymbolTables.getAssociationsInHierarchy(domainClass)) {
      getCD4C().addMethod(clazz, "umlp.gui2ts.domain.TSCheck",
          domainClass.getName(), assoc.getName(),
          !GenService.isOptional(assoc));
    }
  }

  @Override
  protected void createObserverMethods(ASTCDClass domainClass, ASTCDClass clazz) {
    getCD4C().addMethod(clazz, "umlp.gui2ts.domain.TSAddObserver", domainClass.getName(), clazz.isPresentCDExtendUsage());
    getCD4C().addMethod(clazz, "umlp.gui2ts.domain.TSRemoveObserver", domainClass.getName());
    getCD4C().addMethod(clazz, "umlp.gui2ts.domain.TSContainsObserver", domainClass.getName());
  }

  @Override
  protected String getEqualsTemplate() {
    return "umlp.gui2ts.domain.TSEquals";
  }

}
