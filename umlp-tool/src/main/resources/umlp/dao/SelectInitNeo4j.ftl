<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "attributes")}
${cd4c.method("public void ensureSelect${typeName}Created()")}
if(select${typeName} == null) {
select${typeName} = DatabaseAccessManager.prepareStatement("MATCH(o:${typeName}) WHERE (o.gemId=?) RETURN<#t>
<#lt> o.gemId as gemId <#list attributes as attr>, o.${attr.getName()} as ${attr.getName()}</#list>");
}
