<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superTypeNames", "attributes")}
${cd4c.method("public void ensureInsert${typeName}Created()")}
if(insert${typeName} == null) {
insert${typeName} = DatabaseAccessManager.prepareStatement("CREATE (o<#list superTypeNames as typeName>:${typeName?cap_first}</#list> <#t>
<#lt>{gemId: ? <#list attributes as attr>, ${attr.getName()}: ?</#list>})");
}
