<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("returnType", "domainType")}
${cd4c.method("protected " + returnType + " replyFail(umlp.backendrte.common.LoadingState loadingState)")}
  return new ${returnType}(this.getId(), loadingState, "${domainType}");
