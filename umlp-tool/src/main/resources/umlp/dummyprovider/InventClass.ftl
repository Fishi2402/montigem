<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "providerName", "attrList", "isSingleton")}
${cd4c.method("public ${name} invent${name}()")}

Log.trace("Inventing ${name}", "DummyProvider");
<#if isSingleton>
  // Singletons with partial init
  ${name?cap_first} _inst = ${providerName}Manager.get${name}();
  <#list attrList as attrName>
    _inst.set${attrName?cap_first}(invent${name}${attrName?cap_first}());
  </#list>
  return _inst;
<#else>
// Der Builder fuegt das Objekt zum ${providerName}Manager hinzu
return ${providerName}Manager
  .${name?uncap_first}Builder()
<#list attrList as attrName>
  .${attrName}(invent${name}${attrName?cap_first}())
</#list>
  .build().get();
</#if>
