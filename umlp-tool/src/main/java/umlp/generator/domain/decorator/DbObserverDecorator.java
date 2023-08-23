/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
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
import java.util.function.Predicate;
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
    origCD.getCDDefinition().getCDClassesList().stream()
        .filter(c -> !c.getModifier().isDerived())
        .filter(c -> !c.getModifier().isAbstract())
        .forEach(c -> astcdPackage.addCDElement(withOrigin(createClass(c))));
  }

  protected ASTCDClass createClass(ASTCDClass domainClass) {
    String className = domainClass.getName();

    ASTCDClass observerClass = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage(className + "Observer"))
        .setName(className + SUFFIX)
        .addCDMember(getDAOAttribute(domainClass))
        .addAllCDMembers(getAssocDaoAttributes(domainClass)).build();

    UMLPSymbolTables.getAttributesInHierarchyWithDefiningType(domainClass).entrySet().stream()
        .filter(e -> !e.getKey().getModifier().isDerived())
        .forEach(e -> {
          ASTCDAttribute a = e.getKey();
          ASTCDMethodSignature set = getCD4C().addMethod(observerClass, "umlp.dbobserver.Notify",
              a.getName(), e.getValue().getName(), GenService.getType(a.getSymbol().getType()));
          glex.replaceTemplate("cd2java.Annotations", set, new StringHookPoint("@Override"));

          ASTCDCompilationUnit typeCD = UMLPSymbolTables.getDiagramFor(e.getValue());
          getCD4C().addImport(observerClass, getPackageName(typeCD, DEFAULT_PACKAGE) + "." + e.getValue().getName());
        });

    for (Map.Entry<ASTCDAssocSide, ASTCDAssociation> e : DAODecorator.getOppositeSideToAssociationMap(domainClass).entrySet()) {
      if (!e.getValue().getModifier().isDerived()) {
        CDRoleSymbol cdRollSymbol = e.getKey().getSymbol();

        SymTypeExpression type = cdRollSymbol.getType();

        SymTypeExpression otherType = cdRollSymbol.getOtherSide().getType();
        String otherTypeName = otherType.print();

        ASTCDCompilationUnit typeCD = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());
        ASTCDCompilationUnit otherTypeCD = UMLPSymbolTables.getDiagramFor((ASTCDType) otherType.getTypeInfo().getAstNode());
        getCD4C().addImport(observerClass, getPackageName(typeCD, DEFAULT_PACKAGE) + "." + type.print());
        getCD4C().addImport(observerClass, getPackageName(otherTypeCD, DEFAULT_PACKAGE) + "." + otherType.print());

        ASTCDMethodSignature add = null;
        ASTCDMethodSignature remove = null;
        if (e.getKey().getCDCardinality().isMult()) {
          add = getCD4C().addMethod(observerClass, "umlp.dbobserver.assocs.AddNotify",
              className, e.getValue().getName(), e.getKey(), otherTypeName);
          remove = getCD4C().addMethod(observerClass, "umlp.dbobserver.assocs.RemoveNotify",
              className, e.getValue().getName(), e.getKey(), otherTypeName);
        }
        ASTCDMethodSignature set = getCD4C().addMethod(observerClass, "umlp.dbobserver.assocs.SetNotify",
            className, e.getValue().getName(), e.getKey(), otherTypeName);

        if (add != null && remove != null) {
          glex.replaceTemplate("cd2java.Annotations", add, new StringHookPoint("@Override"));
          glex.replaceTemplate("cd2java.Annotations", remove, new StringHookPoint("@Override"));
        }
        glex.replaceTemplate("cd2java.Annotations", set, new StringHookPoint("@Override"));
      }
    }

    addImports(domainClass, observerClass);
    return observerClass;
  }

  private ASTCDAttribute getDAOAttribute(ASTCDClass domainClass) {
    ASTCDAttribute domainClassDaoAttr = getCDAttributeFacade().createAttribute(PRIVATE.build(),
        getMCTypeFacade().createQualifiedType(domainClass.getName() + "DAO"),
        "dao");
    getGlex().replaceTemplate("cd2java.Value", domainClassDaoAttr, new StringHookPoint(" = new " + domainClass.getName() + "DAO" + "()"));
    return domainClassDaoAttr;
  }

  private List<ASTCDAttribute> getAssocDaoAttributes(ASTCDClass domainClass) {
    Map<ASTCDAssocSide, ASTCDAssociation> assocSideToAssociationMap = DAODecorator.getOppositeSideToAssociationMap(domainClass);
    List<ASTCDAttribute> attributes = new ArrayList<>();
    for (ASTCDAssociation association : assocSideToAssociationMap.values()) {
      if (!association.getModifier().isDerived()) {
        String type = "Assoc" + StringTransformations.capitalize(association.getName()) + "DAO";
        String attrName = StringTransformations.uncapitalize(type);
        if (attributes.stream().noneMatch(attribute -> attribute.getName().equals(attrName))) {
          ASTCDAttribute assocDaoAttr = getCDAttributeFacade().createAttribute(PRIVATE.build(),
              getMCTypeFacade().createQualifiedType(type), attrName);
          getGlex().replaceTemplate("cd2java.Value", assocDaoAttr, new StringHookPoint(" = new " + type + "()"));
          attributes.add(assocDaoAttr);
        }
      }
    }
    return attributes;
  }

  protected void addImports(ASTCDClass domainClass, ASTCDType observerClass) {
    getCD4C().addImport(observerClass, PathUtil.getBackendRTECommonPath("DatabaseAccessManager"));

    List<ASTCDAssocSide> assocSides = CDSymbolTables.getAssociationsInHierarchy(domainClass).stream()
        .filter(GenService::isVisible).collect(Collectors.toList());
    assocSides.stream()
        .filter(Predicate.not(GenService::isSymbolDerived))
        .map(assocSide -> assocSide.getSymbol().getAssoc().getAssociation())
        .forEach(associationSymbol -> {
          if (!associationSymbol.getPackageName().isEmpty()) {
            getCD4C().addImport(observerClass, Joiners.DOT.join(
                associationSymbol.getPackageName().toLowerCase(),
                "Assoc" + StringTransformations.capitalize(associationSymbol.getName())) + "DAO");
          } else {
            getCD4C().addImport(observerClass, "Assoc" + StringTransformations.capitalize(associationSymbol.getName()) + "DAO");
          }
        });
  }
}
