<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "cdName", "attributes", "optionalAttributes", "listAttributes", "listAssocs", "gemAssocs", "assocsToDefinitionDiagramMap")}
${cd4c.method("public void executeAssocBatches${typeName}()")}
// TODO replace this with Commands
  <#list listAssocs as assocSide, assoc>
    ${assocsToDefinitionDiagramMap[assoc.getName()]}DAOFactory.getAssoc${assoc.getName()?cap_first}DAO().executeBatchAdd${assoc.getName()?cap_first}();
  </#list>
  <#list gemAssocs as assocSide, assoc>
    ${assocsToDefinitionDiagramMap[assoc.getName()]}DAOFactory.getAssoc${assoc.getName()?cap_first}DAO().executeBatchAdd${assoc.getName()?cap_first}();
  </#list>

