<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "cdName", "attributes", "optionalAttributes", "listAttributes", "listAssocs", "gemAssocs")}
${cd4c.method("public void insert${typeName}(Collection<${typeName}> objectsToInsert)")}
<#assign statement="insert"+typeName>
ensureInsert${typeName}Created();
try{
  ${statement}.clearBatch();
  for(${typeName} o : objectsToInsert) {
    ${statement}.clearParameters();
    ${statement}.setLong(1, o.getGemId());
    <#list attributes as attr><#assign index = attr?index+2>
        ${includeArgs("umlp.dao.Set", statement, index, genService.getDBType(attr.getSymbol().getType()), "o."+genService.getGetterName(attr.getName(), attr.getSymbol().getType())+"()", false)};
    </#list>
    <#list optionalAttributes as attr><#assign index = index+1>
        ${includeArgs("umlp.dao.Set", statement, index, genService.getDBType(attr.getSymbol().getType()), "o."+genService.getGetterName(attr.getName(), attr.getSymbol().getType())+"()", true)};
    </#list>
    ${statement}.addBatch();
    <#list listAttributes as attr>
      // TODO replace this with Commands
      update${attr.getName()?cap_first}(o.getGemId(), o.get${attr.getName()?cap_first}());
    </#list>
  }
  ${statement}.executeBatch();
  } catch (SQLException e){
    throw new RuntimeException(e);
  }
