<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("concreteClasses")}
${cd4c.method("protected void _TESTING_clearCache()")}
<#list concreteClasses as cc>
known${cc.name}s.clear();
</#list>
