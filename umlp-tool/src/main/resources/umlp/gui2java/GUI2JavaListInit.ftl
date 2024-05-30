<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "listItems", "initList")}
<#assign tmpVar = "tmp">

${cd4c.method("protected void init${name?cap_first}()")}
${type} ${tmpVar} = new ArrayList<>();
<#list listItems as item>
${tmpVar}.add(${item});
</#list>

this.${name} = ${tmpVar};
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}