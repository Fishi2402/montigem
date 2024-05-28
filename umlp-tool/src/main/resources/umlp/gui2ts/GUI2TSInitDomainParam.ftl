<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "name", "manager", "builder", "initList")}
<#assign type = cdPrinter.printType(mcType)>
<#assign tmpVar = "tmp">

${cd4c.method("protected void ${builder}()")}
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