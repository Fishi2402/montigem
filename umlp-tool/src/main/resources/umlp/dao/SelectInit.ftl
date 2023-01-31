<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tableName","typeName", "attributes")}
${cd4c.method("public void ensureSelect${typeName}Created()")}
  if(select${typeName} == null) {
    select${typeName} = DatabaseAccessManager.prepareStatement("SELECT id <#list attributes as attrName>, ${attrName}</#list><#t>
    <#lt> FROM ${tableName} WHERE id = ?");
  }
