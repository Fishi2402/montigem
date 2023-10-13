${tc.signature("domainVar", "domainVarType")}
<#assign useJsweet = glex.getGlobalVar("UMLPGeneratorConfig").useJsweet>
${cd4c.method("protected void ${domainVarType?uncap_first}Added(${domainVarType} o)") }
this.${domainVar}._internal_add(this.${domainVar}.size(), <#if useJsweet>new UMLPArrayList(</#if>[o.getGemId()]<#if useJsweet>)</#if>);
