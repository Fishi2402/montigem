<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("returnType", "domainType")}
${cd4c.method("protected " + returnType + " replyFail()")}
  return ListResult.error(this.getId(), "${domainType}");
