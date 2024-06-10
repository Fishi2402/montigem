<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "id", "init", "initList")}
<#assign tsType = cdPrinter.printType(type)>
<#assign tmpVar = "tmp_$">

${cd4c.method("protected void ${init}()")}
  <#-- if attr set early (no/fast data loading) -->
  if (this.${name} == null) {
    if (this.${id} != null) {
      ${manager}.get${tsType}Sure(this.${id}).then(${tmpVar} => {
        if (this.${name} == null) {
          this.${name} = ${tmpVar};
        }
        ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
      });
    }
    else {
      ${manager}.${tsType?uncap_first}Builder().then(${tmpVar} => {
        if (this.${name} == null) {
          this.${name} = ${tmpVar};
        }
        ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
      });
    }
  }