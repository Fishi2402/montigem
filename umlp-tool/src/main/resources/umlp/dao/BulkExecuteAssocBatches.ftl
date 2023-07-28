<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "cdName", "attributes", "optionalAttributes", "listAttributes", "listAssocs", "gemAssocs")}
${cd4c.method("public void executeBatchAddAssocs${typeName}()")}
// TODO replace this with Commands
  <#list listAssocs as assocSide, assoc>
    assoc${assoc.getName()?cap_first}DAO.executeBatchAdd${assoc.getName()?cap_first}();
  </#list>
  <#list gemAssocs as assocSide, assoc>
    assoc${assoc.getName()?cap_first}DAO.executeBatchAdd${assoc.getName()?cap_first}();
  </#list>

