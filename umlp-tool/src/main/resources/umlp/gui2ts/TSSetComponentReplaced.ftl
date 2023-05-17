<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "attrType", "attr")}
<#assign attrName="value">
${cd4c.method("public void ${methodName}(${attrType} ${attrName})")}
    this.${attr}${attrName});