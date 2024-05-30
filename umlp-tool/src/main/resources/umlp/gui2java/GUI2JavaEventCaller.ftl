<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "callerExpression", "params")}
<#assign paramString = genHelper.params2String(params)>

${cd4c.method("public void ${name}(${paramString})")}
${callerExpression}.accept(null);