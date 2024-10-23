<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "id", "init", "initCheck", "isAbstract", "initList")}
<#assign tsType = cdPrinter.printType(type)>
<#assign tmpVar = "tmp_$">

${cd4c.method("protected void ${init}()")}
  <#-- if attr set early (no/fast data loading) -->
  if (!this.${initCheck}) {
    if (this.${id} != null) {
      ${manager}.get${tsType}Sure(this.${id}).then(${tmpVar} => {
        this.${name} = ${tmpVar};
        ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
      });
    }
  <#if !isAbstract>
    else {
      ${manager}.${tsType?uncap_first}Builder().then(${tmpVar} => {
        this.${name} = ${tmpVar};
        ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
      });
    }
  </#if>
  }
  else {
    ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
  }
