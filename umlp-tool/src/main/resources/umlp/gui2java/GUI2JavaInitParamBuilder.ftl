<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "init", "initList")}
<#assign genHelper = glex.getGlobalVar("genHelper")>

${cd4c.method("protected void ${init}()")}
this.${name} = ${manager}.${type?uncap_first}Builder();
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
