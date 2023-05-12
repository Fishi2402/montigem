/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    origCD.getCDDefinition().getCDClassesList().stream()
        .filter(c -> !c.getModifier().isDerived())
        .filter(c -> !c.getModifier().isAbstract())
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

    UMLPSymbolTables.getAttributesInHierarchyWithDefiningType(domainClass).entrySet().stream()
      .filter(e -> !e.getKey().getModifier().isDerived())
      .forEach(e -> {
        ASTCDAttribute a = e.getKey();
        ASTCDMethodSignature set = getCD4C().addMethod(observerClass, "umlp.dbobserver.Notify",
            a.getName(), e.getValue().getName(), GenService.getType(a.getSymbol().getType()));
        glex.replaceTemplate("cd2java.Annotations", set, new StringHookPoint("@Override"));
      });

    for (Map.Entry<ASTCDAssocSide, ASTCDAssociation> e: DAODecorator.getOppositeSideToAssociationMap(domainClass, associations).entrySet()) {
      CDRoleSymbol cdRollSymbol = e.getKey().getSymbol();
      SymTypeExpression otherType = cdRollSymbol.getOtherSide().getType();
      String otherTypeName = otherType.print();

      ASTCDMethodSignature add = null;
      ASTCDMethodSignature remove = null;
      if(e.getKey().getCDCardinality().isMult()) {
        add = getCD4C().addMethod(observerClass, "umlp.dbobserver.assocs.AddNotify",
            className, e.getValue().getName(), e.getKey(), otherTypeName);
        remove = getCD4C().addMethod(observerClass, "umlp.dbobserver.assocs.RemoveNotify",
            className, e.getValue().getName(), e.getKey(), otherTypeName);
      }
      ASTCDMethodSignature set = getCD4C().addMethod(observerClass, "umlp.dbobserver.assocs.SetNotify",
          className, e.getValue().getName(), e.getKey(), otherTypeName);

      if(add != null && remove != null) {
        glex.replaceTemplate("cd2java.Annotations", add, new StringHookPoint("@Override"));
        glex.replaceTemplate("cd2java.Annotations", remove, new StringHookPoint("@Override"));
      }
      glex.replaceTemplate("cd2java.Annotations", set, new StringHookPoint("@Override"));
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
