<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "attrType", "attr", "args", "condition")}
<#assign attrName="value">
${cd4c.method("public void ${methodName}(${args})")}
<#if condition?has_content>
  if (!${condition}) {
    return null;
  }
</#if>
    ${attr}${attrName});