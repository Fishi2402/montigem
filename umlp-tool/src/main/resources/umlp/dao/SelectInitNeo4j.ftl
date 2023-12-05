<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "attributes", "optionalAttributes")}
${cd4c.method("public void ensureSelect${typeName}Created()")}
if(select${typeName} == null) {
select${typeName} = DatabaseAccessManager.prepareStatement("MATCH(o:${typeName}) WHERE (o.gemId=?) RETURN<#t>
<#lt> o.gemId as id <#list attributes as attr>, o.${attr.getName()} as ${attr.getName()}</#list><#rt>
                    <#lt><#list optionalAttributes as attr>, o.${attr.getName()} as ${attr.getName()}</#list>");
}
