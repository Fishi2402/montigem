<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "attributes")}
${cd4c.method("public void ensureInsert${typeName}Created()")}
  if(insert${typeName} == null) {
    insert${typeName} = DatabaseAccessManager.prepareStatement("INSERT INTO ${typeName?uncap_first} <#t>
    <#lt>(id <#list attributes as attr>, ${attr.getName()}</#list>) VALUES (? <#list attributes as attr>, ?</#list>)");
  }
