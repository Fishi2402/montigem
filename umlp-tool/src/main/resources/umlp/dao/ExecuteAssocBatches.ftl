<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "cdName", "attributes", "optionalAttributes", "listAttributes", "listAssocs", "gemAssocs", "assocsToDefinitionDiagramMap")}
${cd4c.method("public void executeAssocBatches${typeName}()")}
// TODO replace this with Commands; batches is JDBC specific so we cast
  <#list listAssocs as assocSide, assoc>
    ((Assoc${assoc.getName()?cap_first}DAO) ${assocsToDefinitionDiagramMap[assoc.getName()]}DAOFactory.getAssoc${assoc.getName()?cap_first}DAO())
      .executeBatchAdd${assoc.getName()?cap_first}();
  </#list>
  <#list gemAssocs as assocSide, assoc>
    ((Assoc${assoc.getName()?cap_first}DAO) ${assocsToDefinitionDiagramMap[assoc.getName()]}DAOFactory.getAssoc${assoc.getName()?cap_first}DAO())
      .executeBatchAdd${assoc.getName()?cap_first}();
  </#list>

