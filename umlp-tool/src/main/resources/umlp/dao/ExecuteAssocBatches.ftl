<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "cdName", "attributes", "optionalAttributes", "listAttributes", "listAssocs", "gemAssocs")}
${cd4c.method("public void executeAssocBatches${typeName}()")}
// TODO replace this with Commands
  <#list listAssocs as assocSide, assoc>
    ${cdName}DAOFactory.getAssoc${assoc.getName()?cap_first}DAO().executeBatchAdd${assoc.getName()?cap_first}();
  </#list>
  <#list gemAssocs as assocSide, assoc>
    ${cdName}DAOFactory.getAssoc${assoc.getName()?cap_first}DAO().executeBatchAdd${assoc.getName()?cap_first}();
  </#list>

