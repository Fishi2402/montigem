<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("concreteClasses", "singletonInstances")}
${cd4c.method("protected void _TESTING_clearCache()")}
<#list concreteClasses as cc>
this.${cc.name?uncap_first}s.clear();
</#list>
<#list singletonInstances as cc>
    this.${cc.name?uncap_first}Instance = null;
</#list>
