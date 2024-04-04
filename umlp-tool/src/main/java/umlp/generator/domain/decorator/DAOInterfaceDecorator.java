package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterfaceBuilder;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.DAODecorator.getOppositeSideToAssociationMap;

public class DAOInterfaceDecorator extends AbstractDecorator {

  public static final String INTERFACE_SUFFIX = "Interface";

  public DAOInterfaceDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    getConcreteDomainClasses(origCD).forEach(c -> astcdPackage.addCDElement(withOrigin(createDAOInterfaceForDomainClass(
        c))));

    origCD.getCDDefinition().getCDAssociationsList().stream()
        .filter(a -> !a.getModifier().isDerived())
        .forEach(assoc -> astcdPackage.addCDElement(withOrigin(createDAOClassForAssoc(assoc))));
  }

  /**
   * Creates DaoClass from a DomainClass
   * @param domainClass
   * @return
   */
  private ASTCDInterface createDAOInterfaceForDomainClass(ASTCDClass domainClass) {
    Map<ASTCDAssocSide, ASTCDAssociation> assocSideToAssociation = getOppositeSideToAssociationMap(domainClass)
        .entrySet().stream()
        .filter(entry -> !entry.getValue().getModifier().isDerived())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    ASTCDInterfaceBuilder builder = UMLPMill.cDInterfaceBuilder()
        .setModifier(PUBLIC.build())
        .setName(domainClass.getName() + "DAO"+INTERFACE_SUFFIX);

    ASTCDInterface astcdInterface = builder.build();
    addStaticImports(astcdInterface);
    this.addImports(domainClass, astcdInterface);
    domainClassDAOMethods(astcdInterface, domainClass, assocSideToAssociation);
    return astcdInterface;
  }

  private void domainClassDAOMethods(ASTCDInterface daoInterface, ASTCDClass domainClass, Map<ASTCDAssocSide, ASTCDAssociation> assocSideToAssoc) {
    selectMethod(daoInterface, domainClass);
    selectAllIdsMethod(daoInterface, domainClass);
    unpackMethod(daoInterface, domainClass);
    insertMethods(daoInterface, domainClass);
    deleteMethod(daoInterface, domainClass);
    updateAttrMethod(daoInterface, domainClass);
    updateOptionalAttrMethod(daoInterface, domainClass);
    updateAndSelectListAttrMethods(daoInterface, domainClass);
  }

  protected void deleteMethod(ASTCDInterface daoInterface, ASTCDClass domainClass) {
    getCD4C().addMethod(daoInterface, "umlp.daointerface.Delete", domainClass.getName());
  }

  protected void insertMethods(ASTCDInterface daoInterface, ASTCDClass domainClass) {
    getCD4C().addMethod(daoInterface, "umlp.daointerface.Insert", domainClass.getName());
  }

  protected void unpackMethod(ASTCDInterface daoInterface, ASTCDClass domainClass){
    getCD4C().addMethod(daoInterface, "umlp.daointerface.Unpack", domainClass.getName());
  }

  protected void selectMethod(ASTCDInterface daoInterface, ASTCDClass domainClass) {
    getCD4C().addMethod(daoInterface, "umlp.daointerface.Select", domainClass.getName());
  }

  protected void selectAllIdsMethod(ASTCDInterface daoInterface, ASTCDClass domainClass){
    getCD4C().addMethod(daoInterface, "umlp.daointerface.SelectAllIds", domainClass.getName());
  }

  protected void updateAttrMethod(ASTCDInterface daoInterface, ASTCDClass domainClass) {
    getNormalAttributes(domainClass).forEach(attr ->
        getCD4C().addMethod(daoInterface, "umlp.daointerface.UpdateAttr",
            attr.getName(), attr.printType()));
  }

  protected void updateOptionalAttrMethod(ASTCDInterface daoInterface, ASTCDClass domainClass) {
    getOptionalAttributes(domainClass).forEach(attr ->
            getCD4C().addMethod(daoInterface, "umlp.daointerface.UpdateOptionalAttr",
                attr.getName(), GenService.createType(attr).printType()));
  }

  protected void updateAndSelectListAttrMethods(ASTCDInterface daoInterface, ASTCDClass domainClass) {
    getListAttributes(domainClass).forEach(attr -> {
              String innerType = GenService.getInnerType(GenService.createType(attr));
              getCD4C().addMethod(daoInterface, "umlp.daointerface.UpdateListAttr", attr.getName(), innerType);
              getCD4C().addMethod(daoInterface, "umlp.daointerface.SelectListAttr", attr, innerType);
            }
        );
  }

  // Interface of Assoc Daos for each domain class

  /**
   * Creates DaoClass from an Association
   *
   * @param assoc
   * @return
   */
  private ASTCDInterface createDAOClassForAssoc(ASTCDAssociation assoc) {
    ASTCDInterfaceBuilder builder = UMLPMill.cDInterfaceBuilder()
        .setModifier(PUBLIC.build())
        .setName("Assoc" + StringTransformations.capitalize(assoc.getName()) + "DAO"+INTERFACE_SUFFIX);
    ASTCDInterface associationDAOClass = builder.build();
    addStaticImports(associationDAOClass);
    assocDAOMethods(associationDAOClass, assoc);
    return associationDAOClass;
  }

  protected void assocDAOMethods(ASTCDInterface daoInterface, ASTCDAssociation association) {
    addMethodsForAssociationDAO(daoInterface, association);

    removeMethodForAssociationDAO(daoInterface, association);
    if (association.getCDAssocDir().isDefinitiveNavigableRight()) {
      selectMethodForAssocDAO(daoInterface, association.getLeft(), association.getRight());
      removeAllMethodForAssocDAO(daoInterface, association.getLeft().getName());
      updateMethodForAssocDAO(daoInterface, association.getLeft().getName(), association.getRight().getName());
    }
    if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
      selectMethodForAssocDAO(daoInterface, association.getRight(), association.getLeft());
      removeAllMethodForAssocDAO(daoInterface, association.getRight().getName());
      updateMethodForAssocDAO(daoInterface, association.getRight().getName(), association.getLeft().getName());
    }
  }

  protected void removeAllMethodForAssocDAO(ASTCDInterface daoInterface, String startName) {
    getCD4C().addMethod(daoInterface, "umlp.daointerface.associations.RemoveAllForAssocDAO", startName);
  }

  protected void selectMethodForAssocDAO(ASTCDInterface daoInterface, ASTCDAssocSide assocStart, ASTCDAssocSide assocEnd) {
    if(GenService.isList(assocEnd)) {
      selectMethodForListAssocDAO(daoInterface, assocStart, assocEnd);
    } else {
      selectMethodForGemAssocDAO(daoInterface, assocStart, assocEnd);
    }
  }

  private void selectMethodForListAssocDAO(ASTCDInterface daoInterface, ASTCDAssocSide start, ASTCDAssocSide end) {
    getCD4C().addMethod(daoInterface, "umlp.daointerface.associations.SelectForAssocDAO", start.getName(), end.getName());
  }

  private void selectMethodForGemAssocDAO(ASTCDInterface daoInterface, ASTCDAssocSide start, ASTCDAssocSide end) {
    getCD4C().addMethod(daoInterface, "umlp.daointerface.associations.SelectForGemAssocDAO", start.getName(), end.getName());
  }

  protected void addMethodsForAssociationDAO(ASTCDInterface daoInterface, ASTCDAssociation assoc) {
    getCD4C().addMethod(daoInterface, "umlp.daointerface.associations.AddForAssocDAO", assoc);
    getCD4C().addMethod(daoInterface, "umlp.daointerface.associations.AddManyForAssocDAO", assoc);
    getCD4C().addMethod(daoInterface, "umlp.daointerface.associations.AddManyToBatchForAssocDAO", assoc);
    getCD4C().addMethod(daoInterface, "umlp.daointerface.associations.ExecuteAddBatchForAssocDAO", assoc);
  }

  protected void updateMethodForAssocDAO(ASTCDInterface daoInterface, String startName, String endName) {
    getCD4C().addMethod(daoInterface, "umlp.daointerface.associations.UpdateForAssocDAO", startName, endName);
  }

  protected void removeMethodForAssociationDAO(ASTCDInterface daoInterface, ASTCDAssociation assoc) {
    getCD4C().addMethod(daoInterface, "umlp.daointerface.associations.RemoveForAssocDAO", assoc);
  }


  // Helper Methods

  protected List<ASTCDAttribute> getOptionalAttributes(ASTCDClass domainClass) {
    return CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
        .filter(a -> !a.getModifier().isDerived() && GenService.isOptional(a)).collect(Collectors.toList());
  }

  protected List<ASTCDAttribute> getListAttributes(ASTCDClass domainClass) {
    return CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
        .filter(a -> !a.getModifier().isDerived() && GenService.isList(a)).collect(Collectors.toList());
  }

  protected List<ASTCDAttribute> getNormalAttributes(ASTCDClass domainClass) {
    return CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
        .filter(a -> !a.getModifier().isDerived() &&
            !GenService.isList(a) && !GenService.isOptional(a)).collect(Collectors.toList());
  }

  private void addStaticImports(ASTCDType clazz) {
    // UMLP RTE
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("DatabaseAccessManager"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("Gem"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("LoadResult"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("LoadingState"));

    // SQL
    getCD4C().addImport(clazz, "java.sql.PreparedStatement");
    getCD4C().addImport(clazz, "java.sql.ResultSet");
    getCD4C().addImport(clazz, "java.sql.SQLException");

    // Java
    getCD4C().addImport(clazz, "java.util.List");
    getCD4C().addImport(clazz, "java.util.Optional");
    getCD4C().addImport(clazz, "java.util.Map;");
    getCD4C().addImport(clazz, "java.util.HashMap;");
  }

}
