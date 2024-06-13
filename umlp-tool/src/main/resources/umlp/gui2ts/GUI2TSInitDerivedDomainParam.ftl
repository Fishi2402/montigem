<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "init", "initList")}
<#assign tsType = cdPrinter.printType(type)>
<#assign tmpVar = "tmp_$">

${cd4c.method("protected void ${init}()")}
  <#-- if attr set early (no/fast data loading) -->
  if (this.${name} === undefined) {
    ${manager}.get${tsType}Sure(-1).then(${tmpVar} => {
      if (this.${name} === undefined) {
        this.${name} = ${tmpVar};
      }
      ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
    });
  }
  else {
    ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
  }