<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("concreteClasses")}
${cd4c.method("protected void _TESTING_clearCache()")}
<#list concreteClasses as cc>
${cc.name?uncap_first}s.clear();
</#list>
