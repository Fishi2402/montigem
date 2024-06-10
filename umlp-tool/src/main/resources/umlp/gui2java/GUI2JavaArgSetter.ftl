<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "nullCheck", "setterExpression", "params")}
<#assign mcType = type.printType()>
<#assign arg = "val_$">
<#assign paramString = genHelper.params2String(mcType, arg, params)>

${cd4c.method("public void set${name?cap_first}(${paramString})")}
if (${nullCheck}) {
  return;
}
${setterExpression};
