<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "params")}
<#assign mcType = type.printType()>
<#assign paramString = genHelper.params2String(params)>

${cd4c.method("public ${mcType} ${name}(${paramString})")}
return null;
