${tc.signature("domainVar", "domainVarType")}
${cd4c.method("protected void ${domainVarType?uncap_first}Deleted(number gemId)") }

let index = this.${domainVar}.indexOfId(gemId);

if(index >= 0){
  this.${domainVar}._internal_remove(new Set<number>([index]));
}
