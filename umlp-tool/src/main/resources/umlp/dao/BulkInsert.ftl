<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "cdName", "attributes", "optionalAttributes", "listAttributes", "listAssocs", "gemAssocs", "neo4j")}
${cd4c.method("public Boolean insert${typeName}(Collection<${typeName}> objectsToInsert)")}
<#assign statement="insert"+typeName>
ensureInsert${typeName}Created();
synchronized(${statement}){
  try{
    DatabaseAccessManager.getConnection().setAutoCommit(false);
    ${statement}.clearBatch();
    for(${typeName} o : objectsToInsert) {
      ${statement}.clearParameters();
      ${statement}.setLong(1, o.getGemId());
      <#assign index = 1>
      <#list attributes as attr><#assign index = index+1>
          ${includeArgs("umlp.dao.Set", statement, index, genService.getDBType(attr.getSymbol().getType()), "o."+genService.getGetterName(attr.getName(), attr.getSymbol().getType())+"()", false,neo4j)};
      </#list>
      <#list optionalAttributes as attr><#assign index = index+1>
          ${includeArgs("umlp.dao.Set", statement, index, genService.getDBType(attr.getSymbol().getType()), "o."+genService.getGetterName(attr.getName(), attr.getSymbol().getType())+"()", true,neo4j)};
      </#list>
      ${statement}.addBatch();
      <#list listAttributes as attr>
        // TODO replace this with Commands
        update${attr.getName()?cap_first}(o.getGemId(), o.get${attr.getName()?cap_first}List());
      </#list>
    }
    ${statement}.executeBatch();
    DatabaseAccessManager.getConnection().commit();
    DatabaseAccessManager.getConnection().setAutoCommit(true);
    return true;

    } catch (SQLException e){
      try {
        DatabaseAccessManager.getConnection().rollback();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    e.printStackTrace();
    return false;
    }

    finally{
      try {
        DatabaseAccessManager.getConnection().setAutoCommit(true);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
