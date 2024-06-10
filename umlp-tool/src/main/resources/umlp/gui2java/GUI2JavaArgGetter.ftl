<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "nullCheck", "getterExpression", "params")}
<#assign mcType = type.printType()>
<#assign paramString = genHelper.params2String(params)>

${cd4c.method("public ${mcType} get${name?cap_first}(${paramString})")}
if (${nullCheck}) {
  return null;
}
return ${getterExpression};
