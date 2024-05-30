<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "params")}
<#assign paramString = genHelper.params2String(params)>

${cd4c.method("public ${type} ${name}(${paramString})")}
return null;
