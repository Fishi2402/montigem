/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import com.google.common.collect.Lists;
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
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.methods.AssocAccessorDecorator;
import umlp.generator.domain.decorator.methods.AssocMutatorDecorator;
import umlp.generator.domain.decorator.methods.MethodGenService;
import umlp.generator.domain.decorator.methods.SimpleMethodMutatorDecorator;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static umlp.generator.domain.decorator.GenService.createType;

public abstract class DomainTypeDecorator extends AbstractDecorator {

  public DomainTypeDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
    super.addImports(domainType, cdType);
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("GemIdentifiable"));
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("Gem"));
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("GemList"));
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommonPath("GemBuildable"));
    addDBAnnotationInfo(cdType);
  }

  protected void addDBAnnotationInfo(ASTCDType cdType) {
    // empty
  }

  protected void createGetterSetterForAttrs(ASTCDType domainType, ASTCDType cdType,
                                            List<ASTCDAttribute> attrList, boolean isInterface) {
    CDGenService service = new MethodGenService();
    MethodDecorator methodDecorator = new MethodDecorator(glex,
        new AccessorDecorator(glex, service),
        new MutatorDecorator(glex, new SimpleMethodMutatorDecorator(glex, service, domainType),
            new OptionalMutatorDecorator(glex, service),
            new ListMutatorDecorator(glex, service)), service);
    for (ASTCDAttribute attribute : attrList) {
      List<ASTCDMethod> methods = methodDecorator.decorate(attribute);
      methods.forEach(m -> m.getModifier().setAbstract(isInterface));
      cdType.addAllCDMembers(methods);
    }
  }

  protected void createGetterSetterForAssocs(ASTCDType cdType,
                                             List<ASTCDAttribute> attrList, boolean isInterface) {
    CDGenService service = new MethodGenService();
    MethodDecorator methodDecorator = new MethodDecorator(glex,
        new AssocAccessorDecorator(glex, service, cdType),
        new AssocMutatorDecorator(glex, service, cdType), service);
    for (ASTCDAttribute attribute : attrList) {
      List<ASTCDMethod> methods = methodDecorator.decorate(attribute);
      if (isInterface) {
        methods.forEach(m -> m.getModifier().setAbstract(true));
      } else if (methods.stream().anyMatch(m -> m.getModifier().isAbstract())) {
        cdType.getModifier().setAbstract(true);
      } else {
        methods.forEach(m -> m.getModifier().setAbstract(false));
      }
      cdType.addAllCDMembers(methods);
    }
  }

  protected List<ASTCDAttribute> getAttributesForFields(ASTCDType domainType) {
    List<ASTCDAttribute> attributesList = Lists.newLinkedList();
    List<ASTCDAttribute> visibleAttributes = CDSymbolTables.getAttributesInHierarchy(domainType).stream()
        .filter(GenService::isVisible).collect(Collectors.toList());

    for (ASTCDAttribute visibleAttribute : visibleAttributes) {
      // TODO AGe: wrong optionals
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED.build(),
          createType(visibleAttribute), GenService.createName(visibleAttribute.getName()));
      attribute.getModifier().setDerived(visibleAttribute.getModifier().isDerived());
      attributesList.add(attribute);
      if (GenService.isOptional(visibleAttribute)) {
        glex.replaceTemplate("cd2java.Value", attribute, new StringHookPoint(" = Optional.empty()"));
      }
    }
    return attributesList;
  }

  protected List<ASTCDAttribute> getAttributesForAssociations(ASTCDType domainType, boolean isInterface, ASTCDCompilationUnit cd) {
    List<ASTCDAttribute> attrList = Lists.newLinkedList();
    List<ASTCDAssocSide> associations = (isInterface ?
        CDSymbolTables.getAssociations(domainType) :
        CDSymbolTables.getAssociationsInHierarchy(domainType))
        .stream()
        .filter(GenService::isVisible)
        .filter(side -> !isDerived(side))
        .collect(Collectors.toList());

    // handle associations
    for (ASTCDAssocSide assocSide : associations) {
      String type = assocSide.getSymbol().getType().print();
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(
          PROTECTED.build(),
          getMCTypeFacade().createBasicGenericTypeOf(GenService.isList(assocSide) ? "GemList" : "Gem", type),
          GenService.createName(assocSide.getName())
      );
      if (GenService.isSymbolDerived(assocSide)) {
        attribute.getModifier().setDerived(true);
      }
      attrList.add(attribute);
    }
    return attrList;
  }


  protected AttributesTuple getAllAttributes(ASTCDType domainType, boolean isInterface, ASTCDCompilationUnit cd) {
    return new AttributesTuple(
        getAttributesForFields(domainType),
        getAttributesForAssociations(domainType, isInterface, cd)
    );
  }

  protected static class AttributesTuple {
    final List<ASTCDAttribute> attrFields;
    final List<ASTCDAttribute> attrAssocs;
    final List<ASTCDAttribute> attrList;

    protected AttributesTuple(List<ASTCDAttribute> attrFields, List<ASTCDAttribute> attrAssocs) {
      this.attrFields = attrFields;
      this.attrAssocs = attrAssocs;
      attrList = new ArrayList<>(attrAssocs);
      attrList.addAll(attrFields);
    }

    public List<ASTCDAttribute> getAttrList() {
      return attrList;
    }

    public List<ASTCDAttribute> getAttrAssocs() {
      return attrAssocs;
    }

    public List<ASTCDAttribute> getAttrFields() {
      return attrFields;
    }
  }

}
