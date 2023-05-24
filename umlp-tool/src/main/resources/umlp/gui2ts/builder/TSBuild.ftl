<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The build method is generated to allow object creation.
-->
${tc.signature("name", "managerName", "assocList")}
${cd4c.method("public ${name} build()")}

if (!this.buildFinished) {
  if (this.isBuildable()) {
<#list assocList as assoc>
    this._${assoc.getName()}.build();
</#list>
    if(!this.INTERNAL_deser_mode) {
      <#-- TODO AGe: enable manager -->
      // ${managerName}.push${name}(this);
    }
    this.${name?uncap_first}Observers.forEach(obs => obs.notifyBuild(this)); //+ an initial notification
    this.buildFinished = true;
    return this;
  }
}
return null;