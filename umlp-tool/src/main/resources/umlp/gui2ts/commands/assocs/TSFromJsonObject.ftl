<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "attrNameList")}
${cd4c.method("protected Optional<${command}> fromJsonObject(any jsonObject)")}

return new ${command}(
<#list attrNameList as attrName>
jsonObject["${attrName}"]<#if attrName?has_next>,</#if>
</#list>
);
