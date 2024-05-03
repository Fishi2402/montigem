<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "superTypeNames", "attributes", "optionalAttributes")}
${cd4c.method("public void ensureInsert${typeName}Created()")}
if(insert${typeName} == null) {
insert${typeName} = DatabaseAccessManager.prepareStatement("CREATE (o<#list superTypeNames as superTypeName>:${superTypeName?cap_first}</#list> <#t>
<#lt>{gemId: ? <#list attributes as attr>, ${attr.getName()}: ? </#list> <#list optionalAttributes as attr>, ${attr.getName()}: ?</#list>})");
}
