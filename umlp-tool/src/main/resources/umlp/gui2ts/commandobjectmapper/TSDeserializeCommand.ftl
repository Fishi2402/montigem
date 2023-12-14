<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superCdDeSers")}
${cd4c.method("protected Optional<Command<any>> _deserializeCommand(String cmdStr)")}

return this.commandDeSer.deserialize(cmdStr)
  <#list superCdDeSers as tmp>
    ?? this.${tmp}.deserialize(cmdStr)
  </#list>
;
