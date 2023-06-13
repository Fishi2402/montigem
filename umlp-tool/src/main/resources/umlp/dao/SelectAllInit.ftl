<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tableName","typeName", "attributes")}
${cd4c.method("public void ensureSelectAll${typeName}Created()")}
  if(selectAll${typeName} == null) {
    selectAll${typeName} = DatabaseAccessManager.prepareStatement("SELECT id <#list attributes as attrName>, ${attrName}</#list><#t>
    <#lt> FROM ${tableName};");
  }
