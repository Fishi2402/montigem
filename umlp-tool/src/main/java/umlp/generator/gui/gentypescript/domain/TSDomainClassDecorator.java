/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cd.codegen.methods.MutatorDecorator;
import de.monticore.cd.codegen.methods.mutator.ListMutatorDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import umlp.generator.domain.decorator.DomainClassDecorator;
import umlp.generator.domain.decorator.ErrorCollectorDecorator;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.domain.decorator.methods.MethodGenService;
import umlp.generator.gui.gentypescript.method.TSAccessorDecorator;
import umlp.generator.gui.gentypescript.method.TSAssocAccessorDecorator;
import umlp.generator.gui.gentypescript.method.TSAssocMutatorDecorator;
import umlp.generator.gui.gentypescript.method.TSOptionalMutatorDecorator;
import umlp.generator.gui.gentypescript.method.TSSimpleMethodMutatorDecorator;
import umlp.generator.util.PathUtil;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static umlp.generator.domain.decorator.GenService.createType;

public class TSDomainClassDecorator extends DomainClassDecorator {

  protected String astcdPackage;
  protected ASTCDCompilationUnit origCD;

  public TSDomainClassDecorator(final GlobalExtensionManagement glex, MCPath hwPath) {
    super(glex, hwPath);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE).getName();
    this.origCD = origCD;
    super.transform(origCD, decoratedCD);
  }

  @Override
  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("GemIdentifiable"));
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("Gem"));
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("GemList"));
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("GemBuildable"));
    getCD4C().addImport(cdType, this.astcdPackage + "." + domainType.getName() + ObserverDecorator.SUFFIX);
    getCD4C().addImport(cdType, this.astcdPackage + "." + domainType.getName() + ErrorCollectorDecorator.SUFFIX);
    if (!domainType.getSuperclassList().isEmpty()) {
      getCD4C().addImport(cdType, this.astcdPackage + "." + domainType.getSuperclassList().get(0).printType());
    }
    for (ASTCDClass c : this.origCD.getCDDefinition().getCDClassesList()) {
      if (c.getName() != domainType.getName()) {
        getCD4C().addImport(cdType, this.astcdPackage + "." + c.getName());
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

  @Override
  protected void createGetterSetterForAttrs(ASTCDType domainType, ASTCDType cdType,
      List<ASTCDAttribute> attrList, boolean isInterface) {
    CDGenService service = new MethodGenService();
    MethodDecorator methodDecorator = new MethodDecorator(glex,
        new TSAccessorDecorator(glex, service),
        new MutatorDecorator(glex, new TSSimpleMethodMutatorDecorator(glex, service, domainType),
            new TSOptionalMutatorDecorator(glex, service),
            new ListMutatorDecorator(glex, service)), service);
    for (ASTCDAttribute attribute : attrList) {
      List<ASTCDMethod> methods = methodDecorator.decorate(attribute);
      // methods.forEach(m -> m.getModifier().setAbstract(isInterface));
      methods.forEach(m -> m.getModifier().setDerived(isInterface));
      // TODO AGe: why is it getting set to abstract?
      methods.forEach(m -> m.getModifier().setAbstract(false));
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
      // methods.forEach(m -> m.getModifier().setAbstract(isInterface));
      methods.forEach(m -> m.getModifier().setDerived(isInterface));
      // TODO AGe: why is it getting set to abstract?
      methods.forEach(m -> m.getModifier().setAbstract(false));
      cdType.addAllCDMembers(methods);
    }
  }

  // TODO AGe: types aren't being built correctly in the super method
  @Override
  protected List<ASTCDAttribute> getAttributesForFields(ASTCDType domainType) {
    List<ASTCDAttribute> attributesList = Lists.newLinkedList();
    List<ASTCDAttribute> visibleAttributes = CDSymbolTables.getAttributesInHierarchy(domainType).stream()
        .filter(GenService::isVisible).collect(Collectors.toList());

    for (ASTCDAttribute visibleAttribute: visibleAttributes) {
      ASTMCType type = createType(visibleAttribute);
      if (GenService.isOptional(visibleAttribute)) {
        String innerType = visibleAttribute.getMCType().printType().replace("Optional<", "");
        innerType = innerType.replace(">", "");
        type = MCTypeFacade.getInstance().createOptionalTypeOf(
            MCTypeFacade.getInstance().createQualifiedType(innerType)
        );
      }
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED.build(),
          type , GenService.createName(visibleAttribute.getName()));
      attribute.getModifier().setDerived(visibleAttribute.getModifier().isDerived());
      attributesList.add(attribute);
      if (GenService.isOptional(visibleAttribute)) {
        glex.replaceTemplate("cd2java.Value", attribute, new StringHookPoint(" = null"));
      }
    }
    return attributesList;
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
  protected String getInitialGemValue() {
    return "= new Gem()";
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
  protected void createIsBuildFinishedMethod(ASTCDClass clazz) {
    getCD4C().addMethod(clazz, "umlp.gui2ts.domain.TSIsBuildFinishedI");
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
