<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "attrType", "attr")}
<#assign attrName="value">
${cd4c.method("public void ${methodName}(${attrType} ${attrName})")}
    set${attr?cap_first}(${attrName});