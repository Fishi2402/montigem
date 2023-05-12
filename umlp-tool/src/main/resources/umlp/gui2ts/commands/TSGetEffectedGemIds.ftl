<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("gemIds")}
${cd4c.method("public Collection<Long> getEffectedGemIds()")}

return [<#list gemIds as gemId>${gemId}<#if gemId?has_next>, </#if></#list>];

