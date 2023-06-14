package umlp.generator.domain.decorator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDClassBuilder;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.db.sql.SQLSchemaGenerator;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PRIVATE;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class DAODecorator extends AbstractDecorator {

  protected String cdName = "";

  public DAODecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    cdName = origCD.getCDDefinition().getName();
    origCD.getCDDefinition().getCDClassesList().stream()
        .filter(c -> !c.getSymbol().isIsDerived())
        .filter(c -> !c.getSymbol().isIsAbstract())
        .forEach(c -> astcdPackage.addCDElement(createDAOClassForDomainClass(c, origCD.getCDDefinition().getCDAssociationsList())));

    origCD.getCDDefinition().getCDAssociationsList().stream().filter(a -> !a.getModifier().isDerived()).forEach
        (assoc -> astcdPackage.addCDElement(createDAOClassForAssoc(assoc)));

  }

  /**
   * Creates DaoClass from a DomainClass
   * @param domainClass
   * @param associations
   * @return
   */
  private ASTCDClass createDAOClassForDomainClass(ASTCDClass domainClass, List<ASTCDAssociation> associations) {
    Map<ASTCDAssocSide, ASTCDAssociation> assocSideToAssociation = getOppositeSideToAssociationMap(domainClass, associations);
    ASTCDClassBuilder builder = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(domainClass.getName()+"DAO")
        .addAllCDMembers(createAttributesForDomainClassDAOs(domainClass))
        .addAllCDMembers(getAssocAttributes(assocSideToAssociation));

    ASTCDClass clazz = builder.build();
    addStaticImports(clazz);
    super.addImports(domainClass, clazz);
    domainClassDAOMethods(clazz, domainClass, assocSideToAssociation);
    return clazz;
  }

  /**
   * Creates DaoClass from an Association
   * @param assoc
   * @return
   */
  private ASTCDClass createDAOClassForAssoc(ASTCDAssociation assoc) {
    ASTCDClassBuilder builder = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName("Assoc"+StringTransformations.capitalize(assoc.getName())+"DAO")
        .addAllCDMembers(createAttributesForAssociationDAOs(assoc));
    ASTCDClass associationDAOClass = builder.build();
    addStaticImports(associationDAOClass);
    assocDAOMethods(associationDAOClass, assoc);
    return associationDAOClass;
  }

  // Attributes of Association DAOs
  private List<ASTCDAttribute> getAssocAttributes(Map<ASTCDAssocSide, ASTCDAssociation> assocSideToAssoc) {
    List<ASTCDAttribute> attributesList = new ArrayList<>();

    for (ASTCDAssocSide assocSide : assocSideToAssoc.keySet()) {
      String type = "Assoc"+StringTransformations.capitalize(assocSideToAssoc.get(assocSide).getName())+"DAO";
      String attrName = StringTransformations.uncapitalize(type);
      if(attributesList.stream().noneMatch(attribute -> attribute.getName().equals(attrName))){
        ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PRIVATE.build(),
            getMCTypeFacade().createQualifiedType(type), attrName);
        getGlex().replaceTemplate("cd2java.Value", attribute, new StringHookPoint(" = new "+type+"()"));
        attributesList.add(attribute);
      }
    }
    return attributesList;
  }

  private List<ASTCDAttribute> createAttributesForDomainClassDAOs(ASTCDClass domainClass) {
    List<ASTCDAttribute> attributesList = Lists.newLinkedList();

    // Select / Insert PreparedStatements
    attributesList.add(createPreparedStatementAttrForMethod(domainClass.getName(), "select"));
    attributesList.add(createPreparedStatementAttrForMethod(domainClass.getName(), "selectAllIds"));
    attributesList.add(createPreparedStatementAttrForMethod(domainClass.getName(), "insert"));
    attributesList.add(createPreparedStatementAttrForMethod(domainClass.getName(), "delete"));

    // Set/Update PreparedStatements
    for (ASTCDAttribute attr : CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
        .filter(a -> !a.getModifier().isDerived()).collect(Collectors.toList())) {
      if(GenService.isList(attr)) {
        ASTCDAttribute removePrepStatement = getCDAttributeFacade().createAttribute(PRIVATE.build(),
                createAttributeType("PreparedStatement"), getStatementAttributeName(attr, "remove"));
        attributesList.add(removePrepStatement);
        ASTCDAttribute updatePrepStatement = getCDAttributeFacade().createAttribute(PRIVATE.build(),
                createAttributeType("PreparedStatement"), getStatementAttributeName(attr, "update"));
        attributesList.add(updatePrepStatement);
        ASTCDAttribute selectPrepStatement = getCDAttributeFacade().createAttribute(PRIVATE.build(),
                createAttributeType("PreparedStatement"), getStatementAttributeName(attr, "select"));
        attributesList.add(selectPrepStatement);
      } else {
        ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PRIVATE.build(),
            createAttributeType("PreparedStatement"), getStatementAttributeName(attr, "update"));
        attributesList.add(attribute);
      }
    }
    return attributesList;
  }

  private ASTCDAttribute createPreparedStatementAttrForMethod(String domainClassName, String methodName) {
    return getCDAttributeFacade().createAttribute(PRIVATE.build(),
        createAttributeType("PreparedStatement"), methodName+domainClassName);
  }

  private List<ASTCDAttribute> createAttributesForAssociationDAOs(ASTCDAssociation association) {
    List<ASTCDAttribute> attributesList = Lists.newLinkedList();

    // Add / Remove / Update PreparedStatements
    attributesList.add(createPreparedStatementAttrForMethod("add", association));
    attributesList.add(createPreparedStatementAttrForMethod("remove", association));

    if(association.getCDAssocDir().isDefinitiveNavigableRight()) {
      attributesList.add(createPreparedStatementAttrForMethodOfSide("removeAllEntriesRelatedTo", association.getLeft()));
      attributesList.add(createPreparedStatementAttrForMethodOfSide("update", association.getRight()));
    }
    if(association.getCDAssocDir().isDefinitiveNavigableLeft()) {
      attributesList.add(createPreparedStatementAttrForMethodOfSide("removeAllEntriesRelatedTo", association.getRight()));
      attributesList.add(createPreparedStatementAttrForMethodOfSide("update", association.getLeft()));
    }

    // Select for each side PreparedStatement
    String idNumerusLeft = getIDNumerusForAssociationSide(association.getLeft());
    String idNumerusRight = getIDNumerusForAssociationSide(association.getRight());

    attributesList.add(createPreparedStatementForSelect(association.getLeft().getName(), association.getRight().getName(), idNumerusLeft));
    attributesList.add(createPreparedStatementForSelect(association.getRight().getName(), association.getLeft().getName(), idNumerusRight));

    return attributesList;
  }

  private ASTCDAttribute createPreparedStatementAttrForMethod(String method, ASTCDAssociation association) {
    return getCDAttributeFacade().createAttribute(PRIVATE.build(), createAttributeType("PreparedStatement"),
        method+StringTransformations.capitalize(association.getName()));
  }

  private ASTCDAttribute createPreparedStatementAttrForMethodOfSide(String method, ASTCDAssocSide assocSide) {
    return getCDAttributeFacade().createAttribute(PRIVATE.build(), createAttributeType("PreparedStatement"),
        method+StringTransformations.capitalize(assocSide.getName()));
  }

  private ASTCDAttribute createPreparedStatementForSelect(String requested, String given, String requestedNumerus) {
    return getCDAttributeFacade().createAttribute(PRIVATE.build(),
        createAttributeType("PreparedStatement"),
        "select"+StringTransformations.capitalize(requested)+"Id"+requestedNumerus+"For"+StringTransformations.capitalize(given));
  }

  private String getIDNumerusForAssociationSide(ASTCDAssocSide side){
    if(GenService.isList(side)) {
      return "s";
    } return "";
  }

  private String getStatementAttributeName(ASTCDAttribute attr, String methodName) {
    return methodName+StringTransformations.capitalize(attr.getName())+"Statement";
  }

  private ASTMCType createAttributeType(String typeName) {
    return getMCTypeFacade().createQualifiedType(typeName);
  }

  private void domainClassDAOMethods(ASTCDClass daoClass, ASTCDClass domainClass, Map<ASTCDAssocSide, ASTCDAssociation> assocSideToAssoc) {
    Map<ASTCDAssocSide, ASTCDAssociation> gemAssocSideToAssoc =
        assocSideToAssoc.entrySet().stream().filter(e -> !GenService.isList(e.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    Map<ASTCDAssocSide, ASTCDAssociation> listAssocSideToAssoc =
        assocSideToAssoc.entrySet().stream().filter(e -> GenService.isList(e.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    selectMethod(daoClass, domainClass, listAssocSideToAssoc, gemAssocSideToAssoc);
    selectAllIdsMethod(daoClass, domainClass, listAssocSideToAssoc, gemAssocSideToAssoc);
    unpackMethod(daoClass, domainClass, listAssocSideToAssoc, gemAssocSideToAssoc);
    insertMethod(daoClass, domainClass, listAssocSideToAssoc, gemAssocSideToAssoc);
    deleteMethod(daoClass, domainClass, assocSideToAssoc);
    updateAttrMethod(daoClass, domainClass);
    updateOptionalAttrMethod(daoClass, domainClass);
    updateAndSelectListAttrMethods(daoClass, domainClass);
  }

  protected void assocDAOMethods(ASTCDClass daoClass, ASTCDAssociation association) {
    addMethodForAssociationDAO(daoClass, association);

    removeMethodForAssociationDAO(daoClass, association);
    if(association.getCDAssocDir().isDefinitiveNavigableRight()) {
      selectMethodForAssocDAO(daoClass, association.getName(), association.getLeft(), association.getRight());
      removeAllMethodForAssocDAO(daoClass, association.getName(), association.getLeft().getName());
      updateMethodForAssocDAO(daoClass, association.getName(), association.getLeft().getName(), association.getRight().getName());
    }
    if(association.getCDAssocDir().isDefinitiveNavigableLeft()) {
      selectMethodForAssocDAO(daoClass, association.getName(), association.getRight(), association.getLeft());
      removeAllMethodForAssocDAO(daoClass, association.getName(), association.getRight().getName());
      updateMethodForAssocDAO(daoClass, association.getName(), association.getRight().getName(), association.getLeft().getName());
    }
  }


  // All Methods for Association DAOs

  protected void removeAllMethodForAssocDAO(ASTCDClass daoClass, String assocName, String startName) {
    getCD4C().addMethod(daoClass, "umlp.dao.associations.RemoveAllInitForAssocDAO", startName,
        SQLSchemaGenerator.convertAssociationRollName(startName), SQLSchemaGenerator.convertAssocName(assocName));
    getCD4C().addMethod(daoClass, "umlp.dao.associations.RemoveAllForAssocDAO", startName);
  }

  protected void selectMethodForAssocDAO(ASTCDClass daoClass, String assocName, ASTCDAssocSide assocStart, ASTCDAssocSide assocEnd) {

    if(GenService.isList(assocEnd)) {
      selectInitMethodForListAssocDAO(daoClass, assocName, assocStart, assocEnd, "s");
      selectMethodForListAssocDAO(daoClass, assocName, assocStart, assocEnd);
    } else {
      selectInitMethodForListAssocDAO(daoClass, assocName, assocStart, assocEnd, "");
      selectMethodForGemAssocDAO(daoClass, assocName, assocStart, assocEnd);
    }
  }

  protected void selectInitMethodForListAssocDAO(ASTCDClass daoClass, String assocName, ASTCDAssocSide start, ASTCDAssocSide end, String numerus) {
    String startName = start.getName();
    String endName = end.getName();
    getCD4C().addMethod(daoClass, "umlp.dao.associations.SelectInitForAssocDAO", startName, endName,
        SQLSchemaGenerator.convertAssociationRollName(startName), SQLSchemaGenerator.convertAssociationRollName(endName),
        SQLSchemaGenerator.convertAssocName(assocName), numerus);
  }

  private void selectMethodForListAssocDAO(ASTCDClass daoClass, String assocName, ASTCDAssocSide start, ASTCDAssocSide end) {
    getCD4C().addMethod(daoClass, "umlp.dao.associations.SelectForAssocDAO", start.getName(), end.getName());
  }

  private void selectMethodForGemAssocDAO(ASTCDClass daoClass, String assocName, ASTCDAssocSide start, ASTCDAssocSide end) {
    getCD4C().addMethod(daoClass, "umlp.dao.associations.SelectForGemAssocDAO", start.getName(), end.getName());
  }

  protected void addMethodForAssociationDAO(ASTCDClass daoClass, ASTCDAssociation assoc) {
    getCD4C().addMethod(daoClass, "umlp.dao.associations.AddInitForAssocDAO", assoc.getName(),
        SQLSchemaGenerator.convertAssociationRollName(assoc.getLeft().getName()), SQLSchemaGenerator.convertAssociationRollName(assoc.getRight().getName()),
        SQLSchemaGenerator.convertAssocName(assoc.getName()));
    getCD4C().addMethod(daoClass, "umlp.dao.associations.AddForAssocDAO", assoc);
  }

  protected void updateMethodForAssocDAO(ASTCDClass daoClass, String assocName, String startName, String endName) {
    getCD4C().addMethod(daoClass, "umlp.dao.associations.UpdateInitForAssocDAO", endName,
        SQLSchemaGenerator.convertAssociationRollName(startName), SQLSchemaGenerator.convertAssociationRollName(endName),
        SQLSchemaGenerator.convertAssocName(assocName));
    getCD4C().addMethod(daoClass, "umlp.dao.associations.UpdateForAssocDAO", startName, endName);
  }

  protected void removeMethodForAssociationDAO(ASTCDClass daoClass, ASTCDAssociation assoc) {
    getCD4C().addMethod(daoClass, "umlp.dao.associations.RemoveInitForAssocDAO",  assoc.getName(),
        SQLSchemaGenerator.convertAssociationRollName(assoc.getLeft().getName()), SQLSchemaGenerator.convertAssociationRollName(assoc.getRight().getName()),
        SQLSchemaGenerator.convertAssocName(assoc.getName()));
    getCD4C().addMethod(daoClass, "umlp.dao.associations.RemoveForAssocDAO", assoc);
  }


  // All methods for domain class daos

  private void updateAndSelectListAttrMethods(ASTCDClass doaClass, ASTCDClass domainClass) {
    CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
        .filter(a -> !a.getModifier().isDerived() && GenService.isList(a)).forEach(attr -> {
              GenService.createType(attr);
              String innerType = GenService.getInnerType(GenService.createType(attr));
              String tableName = SQLSchemaGenerator.convertClassName(domainClass.getName(), SQLSchemaGenerator.AttributeType.PLAIN)+
                  "$"+SQLSchemaGenerator.convertAttributeName(attr.getName(), SQLSchemaGenerator.AttributeType.PLAIN);
              String dbType = GenService.getDBType(attr.getSymbol().getType());
              getCD4C().addMethod(doaClass, "umlp.dao.UpdateListAttr", tableName, attr, innerType, dbType);
              getCD4C().addMethod(doaClass, "umlp.dao.SelectListAttr", tableName, attr, innerType, dbType);
            }
        );
  }

  protected void updateAttrMethod(ASTCDClass doaClass, ASTCDClass domainClass){
    getNormalAttributes(domainClass).forEach(attr ->
      getCD4C().addMethod(doaClass, "umlp.dao.UpdateAttr", SQLSchemaGenerator.convertClassName(domainClass.getName(), SQLSchemaGenerator.AttributeType.ESCAPED_QUOTED),
              attr.getName(), attr.printType(),
                    GenService.getDBType(attr.getSymbol().getType())));
  }

  private void updateOptionalAttrMethod(ASTCDClass doaClass, ASTCDClass domainClass){
    CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
        .filter(a -> !a.getModifier().isDerived() && GenService.isOptional(a)).forEach(attr ->
            getCD4C().addMethod(doaClass, "umlp.dao.UpdateOptionalAttr", SQLSchemaGenerator.convertClassName(domainClass.getName(), SQLSchemaGenerator.AttributeType.ESCAPED_QUOTED), attr.getName(),
                    GenService.createType(attr).printType(), GenService.getDBType(attr.getSymbol().getType())));
  }

  protected void deleteMethod(ASTCDClass daoClass, ASTCDClass domainClass, Map<ASTCDAssocSide, ASTCDAssociation> assocSideToAssoc) {
    getCD4C().addMethod(daoClass, "umlp.dao.Delete", SQLSchemaGenerator.convertClassName(domainClass.getName(), SQLSchemaGenerator.AttributeType.ESCAPED_QUOTED),
        domainClass.getName(), assocSideToAssoc);
  }

  protected void selectMethod(ASTCDClass daoClass, ASTCDClass domainClass,
                            Map<ASTCDAssocSide, ASTCDAssociation> listAssociationMap,
                            Map<ASTCDAssocSide, ASTCDAssociation> gemAssociationMap) {
    List<ASTCDAttribute> listAttributes = getListAttributes(domainClass);
    List<ASTCDAttribute> attributes = getNormalAttributes(domainClass);
    Map<ASTCDAttribute, String> optMap = Maps.newLinkedHashMap();
    getOptionalAttributes(domainClass).forEach(e -> optMap.put(e,
                    SQLSchemaGenerator.convertAttributeName(e.getName(), SQLSchemaGenerator.AttributeType.QUOTED)));

    getCD4C().addMethod(daoClass, "umlp.dao.SelectInit",
        SQLSchemaGenerator.convertClassName(domainClass.getName(), SQLSchemaGenerator.AttributeType.ESCAPED_QUOTED), domainClass.getName(),
        attributes.stream().map(a -> SQLSchemaGenerator.convertAttributeName(a.getName(), SQLSchemaGenerator.AttributeType.ESCAPED_QUOTED)).collect(Collectors.toList()));

    Map<ASTCDAttribute, String> attributeMap = Maps.newLinkedHashMap();
    attributes.forEach(e -> attributeMap.put(e,
            SQLSchemaGenerator.convertAttributeName(e.getName(), SQLSchemaGenerator.AttributeType.QUOTED)));
    getCD4C().addMethod(daoClass, "umlp.dao.Select", domainClass.getName(), cdName, attributeMap,
        optMap, listAttributes, listAssociationMap, gemAssociationMap);
  }

  protected void selectAllIdsMethod(ASTCDClass daoClass, ASTCDClass domainClass,
                                    Map<ASTCDAssocSide, ASTCDAssociation> listAssociationMap,
                                    Map<ASTCDAssocSide, ASTCDAssociation> gemAssociationMap){
    List<ASTCDAttribute> listAttributes = getListAttributes(domainClass);
    List<ASTCDAttribute> attributes = getNormalAttributes(domainClass);
    Map<ASTCDAttribute, String> optMap = Maps.newLinkedHashMap();
    getOptionalAttributes(domainClass).forEach(e -> optMap.put(e,
        SQLSchemaGenerator.convertAttributeName(e.getName(), SQLSchemaGenerator.AttributeType.QUOTED)));

    getCD4C().addMethod(daoClass, "umlp.dao.SelectAllIdsInit",
        SQLSchemaGenerator.convertClassName(domainClass.getName(), SQLSchemaGenerator.AttributeType.ESCAPED_QUOTED), domainClass.getName(),
        attributes.stream().map(a -> SQLSchemaGenerator.convertAttributeName(a.getName(), SQLSchemaGenerator.AttributeType.ESCAPED_QUOTED)).collect(Collectors.toList()));

    Map<ASTCDAttribute, String> attributeMap = Maps.newLinkedHashMap();
    attributes.forEach(e -> attributeMap.put(e,
        SQLSchemaGenerator.convertAttributeName(e.getName(), SQLSchemaGenerator.AttributeType.QUOTED)));
    getCD4C().addMethod(daoClass, "umlp.dao.SelectAllIds", domainClass.getName(), cdName, attributeMap,
        optMap, listAttributes, listAssociationMap, gemAssociationMap);
  }

  protected void unpackMethod(ASTCDClass daoClass, ASTCDClass domainClass,
                                 Map<ASTCDAssocSide, ASTCDAssociation> listAssociationMap,
                                 Map<ASTCDAssocSide, ASTCDAssociation> gemAssociationMap){
    List<ASTCDAttribute> listAttributes = getListAttributes(domainClass);
    List<ASTCDAttribute> attributes = getNormalAttributes(domainClass);
    Map<ASTCDAttribute, String> optMap = Maps.newLinkedHashMap();
    getOptionalAttributes(domainClass).forEach(e -> optMap.put(e,
        SQLSchemaGenerator.convertAttributeName(e.getName(), SQLSchemaGenerator.AttributeType.QUOTED)));

    Map<ASTCDAttribute, String> attributeMap = Maps.newLinkedHashMap();
    attributes.forEach(e -> attributeMap.put(e,
        SQLSchemaGenerator.convertAttributeName(e.getName(), SQLSchemaGenerator.AttributeType.QUOTED)));
    getCD4C().addMethod(daoClass, "umlp.dao.Unpack", domainClass.getName(), cdName, attributeMap,
        optMap, listAttributes, listAssociationMap, gemAssociationMap);
  }

  protected void insertMethod(ASTCDClass daoClass, ASTCDClass domainClass,
                            Map<ASTCDAssocSide, ASTCDAssociation> listAssociationMap,
                            Map<ASTCDAssocSide, ASTCDAssociation> gemAssociationMap) {
    List<ASTCDAttribute> attributes = getNormalAttributes(domainClass);
    List<ASTCDAttribute> listAttributes = getListAttributes(domainClass);
    List<ASTCDAttribute> optionalAttributes = getOptionalAttributes(domainClass);

    getCD4C().addMethod(daoClass, "umlp.dao.InsertInit",SQLSchemaGenerator.convertClassName(domainClass.getName(), SQLSchemaGenerator.AttributeType.ESCAPED_QUOTED)
        ,domainClass.getName(), attributes.stream().map(a -> SQLSchemaGenerator.convertAttributeName(a.getName(), SQLSchemaGenerator.AttributeType.ESCAPED_QUOTED)).collect(Collectors.toList()));
    getCD4C().addMethod(daoClass, "umlp.dao.Insert", domainClass.getName(), cdName, attributes,
        optionalAttributes , listAttributes, listAssociationMap, gemAssociationMap);
  }

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
  }

  static Map<ASTCDAssocSide, ASTCDAssociation> getOppositeSideToAssociationMap(ASTCDClass domainClass, List<ASTCDAssociation> associations) {
    List<ASTCDAssocSide> assocSides = CDSymbolTables.getAssociationsInHierarchy(domainClass).stream()
        .filter(a -> GenService.isVisible(a)).collect(Collectors.toList());
    Map<ASTCDAssocSide, ASTCDAssociation> assocSideToAssociation = new HashMap<>();
    for (ASTCDAssocSide assocSide : assocSides) {
      Optional<ASTCDAssociation> assocOfAssocSide = associations.stream().filter(a -> a.getRight().equals(assocSide)).findFirst();
      assocOfAssocSide.ifPresent(a -> assocSideToAssociation.put(assocSide, a));

      assocOfAssocSide = associations.stream().filter(a -> a.getLeft().equals(assocSide)).findFirst();
      assocOfAssocSide.ifPresent(a -> assocSideToAssociation.put(assocSide, a));
    }
    return assocSideToAssociation;
  }
}
