<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "providerName", "attrList")}
${cd4c.method("public ${name} invent${name}()")}

Log.trace("Inventing ${name}", "DummyProvider");
// Der Builder fuegt das Objekt zum ${providerName}Manager hinzu
return ${providerName}Manager
  .${name?uncap_first}Builder()
<#list attrList as attrName>
  .${attrName}(invent${name}${attrName?cap_first}())
</#list>
  .build().get();