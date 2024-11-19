<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("protected void replySuccess()")}
  return new StatusResult(this.getID(), true);
