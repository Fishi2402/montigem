${tc.signature("domainVar", "domainVarType")}
<#assign useJsweet = glex.getGlobalVar("UMLPGeneratorConfig").useJsweet>
${cd4c.method("protected void ${domainVarType?uncap_first}Deleted(number gemId)") }

let index = this.${domainVar}.indexOfId(gemId);

if(index >= 0){
  this.${domainVar}._internal_remove(<#if !useJsweet>new Set<number>(</#if>[index]<#if !useJsweet>)</#if>);
}
