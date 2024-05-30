<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "getterExpression", "keys", "initList")}
<#assign paramString = genHelper.params2String(keys)>
<#assign tmpVar = "tmp">

${cd4c.method("public void init${name?cap_first}(${paramString})")}
${getterExpression}.loadAll().then(${tmpVar} => {
  ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
});