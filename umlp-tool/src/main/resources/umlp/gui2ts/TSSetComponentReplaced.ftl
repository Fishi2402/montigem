<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "attrType", "attr", "args", "argValueHolder", "condition")}
${cd4c.method("public void ${methodName}(${args})")}
<#if condition?has_content>
  if (${condition}) {
    return null;
  }
</#if>
<#if attr != "null">
  ${argValueHolder}
  ${attr};
</#if>
