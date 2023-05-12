<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "attrType", "attrName")}
${cd4c.method("public void ${methodName}(${attrType} ${attrName})")}
    this.set${attrName?cap_first}(${attrName});