<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("returnType", "paramType", "domainType")}
${cd4c.method("protected " + returnType + " replySuccess(List<${paramType}> list)")}
  return ListResult.of(this.getId(), list,  "${domainType}");
