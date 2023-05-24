<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "attrNameList")}
${cd4c.method("protected Optional<${command}> fromJsonObject(JsonObject jsonObject)")}

return new ${command}(
<#list attrNameList as attrName>
jsonObject.get<#if attrName?ends_with("Index")>Integer<#else>Long</#if>Member("${attrName}")<#if attrName?has_next>,</#if>
</#list>
);
