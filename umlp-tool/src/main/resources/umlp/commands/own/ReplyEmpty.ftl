<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("returnType")}
${cd4c.method("protected " + returnType + " replyEmpty()")}
  return this.replyFail(umlp.backendrte.common.LoadingState.EMPTY);
