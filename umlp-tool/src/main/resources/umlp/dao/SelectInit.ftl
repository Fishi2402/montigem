<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "attributes")}
${cd4c.method("public void ensureSelect${typeName}Created()")}
  if(select${typeName} == null) {
    select${typeName} = DatabaseAccessManager.prepareStatement("SELECT id <#list attributes as attr>, ${attr.getName()}</#list><#t>
    <#lt> FROM ${typeName} WHERE id = ?");
  }
