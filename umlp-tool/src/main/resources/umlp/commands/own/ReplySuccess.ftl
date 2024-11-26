<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("returnType", "paramType", "domainType")}
${cd4c.method("protected " + returnType + " replySuccess(${paramType} obj)")}
  return new ${returnType}(this.getId(), obj, "${domainType}");
