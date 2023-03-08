/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PRIVATE;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class DbObserverDecorator extends AbstractDecorator {

  public static final String SUFFIX = "DbObserver";

  public DbObserverDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
      .forEach(c -> astcdPackage.addCDElement(createClass(c, origCD.getCDDefinition().getCDAssociationsList())));
  }

  protected ASTCDClass createClass(ASTCDClass domainClass, List<ASTCDAssociation> associations) {
    String className = domainClass.getName();

    ASTCDClass observerClass = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage(className + "Observer"))
      .setName(className + SUFFIX)
      .addCDMember(getDAOAttribute(domainClass))
      .addAllCDMembers(getAssocDaoAttributes(domainClass, associations)).build();

    CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
      .filter(a -> !a.getModifier().isDerived())
      .forEach(a -> getCD4C().addMethod(observerClass, "umlp.dbobserver.Notify",
              a.getName(), className, GenService.getType(a.getSymbol().getType())));

    for (Map.Entry<ASTCDAssocSide, ASTCDAssociation> e: DAODecorator.getOppositeSideToAssociationMap(domainClass, associations).entrySet()) {
      getCD4C().addMethod(observerClass, "umlp.dbobserver.assocs.AddNotify",
          className,  e.getValue().getName(), e.getKey());
      getCD4C().addMethod(observerClass, "umlp.dbobserver.assocs.RemoveNotify",
          className,  e.getValue().getName(), e.getKey());
      getCD4C().addMethod(observerClass, "umlp.dbobserver.assocs.SetNotify",
          className,  e.getValue().getName(), e.getKey().getName());
    }

    addImports(observerClass);

    return observerClass;
  }

  private ASTCDAttribute getDAOAttribute(ASTCDClass domainClass) {
    ASTCDAttribute domainClassDaoAttr = getCDAttributeFacade().createAttribute(PRIVATE.build(),
        getMCTypeFacade().createQualifiedType(domainClass.getName()+"DAO"),
        "dao");
    getGlex().replaceTemplate("cd2java.Value", domainClassDaoAttr, new StringHookPoint(" = new "+domainClass.getName()+"DAO"+"()"));
    return domainClassDaoAttr;
  }

  private List<ASTCDAttribute> getAssocDaoAttributes(ASTCDClass domainClass, List<ASTCDAssociation> associations) {
    Map<ASTCDAssocSide, ASTCDAssociation> assocSideToAssociationMap = DAODecorator.getOppositeSideToAssociationMap(domainClass, associations);
    List<ASTCDAttribute> attributes = new ArrayList<>();
    for (ASTCDAssociation association : assocSideToAssociationMap.values()) {
      String type = "Assoc"+StringTransformations.capitalize(association.getName())+"DAO";
      String attrName = StringTransformations.uncapitalize(type);
      if(attributes.stream().noneMatch(attribute -> attribute.getName().equals(attrName))) {
        ASTCDAttribute assocDaoAttr = getCDAttributeFacade().createAttribute(PRIVATE.build(),
            getMCTypeFacade().createQualifiedType(type), attrName);
        getGlex().replaceTemplate("cd2java.Value", assocDaoAttr, new StringHookPoint(" = new " + type + "()"));
        attributes.add(assocDaoAttr);
      }
    }
    return attributes;
  }

  protected void addImports(ASTCDType observerClass) {
    getCD4C().addImport(observerClass, PathUtil.getBackendRTECommonPath("DatabaseAccessManager"));
  }
}
