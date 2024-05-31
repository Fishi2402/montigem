<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "nullCheck", "setterExpression", "conversion", "params")}
<#assign mcType = type.printType()>
<#assign paramString = genHelper.params2String(mcType, "val_$", params)>

${cd4c.method("public void set${name?cap_first}(${paramString})")}
if (${nullCheck}) {
  return;
}
${conversion};
${setterExpression};