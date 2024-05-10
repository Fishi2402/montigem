<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "builder", "initList")}
<#assign genHelper = glex.getGlobalVar("genHelper")>

${cd4c.method("protected void ${builder}()")}
this.${name} = ${manager}.${type?uncap_first}Builder();
${tc.includeArgs("umlp.gui2java.GUI2JavaCallInit", [initList])}
