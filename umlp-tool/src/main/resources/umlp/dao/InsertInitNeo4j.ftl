<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "superTypeNames", "attributes", "optionalAttributes")}
${cd4c.method("public void ensureInsert${typeName}Created()")}
if(insert${typeName} == null) {
insert${typeName} = "CREATE (o<#list superTypeNames as superTypeName>:${superTypeName?cap_first}</#list> <#t>
<#lt>{gemId: $1 <#assign index = 1><#list attributes as attr><#assign index = index+1>, ${attr.getName()}: $${index} </#list> <#list optionalAttributes as attr><#assign index = index+1>, ${attr.getName()}: $${index}</#list>})";
}
