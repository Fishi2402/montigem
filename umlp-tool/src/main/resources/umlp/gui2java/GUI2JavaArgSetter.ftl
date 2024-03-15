<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "nullCheck", "setterExpression", "args")}
${cd4c.method("protected ${type} get${name?cap_first}(${args})")}
if (${nullCheck}) {
  return null;
}
return ${setterExpression};
