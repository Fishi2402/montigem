package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesFullPrettyPrinter;
import umlp.generator.db.sql.SQLSchemaGenerator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DAONeo4jDecorator extends DAODecorator {

  public DAONeo4jDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
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

  @Override
  protected void addMethodForAssociationDAO(ASTCDClass daoClass, ASTCDAssociation assoc) {
    getCD4C().addMethod(daoClass, "umlp.dao.associations.AddInitNeo4jForAssocDAO", assoc.getName(),
        assoc.getLeft().getMCQualifiedType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())),
        assoc.getRight().getMCQualifiedType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())),
        assoc.getLeft().getName(), assoc.getRight().getName(), assoc.getCDAssocDir().isDefinitiveNavigableLeft(),
        assoc.getCDAssocDir().isDefinitiveNavigableRight());
    getCD4C().addMethod(daoClass, "umlp.dao.associations.AddForAssocDAO", assoc);

  }

  @Override
  protected void selectMethod(ASTCDClass daoClass, ASTCDClass domainClass,
                            Map<ASTCDAssocSide, ASTCDAssociation> listAssociationMap,
                            Map<ASTCDAssocSide, ASTCDAssociation> gemAssociationMap) {
    List<ASTCDAttribute> listAttributes = getListAttributes(domainClass);
    List<ASTCDAttribute> attributes = getNormalAttributes(domainClass);
    Map<ASTCDAttribute, String> optAttrToInnerType = getOptionalAttrsToInnerTypeMap(domainClass);

    getCD4C().addMethod(daoClass, "umlp.dao.SelectInitNeo4j", domainClass.getName(), attributes);

    Map<ASTCDAttribute, String> attributeTableName = attributes.stream().collect(Collectors.toMap(Function.identity(),
        ASTCDAttributeTOP::getName));
    getCD4C().addMethod(daoClass, "umlp.dao.Select", domainClass.getName(), cdName, attributeTableName,
        optAttrToInnerType, listAttributes, listAssociationMap, gemAssociationMap);
  }

  @Override
  protected void insertMethod(ASTCDClass daoClass, ASTCDClass domainClass,
                            Map<ASTCDAssocSide, ASTCDAssociation> listAssociationMap,
                            Map<ASTCDAssocSide, ASTCDAssociation> gemAssociationMap) {
    List<ASTCDAttribute> attributes = getNormalAttributes(domainClass);
    List<ASTCDAttribute> listAttributes = getListAttributes(domainClass);
    Map<ASTCDAttribute, String> optAttrToInnerType = getOptionalAttrsToInnerTypeMap(domainClass);

    getCD4C().addMethod(daoClass, "umlp.dao.InsertInitNeo4j",
        CDSymbolTables.getTransitiveSuperTypes(domainClass).stream().map(ASTCDTypeTOP::getName).collect(Collectors.toList()), attributes);
    getCD4C().addMethod(daoClass, "umlp.dao.Insert", domainClass.getName(), cdName, attributes,
        optAttrToInnerType , listAttributes, listAssociationMap, gemAssociationMap);
  }

  @Override
  protected void selectInitMethodForListAssocDAO(ASTCDClass daoClass, String assocName, ASTCDAssocSide start, ASTCDAssocSide end, String numerus) {
    getCD4C().addMethod(daoClass, "umlp.dao.associations.SelectInitNeo4jForAssocDAO", start.getName(),
        end.getName(), new MCSimpleGenericTypesFullPrettyPrinter(new IndentPrinter()).prettyprint(start.getMCQualifiedType()), assocName, numerus);
  }

  @Override
  protected void deleteMethod(ASTCDClass daoClass, ASTCDClass domainClass, Map<ASTCDAssocSide, ASTCDAssociation> assocSideToAssoc) {
    getCD4C().addMethod(daoClass, "umlp.dao.DeleteNeo4j", domainClass.getName(), assocSideToAssoc);
  }

  @Override
  protected void updateAttrMethod(ASTCDClass doaClass, ASTCDClass domainClass){
    CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
        .filter(a -> !a.getModifier().isDerived() && !GenService.isList(a) && !GenService.isOptional(a)).forEach(attr ->
            getCD4C().addMethod(doaClass, "umlp.dao.UpdateAttrNeo4j", domainClass.getName(), attr.getName(), attr.printType()));
  }
}
