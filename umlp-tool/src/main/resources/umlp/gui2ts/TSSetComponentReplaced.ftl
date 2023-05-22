<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "attrType", "attr", "condition")}
<#assign attrName="value">
${cd4c.method("public void ${methodName}(${attrType} ${attrName})")}
<#if condition?has_content>
  if (!this.${condition}) {
    return null;
  }
</#if>
    this.${attr}${attrName});