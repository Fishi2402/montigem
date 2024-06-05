<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "getterExpression", "keys", "initList")}
<#assign paramString = genHelper.params2String(keys)>

${cd4c.method("public void init${name?cap_first}(${paramString})")}
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}