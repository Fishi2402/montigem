<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tableName", "typeName", "attributes", "optionalAttributes")}
${cd4c.method("public void ensureInsert${typeName}Created()")}

    insert${typeName} = "INSERT INTO ${tableName} <#t>
    <#lt>(id <#list attributes as attrName>, ${attrName}</#list> <#list optionalAttributes as attrName>, ${attrName}</#list>) VALUES (? <#list attributes as attr>, ?</#list> <#list optionalAttributes as attr>, ?</#list>)";

