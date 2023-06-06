<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "attrType", "variableName", "args", "condition")}
<#assign attrName="value">
${cd4c.method("public void ${methodName}(${attrType} ${attrName})")}
    ${variableName}(${attrName});
