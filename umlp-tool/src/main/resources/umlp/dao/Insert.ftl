<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "cdName", "attributes", "optionalAttributes", "listAttributes", "listAssocs", "gemAssocs")}
${cd4c.method("public void insert${typeName}(${typeName} o)")}
  ensureInsert${typeName}Created();
  try{
    insert${typeName}.clearParameters();
    insert${typeName}.setLong(1, o.getGemId());
    <#list attributes as attr><#assign index = attr?index+2>
    ${includeArgs("umlp.dao.insert." + attr.printType()?replace(".", "_"), typeName, index, attr)}
    </#list>
    <#list optionalAttributes as attr, innerType><#assign index = index+1>
    <#-- TODO AHe: also with extenable templates! -->
    insert${typeName}.set${innerType?cap_first}(${index}, o.get${attr.getName()?cap_first}().orElse(null));
    </#list>
    insert${typeName}.execute();
    <#list listAttributes as attr>
    update${attr.getName()?cap_first}(o.getGemId(), o.get${attr.getName()?cap_first}());
    </#list>

    List<Long> iDs;
    <#list listAssocs as assocSide, assoc>
    iDs = o._internal_get${assocSide.getName()?cap_first}GemList().getIDs();
    for(int i = 0; i < iDs.size(); i++) {
      Long id = iDs.get(i);
      <#if assocSide.isLeft()>
        assoc${assoc.getName()?cap_first}DAO.add${assoc.getName()?cap_first}(id, o.getGemId(), i);
      <#else>
        assoc${assoc.getName()?cap_first}DAO.add${assoc.getName()?cap_first}(o.getGemId(), id, i);
      </#if>
    }
    </#list>
    <#list gemAssocs as assocSide, assoc>
    assoc${assoc.getName()?cap_first}DAO.add${assoc.getName()?cap_first}(o.getGemId(), o.get${assocSide.getName()?cap_first}Gem().getGemId(),0);
    </#list>
  } catch (SQLException e){
    throw new RuntimeException(e);
  }