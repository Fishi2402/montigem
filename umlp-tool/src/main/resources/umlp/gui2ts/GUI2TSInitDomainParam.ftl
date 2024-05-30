<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "id", "init", "initList")}
<#assign tmpVar = "tmp">

${cd4c.method("protected void ${init}()")}
  <#-- if attr set early (no/fast data loading) -->
  if (this.${name} == null) {
    if (this.${id} != null) {
      ${manager}.get${type}Sure(this.${id}).then(${tmpVar} => {
        if (this.${name} == null) {
          this.${name} = ${tmpVar};
        }
        ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
      });
    }
    else {
      ${manager}.${type?uncap_first}Builder().then(${tmpVar} => {
        if (this.${name} == null) {
          this.${name} = ${tmpVar};
        }
        ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
      });
    }
  }