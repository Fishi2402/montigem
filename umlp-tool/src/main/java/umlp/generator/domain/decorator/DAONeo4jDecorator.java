package umlp.generator.domain.decorator;

import com.google.common.collect.Maps;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDTypeTOP;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.db.sql.SQLSchemaGenerator;
import umlp.generator.util.UMLPSymbolTables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DAONeo4jDecorator extends DAODecorator {

  public static final String neo4J_SUFFIX = "Neo4J";
  private static final String neo4J_Template_SUFFIX="_Neo4j";

  public DAONeo4jDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected String getDAOClassName(ASTCDClass domainClass) {
    return domainClass.getName() + "DAO"+ neo4J_SUFFIX;
  }

  @Override
  protected String getDAOSpecificNameForAssoc(String assocName) {
    return "Assoc" + StringTransformations.capitalize(assocName) + "DAO"+neo4J_SUFFIX;
  }

  @Override
  protected void assocDAOMethods(ASTCDClass daoClass, ASTCDAssociation association) {
    addMethodsForAssociationDAO(daoClass, association);
    updateMethodsForAssociationDAO(daoClass, association);

    removeMethodForAssociationDAO(daoClass, association);
    if(association.getCDAssocDir().isDefinitiveNavigableRight()) {
      selectMethodForAssocDAO(daoClass, association.getName(), association.getLeft(), association.getRight());
      removeAllMethodForAssocDAO(daoClass, association.getLeft().getName(), association.getRight().getName(),
          CD4CodeMill.prettyPrint(association.getLeft().getMCQualifiedType(), false),
          CD4CodeMill.prettyPrint(association.getRight().getMCQualifiedType(), false)
      );
    }
    if(association.getCDAssocDir().isDefinitiveNavigableLeft()) {
      selectMethodForAssocDAO(daoClass, association.getName(), association.getRight(), association.getLeft());
      removeAllMethodForAssocDAO(daoClass, association.getRight().getName() ,association.getLeft().getName(),
          CD4CodeMill.prettyPrint(association.getRight().getMCQualifiedType(), false),
          CD4CodeMill.prettyPrint(association.getLeft().getMCQualifiedType(), false)
      );
    }
  }

  protected void removeAllMethodForAssocDAO(ASTCDClass daoClass, String startName, String endName,
                                            String startType, String endType) {
    getCD4C().addMethod(daoClass, "umlp.dao.associations.RemoveAllInitNeo4jForAssocDAO", startName,
        startName, endName, startType, endType);
    getCD4C().addMethod(daoClass, "umlp.dao.associations.RemoveAllForAssocDAO", startName);
  }

  public void removeMethodForAssociationDAO(ASTCDClass daoClass, ASTCDAssociation assoc) {
    ASTCDAssocSide assocStart = assoc.getLeft();
    ASTCDAssocSide assocEnd = assoc.getRight();

    getCD4C().addMethod(daoClass, "umlp.dao.associations.RemoveInitNeo4jForAssocDAO", assoc.getName(),
        assocStart.getName(),
        assocEnd.getName(),
        CD4CodeMill.prettyPrint(assocStart.getMCQualifiedType(), false),
        CD4CodeMill.prettyPrint(assocEnd.getMCQualifiedType(), false)
        );
    getCD4C().addMethod(daoClass, "umlp.dao.associations.RemoveForAssocDAO", assoc);
  }

  @Override
  protected void addMethodsForAssociationDAO(ASTCDClass daoClass, ASTCDAssociation assoc) {
    getCD4C().addMethod(daoClass, "umlp.dao.associations.AddInitNeo4jForAssocDAO", assoc.getName(),
            assoc.getLeft().getSymbol().getType().print(),
            assoc.getRight().getSymbol().getType().print(),
            assoc.getLeft().getName(), assoc.getRight().getName(), assoc.getCDAssocDir().isDefinitiveNavigableLeft(),
            assoc.getCDAssocDir().isDefinitiveNavigableRight());
    getCD4C().addMethod(daoClass, "umlp.dao.associations.AddForAssocDAO", assoc);
    getCD4C().addMethod(daoClass, "umlp.dao.associations.AddManyForAssocDAO", assoc);
    getCD4C().addMethod(daoClass, "umlp.dao.associations.AddManyToBatchForAssocDAO", assoc);
    getCD4C().addMethod(daoClass, "umlp.dao.associations.ExecuteAddBatchForAssocDAO", assoc);
  }

  @Override
  protected void selectAllIdsMethod(ASTCDClass daoClass, ASTCDClass domainClass,
                                    Map<ASTCDAssocSide, ASTCDAssociation> listAssociationMap,
                                    Map<ASTCDAssocSide, ASTCDAssociation> gemAssociationMap){
    List<ASTCDAttribute> listAttributes = getListAttributes(domainClass);
    List<ASTCDAttribute> attributes = getNormalAttributes(domainClass);
    Map<ASTCDAttribute, String> optMap = Maps.newLinkedHashMap();
    getOptionalAttributes(domainClass).forEach(e -> optMap.put(e,
        SQLSchemaGenerator.convertAttributeName(e.getName(), SQLSchemaGenerator.AttributeType.QUOTED)));

    getCD4C().addMethod(daoClass, "umlp.dao.SelectAllIdsInitNeo4j",
        SQLSchemaGenerator.convertClassName(domainClass.getName(), SQLSchemaGenerator.AttributeType.ESCAPED_QUOTED), domainClass.getName(),
        attributes.stream().map(a -> SQLSchemaGenerator.convertAttributeName(a.getName(), SQLSchemaGenerator.AttributeType.ESCAPED_QUOTED)).collect(Collectors.toList()));

    Map<ASTCDAttribute, String> attributeMap = Maps.newLinkedHashMap();
    attributes.forEach(e -> attributeMap.put(e,
        SQLSchemaGenerator.convertAttributeName(e.getName(), SQLSchemaGenerator.AttributeType.QUOTED)));
    getCD4C().addMethod(daoClass, "umlp.dao.SelectAllIds", domainClass.getName(), cdName, attributeMap,
        optMap, listAttributes, listAssociationMap, gemAssociationMap);
  }

  @Override
  protected void selectMethod(ASTCDClass daoClass, ASTCDClass domainClass,
                            Map<ASTCDAssocSide, ASTCDAssociation> listAssociationMap,
                            Map<ASTCDAssocSide, ASTCDAssociation> gemAssociationMap) {
    List<ASTCDAttribute> listAttributes = getListAttributes(domainClass);
    List<ASTCDAttribute> attributes = getNormalAttributes(domainClass);
    Map<ASTCDAttribute, String> optMap = Maps.newLinkedHashMap();
    getOptionalAttributes(domainClass).forEach(e -> optMap.put(e,
                    SQLSchemaGenerator.convertAttributeName(e.getName(), SQLSchemaGenerator.AttributeType.QUOTED)));

    getCD4C().addMethod(daoClass, "umlp.dao.SelectInitNeo4j", domainClass.getName(), attributes, getOptionalAttributes(domainClass));

    Map<ASTCDAttribute, String> attributeMap = Maps.newLinkedHashMap();
    attributes.forEach(e -> attributeMap.put(e, SQLSchemaGenerator.convertAttributeName(e.getName(), SQLSchemaGenerator.AttributeType.QUOTED)));
    getCD4C().addMethod(daoClass, "umlp.dao.Select", domainClass.getName(), cdName, attributeMap,
        optMap, listAttributes, listAssociationMap, gemAssociationMap);
  }

  @Override
  protected void insertMethods(ASTCDClass daoClass, ASTCDClass domainClass,
                            Map<ASTCDAssocSide, ASTCDAssociation> listAssociationMap,
                            Map<ASTCDAssocSide, ASTCDAssociation> gemAssociationMap) {
    List<ASTCDAttribute> attributes = getNormalAttributes(domainClass);
    List<ASTCDAttribute> listAttributes = getListAttributes(domainClass);
    List<ASTCDAttribute> optionalAttributes = getOptionalAttributes(domainClass);
    List<String> superTypeNamesInclSelf = new ArrayList<>(CDSymbolTables.getTransitiveSuperTypes(domainClass).stream().map(ASTCDTypeTOP::getName).collect(Collectors.toList()));
    superTypeNamesInclSelf.add(domainClass.getName());

    Map<String, String> assocToCorrectManagerMap =
        getAssocToSourceDefinitionCD(Stream.of(gemAssociationMap.values(), listAssociationMap.values()).flatMap(Collection::stream));

    getCD4C().addMethod(daoClass, "umlp.dao.InsertInitNeo4j", domainClass.getName(),
        superTypeNamesInclSelf, attributes, optionalAttributes);
    getCD4C().addMethod(daoClass, "umlp.dao.Insert", domainClass.getName(), cdName, attributes,
        optionalAttributes , listAttributes, listAssociationMap, gemAssociationMap, assocToCorrectManagerMap, neo4J_Template_SUFFIX);
    getCD4C().addMethod(daoClass, "umlp.dao.BulkInsert", domainClass.getName(), cdName, attributes,
        optionalAttributes , listAttributes, listAssociationMap, gemAssociationMap, neo4J_Template_SUFFIX);
    getCD4C().addMethod(daoClass, "umlp.dao.AddManyAssociationsToBatch", domainClass.getName(), cdName, attributes,
        optionalAttributes , listAttributes, listAssociationMap, gemAssociationMap, assocToCorrectManagerMap);
    getCD4C().addMethod(daoClass, "umlp.dao.ExecuteAssocBatches", domainClass.getName(), cdName, attributes,
        optionalAttributes , listAttributes, listAssociationMap, gemAssociationMap, assocToCorrectManagerMap);
  }

  @Override
  protected void selectInitMethodForListAssocDAO(ASTCDClass daoClass, String assocName, ASTCDAssocSide start, ASTCDAssocSide end, String numerus) {
    getCD4C().addMethod(daoClass, "umlp.dao.associations.SelectInitNeo4jForAssocDAO", start.getName(),
        end.getName(), CD4CodeMill.prettyPrint(start.getMCQualifiedType(), false), assocName, numerus);
  }

  @Override
  protected void deleteMethod(ASTCDClass daoClass, ASTCDClass domainClass, Map<ASTCDAssocSide, ASTCDAssociation> assocSideToAssoc) {
    getCD4C().addMethod(daoClass, "umlp.dao.DeleteNeo4j", domainClass.getName(), assocSideToAssoc);
  }

  @Override
  protected void updateAttrMethod(ASTCDClass doaClass, ASTCDClass domainClass){
    CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
        .filter(a -> !a.getModifier().isDerived() && !GenService.isList(a) && !GenService.isOptional(a)).forEach(attr ->
            getCD4C().addMethod(doaClass, "umlp.dao.UpdateAttrNeo4j", domainClass.getName(), attr.getName(),
                    attr.printType(), GenService.getDBType(attr.getSymbol().getType()), false));
  }

  @Override
  protected void updateOptionalAttrMethod(ASTCDClass doaClass, ASTCDClass domainClass){
    CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
        .filter(a -> !a.getModifier().isDerived() && GenService.isOptional(a)).forEach(attr ->
            getCD4C().addMethod(doaClass, "umlp.dao.UpdateAttrNeo4j", domainClass.getName(), attr.getName(),
                GenService.createType(attr).printType(), GenService.getDBType(attr.getSymbol().getType()), true));
  }

  @Override
  protected void updateAndSelectListAttrMethods(ASTCDClass doaClass, ASTCDClass domainClass) {
    CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
        .filter(a -> !a.getModifier().isDerived() && GenService.isList(a)).forEach(attr -> {
              GenService.createType(attr);
              String innerType = GenService.getInnerType(GenService.createType(attr));
              String tableName = SQLSchemaGenerator.convertClassName(domainClass.getName(), SQLSchemaGenerator.AttributeType.PLAIN)+
                  "$"+SQLSchemaGenerator.convertAttributeName(attr.getName(), SQLSchemaGenerator.AttributeType.PLAIN);
              String dbType = GenService.getDBType(attr.getSymbol().getType());
              getCD4C().addMethod(doaClass, "umlp.dao.UpdateListAttrNeo4j", domainClass.getName(), tableName, attr, innerType, dbType);
              getCD4C().addMethod(doaClass, "umlp.dao.SelectListAttrNeo4j", domainClass.getName(), tableName, attr, innerType, dbType);
            }
        );
  }

  protected void updateMethodsForAssociationDAO(ASTCDClass daoClass, ASTCDAssociation association) {
    String leftType = association.getLeft().getMCQualifiedType().printType();
    String rightType = association.getRight().getMCQualifiedType().printType();

    String leftName = association.getLeft().getName();
    String rightName = association.getRight().getName();

    boolean navigableRight = association.getCDAssocDir().isDefinitiveNavigableRight();
    boolean navigableLeft = association.getCDAssocDir().isDefinitiveNavigableLeft();

    if(navigableLeft) {
      getCD4C().addMethod(daoClass, "umlp.dao.associations.UpdateInitNeo4jForAssocDAO", association.getName(),
          rightName, leftName, rightType, leftType, navigableLeft, navigableRight);
      getCD4C().addMethod(daoClass, "umlp.dao.associations.UpdateNeo4jForAssocDAO", rightName, leftName);
    }
    if(navigableRight) {
      getCD4C().addMethod(daoClass, "umlp.dao.associations.UpdateInitNeo4jForAssocDAO", association.getName(),
          leftName, rightName, leftType, rightType, navigableLeft, navigableRight);
      getCD4C().addMethod(daoClass, "umlp.dao.associations.UpdateNeo4jForAssocDAO", leftName, rightName);
    }

  }

  /**
   * See method in super class. We do not want kebab-case for attributes as in the SQL variant
   */
  @Override
  protected void unpackMethod(ASTCDClass daoClass, ASTCDClass domainClass,
                              Map<ASTCDAssocSide, ASTCDAssociation> listAssociationMap,
                              Map<ASTCDAssocSide, ASTCDAssociation> gemAssociationMap) {
    List<ASTCDAttribute> listAttributes = getListAttributes(domainClass);
    List<ASTCDAttribute> attributes = getNormalAttributes(domainClass);
    Map<ASTCDAttribute, String> optMap = Maps.newLinkedHashMap();
    getOptionalAttributes(domainClass).forEach(e -> optMap.put(e, "\"" + e.getName() + "\""));

    Map<String, String> assocToCorrectManagerMap =
        getAssocToSourceDefinitionCD(Stream.of(gemAssociationMap.values(), listAssociationMap.values()).flatMap(Collection::stream));

    Map<String, String> assocToCorrectDAOFactoryMap =
        getAssocToTargetDefinitionCD(Stream.of(gemAssociationMap.values(), listAssociationMap.values()).flatMap(Collection::stream));

    Map<ASTCDAttribute, String> attributeMap = Maps.newLinkedHashMap();
    attributes.forEach(e -> attributeMap.put(e, "\"" + e.getName() + "\""));
    getCD4C().addMethod(daoClass, "umlp.dao.Unpack", domainClass.getName(), cdName, attributeMap,
        optMap, listAttributes, listAssociationMap, gemAssociationMap, assocToCorrectManagerMap, assocToCorrectDAOFactoryMap,neo4J_Template_SUFFIX);
  }
}
